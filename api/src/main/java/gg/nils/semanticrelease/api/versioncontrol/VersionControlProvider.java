package gg.nils.semanticrelease.api.versioncontrol;

import gg.nils.semanticrelease.api.*;

import java.util.List;

public interface VersionControlProvider {

    List<RawCommit> getRawCommits();

    List<RawCommit> getRawCommitsSince(Version version);

    List<Commit> getCommitsSince(Version version);

    List<Tag> getTags();

    Tag getLatestTag();

    Branch getCurrentBranch();

    Version getLatestVersion();

    Version getNextVersion();
}
