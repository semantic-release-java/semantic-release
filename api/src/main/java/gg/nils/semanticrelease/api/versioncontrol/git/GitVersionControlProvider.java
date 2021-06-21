package gg.nils.semanticrelease.api.versioncontrol.git;

import gg.nils.semanticrelease.api.*;
import gg.nils.semanticrelease.api.config.SemanticReleaseConfig;
import gg.nils.semanticrelease.api.error.SemanticReleaseException;
import gg.nils.semanticrelease.api.versioncontrol.VersionControlProviderImpl;
import gg.nils.semanticrelease.api.versioncontrol.git.converter.*;
import lombok.Setter;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.IOException;
import java.util.List;

public class GitVersionControlProvider extends VersionControlProviderImpl {

    private final Git git;

    @Setter
    private GitRevCommitToRawCommitConverter gitRevCommitToRawCommitConverter;

    @Setter
    private GitRevCommitsToRawCommitsConverter gitRevCommitsToRawCommitsConverter;

    @Setter
    private GitRefToTagConverter gitRefToTagConverter;

    @Setter
    private GitRefsToTagsConverter gitRefsToTagsConverter;

    public GitVersionControlProvider(SemanticReleaseConfig config, Git git) {
        super(config);

        this.git = git;

        this.gitRevCommitToRawCommitConverter = new DefaultGitRevCommitToRawCommitConverter();
        this.gitRevCommitsToRawCommitsConverter = new DefaultGitRevCommitsToRawCommitsConverter(this.gitRevCommitToRawCommitConverter);
        this.gitRefToTagConverter = new DefaultGitRefToTagConverter();
        this.gitRefsToTagsConverter = new DefaultGitRefsToTagsConverter(this.gitRefToTagConverter);
    }

    @Override
    public List<RawCommit> getRawCommits() {
        try {
            Iterable<RevCommit> revCommits = this.git.log().call();

            return this.gitRevCommitsToRawCommitsConverter.convert(revCommits);
        } catch (GitAPIException e) {
            throw new SemanticReleaseException("Could not get commits", e);
        }
    }

    @Override
    public List<RawCommit> getRawCommitsSince(Version version) {
        // When tag is null there is probably no tag yet and SemanticReleaseConfig#getFirstVersion is used
        if(version.getTag() == null)
            return this.getRawCommits();

        try {
            ObjectId since = ObjectId.fromString(version.getTag().getCommitId());
            ObjectId until = this.git.getRepository().resolve("HEAD");

            Iterable<RevCommit> revCommits = this.git.log().addRange(since, until).call();

            return this.gitRevCommitsToRawCommitsConverter.convert(revCommits);
        } catch (GitAPIException|IOException e) {
            throw new SemanticReleaseException("Could not get commits since " + version, e);
        }
    }

    @Override
    public List<Tag> getTags() {
        try {
            List<Ref> refs = this.git.tagList().call();

            return this.gitRefsToTagsConverter.convert(refs);
        } catch (GitAPIException e) {
            throw new SemanticReleaseException("Could not get tags", e);
        }
    }
}
