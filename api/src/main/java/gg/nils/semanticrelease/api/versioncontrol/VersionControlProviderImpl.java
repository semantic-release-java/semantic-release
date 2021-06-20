package gg.nils.semanticrelease.api.versioncontrol;

import gg.nils.semanticrelease.api.*;
import gg.nils.semanticrelease.api.config.SemanticReleaseConfig;
import gg.nils.semanticrelease.api.error.SemanticReleaseException;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public abstract class VersionControlProviderImpl implements VersionControlProvider {

    private static final Pattern MESSAGE_REGEX = Pattern.compile("^(?<type>\\w+)(?:\\((?<scope>[^()]+)\\))?(?<breaking>!)?:\\s*(?<subject>.+)");

    private final SemanticReleaseConfig config;

    protected VersionControlProviderImpl(SemanticReleaseConfig config) {
        this.config = config;
    }

    @Override
    public Version tagToVersion(Tag tag) throws SemanticReleaseException {
        String[] versionParts = tag.getName().split("\\.");

        if (versionParts.length != 3)
            return null;

        try {
            int major = Integer.parseInt(versionParts[0]);
            int minor = Integer.parseInt(versionParts[1]);
            int patch = Integer.parseInt(versionParts[2]);

            return new VersionImpl(tag, major, minor, patch);
        } catch (NumberFormatException e) {
            throw new SemanticReleaseException("Could not transform tag to version", e);
        }
    }

    @Override
    public Commit rawCommitToCommit(RawCommit rawCommit) {
        String message = rawCommit.getMessage();
        List<String> lines = new ArrayList<>(Arrays.asList(message.split("\n")));

        Matcher matcher = MESSAGE_REGEX.matcher(lines.get(0));
        lines.remove(0);

        if (!matcher.find())
            return null;

        String type = matcher.group("type");
        String scope = matcher.group("scope");
        String breaking = matcher.group("breaking");
        String subject = matcher.group("subject");

        return CommitImpl.builder()
                .rawCommit(rawCommit)
                .type(type)
                .scope(scope)
                .subject(subject)
                .notes(lines)
                .breakingChanges(breaking != null)
                .build();
    }

    @Override
    public List<Commit> rawCommitsToCommits(List<RawCommit> rawCommits) {
        return rawCommits.stream()
                .map(this::rawCommitToCommit)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<Commit> getCommitsSince(Version version) throws SemanticReleaseException {
        return this.rawCommitsToCommits(this.getRawCommitsSince(version));
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

        return this.tagToVersion(latestTag);
    }

    @Override
    public Version getNextVersion() {
        Tag latestTag = this.getLatestTag();

        if (latestTag == null)
            return this.config.getFirstVersion();

        Version latestVersion = this.getLatestVersion();

        List<Commit> commits = this.getCommitsSince(latestVersion);

        int breakingChanges = 0;
        int features = 0;
        int patches = 0;

        for (Commit commit : commits) {
            if (commit.hasBreakingChanges()) {
                breakingChanges++;
            } else if (this.config.getFeatureTypes().contains(commit.getType().toLowerCase(Locale.ROOT))) {
                features++;
            } else if (this.config.getPatchTypes().contains(commit.getType().toLowerCase(Locale.ROOT))) {
                patches++;
            }
        }

        if (breakingChanges > 0) {
            latestVersion.incMajor();
        } else if (features > 0) {
            latestVersion.incMinor();
        } else if (patches > 0) {
            latestVersion.incPatch();
        }

        return latestVersion;
    }
}
