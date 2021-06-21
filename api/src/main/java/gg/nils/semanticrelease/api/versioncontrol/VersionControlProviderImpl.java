package gg.nils.semanticrelease.api.versioncontrol;

import gg.nils.semanticrelease.api.Branch;
import gg.nils.semanticrelease.api.Commit;
import gg.nils.semanticrelease.api.Tag;
import gg.nils.semanticrelease.api.Version;
import gg.nils.semanticrelease.api.calculator.NextVersionCalculator;
import gg.nils.semanticrelease.api.calculator.DefaultNextVersionCalculator;
import gg.nils.semanticrelease.api.config.SemanticReleaseConfig;
import gg.nils.semanticrelease.api.versioncontrol.converter.*;
import lombok.Setter;

import java.util.List;

public abstract class VersionControlProviderImpl implements VersionControlProvider {

    private final SemanticReleaseConfig config;

    @Setter
    private TagToVersionConverter tagToVersionConverter;

    @Setter
    private RawCommitToCommitConverter rawCommitToCommitConverter;

    @Setter
    private RawCommitsToCommitsConverter rawCommitsToCommitsConverter;

    @Setter
    private NextVersionCalculator nextVersionCalculator;

    protected VersionControlProviderImpl(SemanticReleaseConfig config) {
        this.config = config;

        this.tagToVersionConverter = new DefaultTagToVersionConverter();
        this.rawCommitToCommitConverter = new DefaultRawCommitToCommitConverter();
        this.rawCommitsToCommitsConverter = new DefaultRawCommitsToCommitsConverter(this.rawCommitToCommitConverter);
        this.nextVersionCalculator = new DefaultNextVersionCalculator(config);
    }

    @Override
    public List<Commit> getCommitsSince(Version version) {
        return this.rawCommitsToCommitsConverter.convert(this.getRawCommitsSince(version));
    }

    @Override
    public Tag getLatestTag() {
        List<Tag> tags = this.getTags();

        if (tags == null || tags.isEmpty())
            return null;

        return tags.get(tags.size() - 1);
    }

    @Override
    public Version getLatestVersion() {
        Tag latestTag = this.getLatestTag();

        if (latestTag == null)
            return this.config.getFirstVersion();

        return this.tagToVersionConverter.convert(latestTag);
    }

    @Override
    public Version getNextVersion() {
        Tag latestTag = this.getLatestTag();

        if (latestTag == null)
            return this.config.getFirstVersion();

        Version latestVersion = this.getLatestVersion();

        List<Commit> commits = this.getCommitsSince(latestVersion);

        return this.nextVersionCalculator.calculate(latestVersion, commits);
    }

    @Override
    public String getFullVersion() {
        Version nextVersion = this.getNextVersion();
        Branch currentBranch = this.getCurrentBranch();

        String version = "";

        String currentBranchName = currentBranch.getName();

        if(currentBranchName.equals("master") || currentBranchName.equals("main")) {
            version += nextVersion.toString();
        } else if(currentBranchName.equals("develop")) {
            version += nextVersion.toString() + "-SNAPSHOT";
        } else {
            version += nextVersion.toString() + "-" + currentBranchName.replaceAll("/", "-");
        }

        if(this.hasUncommittedChanges()) {
            version += "-DIRTY";
        }

        return version;
    }
}
