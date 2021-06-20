package gg.nils.semanticrelease.api.versioncontrol;

import gg.nils.semanticrelease.api.Commit;
import gg.nils.semanticrelease.api.RawCommit;
import gg.nils.semanticrelease.api.Tag;
import gg.nils.semanticrelease.api.Version;

import java.util.List;

public interface VersionControlProvider {

    List<RawCommit> getRawCommits();

    List<RawCommit> getRawCommitsSince(Version version);

    List<Commit> getCommitsSince(Version version);

    List<Tag> getTags();

    Tag getLatestTag();

    Version getLatestVersion();

    Version getNextVersion();
}
