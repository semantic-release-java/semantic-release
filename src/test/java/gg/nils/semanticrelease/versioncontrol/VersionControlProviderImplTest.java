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

import gg.nils.semanticrelease.*;
import gg.nils.semanticrelease.calculator.DefaultNextVersionCalculator;
import gg.nils.semanticrelease.config.SemanticReleaseConfig;
import gg.nils.semanticrelease.config.SemanticReleaseConfigImpl;
import gg.nils.semanticrelease.versioncontrol.converter.DefaultRawCommitToCommitConverter;
import gg.nils.semanticrelease.versioncontrol.converter.DefaultRawCommitsToCommitsConverter;
import gg.nils.semanticrelease.versioncontrol.converter.DefaultTagToVersionConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VersionControlProviderImplTest {

    SemanticReleaseConfig config;
    VersionControlProviderImpl provider;
    VersionControlProviderImpl emptyProvider;

    @BeforeEach
    void setUp() {
        this.config = SemanticReleaseConfigImpl.builder()
                .firstVersion(new VersionImpl(null, 1, 0, 0))
                .featureTypes(Collections.singletonList("feat"))
                .patchTypes(Collections.singletonList("fix"))
                .build();

        this.provider = new VersionControlProviderImpl(this.config) {
            @Override
            public List<RawCommit> getRawCommits() {
                return Arrays.asList(
                        RawCommitImpl.builder().commitId("1").message("Initial commit").build(),
                        RawCommitImpl.builder().commitId("2").message("fix: 1").build(),
                        RawCommitImpl.builder().commitId("3").message("chore(release): 1.0.0").build(),
                        RawCommitImpl.builder().commitId("4").message("fix: 3").build(),
                        RawCommitImpl.builder().commitId("5").message("chore(release): 1.0.1").build(),
                        RawCommitImpl.builder().commitId("6").message("fix: 6").build(),
                        RawCommitImpl.builder().commitId("7").message("fix: 7").build(),
                        RawCommitImpl.builder().commitId("8").message("feat: 8").build(),
                        RawCommitImpl.builder().commitId("9").message("fix: 9").build(),
                        RawCommitImpl.builder().commitId("10").message("chore(release): 1.1.0").build(),
                        RawCommitImpl.builder().commitId("11").message("fix(ci)!: Breaking 11").build(),
                        RawCommitImpl.builder().commitId("12").message("feat!: Breaking 12").build(),
                        RawCommitImpl.builder().commitId("13").message("chore(release): 2.0.0").build(),
                        RawCommitImpl.builder().commitId("14").message("fix: 14").build()
                );
            }

            @Override
            public List<RawCommit> getRawCommitsSince(Version version) {
                List<RawCommit> rawCommits = this.getRawCommits();
                List<RawCommit> result = new ArrayList<>();

                int versionCommitId = Integer.parseInt(version.getTag().getCommitId());

                for (RawCommit rawCommit : rawCommits) {
                    int rawCommitId = Integer.parseInt(rawCommit.getCommitId());

                    if (rawCommitId <= versionCommitId) continue;

                    result.add(rawCommit);
                }

                return result;
            }

            @Override
            public List<Tag> getTags() {
                return Arrays.asList(
                        TagImpl.builder().commitId("3").name("1.0.0").build(),
                        TagImpl.builder().commitId("5").name("1.0.1").build(),
                        TagImpl.builder().commitId("10").name("1.1.0").build(),
                        TagImpl.builder().commitId("13").name("2.0.0").build()
                );
            }

            @Override
            public Tag getLatestTag() {
                return TagImpl.builder().commitId("13").name("2.0.0").build();
            }

            @Override
            public Branch getCurrentBranch() {
                return new BranchImpl("main");
            }

            @Override
            public boolean hasUncommittedChanges() {
                return false;
            }
        };

        this.emptyProvider = new VersionControlProviderImpl(this.config) {
            @Override
            public List<RawCommit> getRawCommits() {
                return Collections.emptyList();
            }

            @Override
            public List<RawCommit> getRawCommitsSince(Version version) {
                return Collections.emptyList();
            }

            @Override
            public List<Tag> getTags() {
                return Collections.emptyList();
            }

            @Override
            public Tag getLatestTag() {
                return null;
            }

            @Override
            public Branch getCurrentBranch() {
                return null;
            }

            @Override
            public boolean hasUncommittedChanges() {
                return false;
            }
        };
    }

    @Test
    void getCommitsSinceCommitId3() {
        VersionImpl version = new VersionImpl(TagImpl.builder().commitId("3").name("1.0.0").build(), 1, 0, 0);

        List<Commit> rawCommitsSince = this.provider.getCommitsSince(version);

        assertEquals(rawCommitsSince.size(), 11);
    }

    @Test
    void getCommitsSinceCommitId5() {
        VersionImpl version = new VersionImpl(TagImpl.builder().commitId("5").name("1.0.1").build(), 1, 0, 1);

        List<Commit> rawCommitsSince = this.provider.getCommitsSince(version);

        assertEquals(rawCommitsSince.size(), 9);
    }

    @Test
    void getCommitsSinceCommitId10() {
        VersionImpl version = new VersionImpl(TagImpl.builder().commitId("10").name("1.1.0").build(), 1, 1, 0);

        List<Commit> rawCommitsSince = this.provider.getCommitsSince(version);

        assertEquals(rawCommitsSince.size(), 4);
    }

    @Test
    void getCommitsSinceCommitId13() {
        VersionImpl version = new VersionImpl(TagImpl.builder().commitId("13").name("2.0.0").build(), 2, 0, 0);

        List<Commit> rawCommitsSince = this.provider.getCommitsSince(version);

        assertEquals(rawCommitsSince.size(), 1);
    }

    @Test
    void getLatestTagSuccess() {
        Tag latestTag = this.provider.getLatestTag();

        assertEquals(latestTag, TagImpl.builder().commitId("13").name("2.0.0").build());
    }

    @Test
    void getLatestTagNull() {
        Tag latestTag = this.emptyProvider.getLatestTag();

        assertNull(latestTag);
    }

    @Test
    void getLatestVersionSuccess() {
        Version latestVersion = this.provider.getLatestVersion();

        assertEquals(latestVersion, new VersionImpl(TagImpl.builder().commitId("13").name("2.0.0").build(), 2, 0, 0));
    }

    @Test
    void getLatestVersionNull() {
        Version latestVersion = this.emptyProvider.getLatestVersion();

        assertEquals(latestVersion, this.config.getFirstVersion());
    }

    @Test
    void getNextVersionSuccess() {
        Version nextVersion = this.provider.getNextVersion();

        assertEquals(nextVersion, new VersionImpl(TagImpl.builder().commitId("13").name("2.0.0").build(), 2, 0, 1));
    }

    @Test
    void getNextVersionNull() {
        Version nextVersion = this.emptyProvider.getNextVersion();

        assertEquals(nextVersion, this.config.getFirstVersion());
    }

    @Test
    void setConverterManually() {
        this.emptyProvider.setTagToVersionConverter(new DefaultTagToVersionConverter());
        DefaultRawCommitToCommitConverter rawCommitToCommitConverter = new DefaultRawCommitToCommitConverter();
        this.emptyProvider.setRawCommitToCommitConverter(rawCommitToCommitConverter);
        this.emptyProvider.setRawCommitsToCommitsConverter(new DefaultRawCommitsToCommitsConverter(rawCommitToCommitConverter));
        this.emptyProvider.setNextVersionCalculator(new DefaultNextVersionCalculator(this.config));

        assertNotNull(this.emptyProvider);
    }
}