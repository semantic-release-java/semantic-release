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

package gg.nils.semanticrelease.versioncontrol.git;

import gg.nils.semanticrelease.*;
import gg.nils.semanticrelease.config.SemanticReleaseConfig;
import gg.nils.semanticrelease.error.SemanticReleaseException;
import gg.nils.semanticrelease.versioncontrol.VersionControlProviderImpl;
import gg.nils.semanticrelease.versioncontrol.git.converter.*;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
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
        if (version.getTag() == null)
            return this.getRawCommits();

        try {
            ObjectId since = ObjectId.fromString(version.getTag().getCommitId());
            ObjectId until = this.git.getRepository().resolve("HEAD");

            Iterable<RevCommit> revCommits = this.git.log().addRange(since, until).call();

            return this.gitRevCommitsToRawCommitsConverter.convert(revCommits);
        } catch (GitAPIException | IOException e) {
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

    @Override
    public Tag getLatestTag() {
        try {
            String call = this.git.describe().setTags(true).setLong(true).call();

            if (call == null)
                return null;

            String version = this.extractVersion(call);

            Optional<Tag> optionalTag = this.getTags().stream()
                    .filter(tag -> tag.getName().equals(version))
                    .findFirst();

            return optionalTag.orElseThrow(() -> new SemanticReleaseException("Could not find any matching tag from describe: " + call));
        } catch (GitAPIException e) {
            throw new SemanticReleaseException("Could not get tags", e);
        }
    }

    private String extractVersion(String describeInput) {
        Matcher matcher1 = Pattern.compile("(.+)-(.+)-(.+)$").matcher(describeInput);

        if(matcher1.find()) {
            return matcher1.group(1);
        }

        Matcher matcher2 = Pattern.compile("(.+)-([0-9a-f]{40})$").matcher(describeInput);

        if(matcher2.find()) {
            return matcher2.group(1);
        }

        return describeInput;
    }

    @Override
    public Branch getCurrentBranch() {
        try {
            String branch = this.git.getRepository().getBranch();

            return new BranchImpl(branch);
        } catch (IOException e) {
            throw new SemanticReleaseException("Could not get current branch", e);
        }
    }

    @Override
    public boolean hasUncommittedChanges() {
        try {
            Status call = this.git.status().call();

            return call.hasUncommittedChanges();
        } catch (GitAPIException e) {
            throw new SemanticReleaseException("Could not get status", e);
        }
    }
}
