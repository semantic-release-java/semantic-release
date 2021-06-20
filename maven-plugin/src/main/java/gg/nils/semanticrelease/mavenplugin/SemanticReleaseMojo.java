package gg.nils.semanticrelease.mavenplugin;

import gg.nils.semanticrelease.api.VersionImpl;
import gg.nils.semanticrelease.api.config.SemanticReleaseConfig;
import gg.nils.semanticrelease.api.config.SemanticReleaseConfigImpl;
import gg.nils.semanticrelease.api.versioncontrol.VersionControlProvider;
import gg.nils.semanticrelease.api.versioncontrol.git.GitVersionControlProvider;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.util.Collections;

@Mojo(name = SemanticReleaseMojo.GOAL, defaultPhase = LifecyclePhase.INITIALIZE, threadSafe = true)
public class SemanticReleaseMojo extends AbstractMojo {

    public static final String GOAL = "semantic-release";

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Log logger = this.getLog();

        try {
            FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder()
                    .findGitDir(this.project.getBasedir());

            if (repositoryBuilder.getGitDir() == null) {
                logger.error(".git dir not found!");
                return;
            }

            try (Repository repository = repositoryBuilder.build()) {
                Git git = new Git(repository);

                SemanticReleaseConfig config = SemanticReleaseConfigImpl.builder()
                        .firstVersion(new VersionImpl(null, 1, 0, 0))
                        .featureTypes(Collections.singletonList("feat"))
                        .patchTypes(Collections.singletonList("fix"))
                        .build();

                VersionControlProvider versionControlProvider = new GitVersionControlProvider(config, git);

                logger.info("Latest version: " + versionControlProvider.getLatestVersion());
                logger.info("Next version: " + versionControlProvider.getNextVersion());
            }
        } catch (Throwable t) {
            throw new MojoFailureException("semantic-release failed", t);
        }
    }
}
