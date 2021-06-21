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

package gg.nils.semanticrelease.api.versioncontrol.converter;

import gg.nils.semanticrelease.api.Commit;
import gg.nils.semanticrelease.api.RawCommit;
import gg.nils.semanticrelease.api.RawCommitImpl;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RawCommitsToCommitsConverterImplTest {

    @Test
    void convert() {
        RawCommitToCommitConverter baseConverter = new DefaultRawCommitToCommitConverter();
        RawCommitsToCommitsConverter converter = new DefaultRawCommitsToCommitsConverter(baseConverter);

        List<RawCommit> rawCommits = Arrays.asList(
                RawCommitImpl.builder().commitId("1").message("Initial commit").build(),
                RawCommitImpl.builder().commitId("2").message("feat(test): Test").build()
        );

        List<Commit> commits = converter.convert(rawCommits);

        assertEquals(commits.size(), 1);
        assertEquals(commits.get(0).getRawCommit().getCommitId(), "2");
        assertEquals(commits.get(0).getRawCommit().toString(), rawCommits.get(1).toString());
        assertEquals(commits.get(0).toString(), "CommitImpl(rawCommit=RawCommitImpl(commitId=2, message=feat(test): Test), type=feat, scope=test, subject=Test, notes=[], breakingChanges=false)");
        assertEquals(commits.get(0).getType(), "feat");
        assertEquals(commits.get(0).getScope(), "test");
        assertEquals(commits.get(0).getSubject(), "Test");
        assertEquals(commits.get(0).getSubject(), "Test");
        assertEquals(commits.get(0).getNotes().size(), 0);
    }
}