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

package gg.nils.semanticrelease.api.versioncontrol.git.converter;

import gg.nils.semanticrelease.api.RawCommit;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DefaultGitRevCommitsToRawCommitsConverter implements GitRevCommitsToRawCommitsConverter {

    private final GitRevCommitToRawCommitConverter gitRevCommitToRawCommitConverter;

    public DefaultGitRevCommitsToRawCommitsConverter(GitRevCommitToRawCommitConverter gitRevCommitToRawCommitConverter) {
        this.gitRevCommitToRawCommitConverter = gitRevCommitToRawCommitConverter;
    }

    @Override
    public List<RawCommit> convert(Iterable<RevCommit> revCommits) {
        List<RevCommit> tmpList = new ArrayList<>();

        revCommits.forEach(tmpList::add);

        return tmpList.stream()
                .map(this.gitRevCommitToRawCommitConverter::convert)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
