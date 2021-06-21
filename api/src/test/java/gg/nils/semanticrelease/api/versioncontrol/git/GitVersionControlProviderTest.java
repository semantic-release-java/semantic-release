package gg.nils.semanticrelease.api.versioncontrol.git;

import gg.nils.semanticrelease.api.*;
import gg.nils.semanticrelease.api.config.SemanticReleaseConfig;
import gg.nils.semanticrelease.api.config.SemanticReleaseConfigImpl;
import gg.nils.semanticrelease.api.error.SemanticReleaseException;
import gg.nils.semanticrelease.api.versioncontrol.git.converter.*;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GitVersionControlProviderTest {

    @TempDir
    Path tempDir;

    SemanticReleaseConfig config;
    GitVersionControlProvider provider;
    GitVersionControlProvider notAGitDirProvider;

    @BeforeEach
    void setUp() {
        this.config = SemanticReleaseConfigImpl.builder()
                .firstVersion(new VersionImpl(null, 1, 0, 0))
                .featureTypes(Collections.singletonList("feat"))
                .patchTypes(Collections.singletonList("fix"))
                .build();

        System.out.println("TempDir: " + this.tempDir.toFile());

        {
            Git git = null;

            try {
                File gitDir = new File(this.tempDir.toFile(), "working");
                gitDir.mkdirs();

                git = Git.init().setInitialBranch("main").setDirectory(gitDir).call();

                File tmpFile = new File(gitDir, "temp.txt");
                tmpFile.createNewFile();

                {
                    FileWriter writer = new FileWriter(tmpFile);
                    writer.write("1");
                    writer.close();

                    git.add().addFilepattern("*").call();
                    git.commit().setMessage("Initial commit").setSign(false).call();
                }

                {
                    FileWriter writer = new FileWriter(tmpFile);
                    writer.write("2");
                    writer.close();

                    git.add().addFilepattern("*").call();
                    git.commit().setMessage("fix: 2").setSign(false).call();
                }

                {
                    FileWriter writer = new FileWriter(tmpFile);
                    writer.write("3");
                    writer.close();

                    git.add().addFilepattern("*").call();
                    RevCommit call = git.commit().setMessage("chore(release): 1.0.0").setSign(false).call();
                    git.tag().setName("1.0.0").setAnnotated(false).setObjectId(call).call();
                }

                {
                    FileWriter writer = new FileWriter(tmpFile);
                    writer.write("4");
                    writer.close();

                    git.add().addFilepattern("*").call();
                    git.commit().setMessage("fix: 4").setSign(false).call();
                }

                {
                    FileWriter writer = new FileWriter(tmpFile);
                    writer.write("5");
                    writer.close();

                    git.add().addFilepattern("*").call();
                    RevCommit call = git.commit().setMessage("chore(release): 1.0.1").setSign(false).call();
                    git.tag().setName("1.0.1").setAnnotated(false).setObjectId(call).call();
                }

                {
                    FileWriter writer = new FileWriter(tmpFile);
                    writer.write("6");
                    writer.close();

                    git.add().addFilepattern("*").call();
                    git.commit().setMessage("feat: 6").setSign(false).call();
                }
            } catch (GitAPIException | IOException e) {
                e.printStackTrace();
            }

            this.provider = new GitVersionControlProvider(this.config, git);
        }

        {
            Git git = null;

            try {
                git = Git.init().setDirectory(new File(this.tempDir.toFile(), "empty")).call();
            } catch (GitAPIException e) {
                e.printStackTrace();
            }

            this.notAGitDirProvider = new GitVersionControlProvider(this.config, git);
        }
    }

    @Test
    void getRawCommitsEmpty() {
        assertThrows(SemanticReleaseException.class, () -> {
            this.notAGitDirProvider.getRawCommits();
        });
    }

    @Test
    void getRawCommits() {
        assertEquals(this.provider.getRawCommits().size(), 6);
    }

    @Test
    void getRawCommitsSinceFail() {
        assertThrows(SemanticReleaseException.class, () -> {
            VersionImpl version = new VersionImpl(new TagImpl("0000000000000000000000000000000000000000", "Initial commit"), 1, 0, 0);

            this.notAGitDirProvider.getRawCommitsSince(version);
        });
    }

    @Test
    void getRawCommitsSinceTagNull() {
        assertThrows(SemanticReleaseException.class, () -> {
            VersionImpl version = new VersionImpl(null, 1, 0, 0);

            this.notAGitDirProvider.getRawCommitsSince(version);
        });
    }

    @Test
    void getRawCommitsSinceTag() {
        Version latestVersion = this.provider.getLatestVersion();

        System.out.println(latestVersion);

        List<RawCommit> rawCommitsSince = this.provider.getRawCommitsSince(latestVersion);

        assertEquals(rawCommitsSince.size(), 1);
    }

    @Test
    void getTagsEmpty() {
        List<Tag> tags = this.notAGitDirProvider.getTags();

        assertEquals(tags.size(), 0);
    }

    @Test
    void getTags() {
        List<Tag> tags = this.provider.getTags();

        assertEquals(tags.size(), 2);
    }

    @Test
    void setConverter() {
        DefaultGitRefToTagConverter gitRefToTagConverter = new DefaultGitRefToTagConverter();
        this.provider.setGitRefToTagConverter(gitRefToTagConverter);
        this.provider.setGitRefsToTagsConverter(new DefaultGitRefsToTagsConverter(gitRefToTagConverter));

        DefaultGitRevCommitToRawCommitConverter gitRevCommitToRawCommitConverter = new DefaultGitRevCommitToRawCommitConverter();
        this.provider.setGitRevCommitToRawCommitConverter(gitRevCommitToRawCommitConverter);
        this.provider.setGitRevCommitsToRawCommitsConverter(new DefaultGitRevCommitsToRawCommitsConverter(gitRevCommitToRawCommitConverter));
    }
}