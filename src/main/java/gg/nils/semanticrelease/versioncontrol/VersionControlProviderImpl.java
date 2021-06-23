/*
 *     Copyright (C) 2021  nils
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package gg.nils.semanticrelease.versioncontrol;

import gg.nils.semanticrelease.Branch;
import gg.nils.semanticrelease.Commit;
import gg.nils.semanticrelease.Tag;
import gg.nils.semanticrelease.Version;
import gg.nils.semanticrelease.calculator.NextVersionCalculator;
import gg.nils.semanticrelease.calculator.DefaultNextVersionCalculator;
import gg.nils.semanticrelease.config.SemanticReleaseConfig;
import gg.nils.semanticrelease.versioncontrol.converter.*;
import lombok.Setter;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        String version = this.getFullVersionWithoutDirty();

        if(this.hasUncommittedChanges()) {
            version += "-DIRTY";
        }

        return version;
    }

    @Override
    public String getFullVersionWithoutDirty() {
        Version nextVersion = this.getNextVersion();
        Branch currentBranch = this.getCurrentBranch();

        String version = "";

        String currentBranchName = currentBranch.getName();

        Matcher matcher = Pattern.compile("^([0-9a-f]{40})$").matcher(currentBranchName);

        if(matcher.find() || currentBranchName.equals("master") || currentBranchName.equals("main")) {
            version += nextVersion.toString();
        } else if(currentBranchName.equals("develop")) {
            version += nextVersion.toString() + "-SNAPSHOT";
        } else {
            version += nextVersion.toString() + "-" + currentBranchName.replaceAll("/", "-");
        }

        return version;
    }
}
