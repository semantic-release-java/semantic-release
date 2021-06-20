package gg.nils.semanticrelease.api.versioncontrol;

import gg.nils.semanticrelease.api.Commit;
import gg.nils.semanticrelease.api.RawCommit;
import gg.nils.semanticrelease.api.Tag;
import gg.nils.semanticrelease.api.Version;
import gg.nils.semanticrelease.api.error.SemanticReleaseException;

import java.util.List;

public interface VersionControlProvider {

    Version tagToVersion(Tag tag);

    Commit rawCommitToCommit(RawCommit rawCommit);

    List<Commit> rawCommitsToCommits(List<RawCommit> rawCommits);

    List<RawCommit> getRawCommits() throws SemanticReleaseException;

    List<RawCommit> getRawCommitsSince(Version version) throws SemanticReleaseException;

    List<Commit> getCommitsSince(Version version) throws SemanticReleaseException;

    List<Tag> getTags() throws SemanticReleaseException;

    Tag getLatestTag();

    Version getLatestVersion();

    Version getNextVersion();
}
