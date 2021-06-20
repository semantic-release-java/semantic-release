package gg.nils.semanticrelease.api.versioncontrol.git;

import gg.nils.semanticrelease.api.*;
import gg.nils.semanticrelease.api.config.SemanticReleaseConfig;
import gg.nils.semanticrelease.api.error.SemanticReleaseException;
import gg.nils.semanticrelease.api.versioncontrol.VersionControlProviderImpl;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GitVersionControlProvider extends VersionControlProviderImpl {

    private final Git git;

    public GitVersionControlProvider(SemanticReleaseConfig config, Git git) {
        super(config);

        this.git = git;
    }

    private List<RawCommit> revCommitsToRawCommits(Iterable<RevCommit> revCommits) {
        List<RawCommit> rawCommits = new ArrayList<>();

        for (RevCommit revCommit : revCommits) {
            RawCommit rawCommit = RawCommitImpl.builder()
                    .commitId(revCommit.getId().getName())
                    .message(revCommit.getFullMessage())
                    .build();

            rawCommits.add(rawCommit);
        }

        return rawCommits;
    }

    @Override
    public List<RawCommit> getRawCommits() throws SemanticReleaseException {
        Iterable<RevCommit> revCommits;

        try {
            revCommits = this.git.log().call();
        } catch (GitAPIException e) {
            throw new SemanticReleaseException("Could not get commits", e);
        }

        return this.revCommitsToRawCommits(revCommits);
    }

    @Override
    public List<RawCommit> getRawCommitsSince(Version version) throws SemanticReleaseException {
        // When tag is null there is probably no tag yet and SemanticReleaseConfig#getFirstVersion is used
        if(version.getTag() == null)
            return this.getRawCommits();

        Iterable<RevCommit> revCommits;

        try {
            ObjectId since = ObjectId.fromString(version.getTag().getCommitId());
            ObjectId until = this.git.getRepository().resolve("HEAD");

            revCommits = this.git.log().addRange(since, until).call();
        } catch (GitAPIException|IOException e) {
            throw new SemanticReleaseException("Could not get commits since " + version, e);
        }

        return this.revCommitsToRawCommits(revCommits);
    }

    @Override
    public List<Tag> getTags() throws SemanticReleaseException {
        List<Ref> refs;

        try {
            refs = this.git.tagList().call();
        } catch (GitAPIException e) {
            throw new SemanticReleaseException("Could not get tags", e);
        }

        List<Tag> tags = new ArrayList<>();

        for (Ref ref : refs) {
            TagImpl tag = TagImpl.builder()
                    .commitId(ref.getObjectId().getName())
                    .name(ref.getName().replaceAll(Constants.R_TAGS, ""))
                    .build();

            tags.add(tag);
        }

        return tags;
    }
}
