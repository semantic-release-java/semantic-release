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

import java.util.List;

public interface VersionControlProvider {

    List<RawCommit> getRawCommits();

    List<RawCommit> getRawCommitsSince(Version version);

    List<Commit> getCommitsSince(Version version);

    List<Tag> getTags();

    Tag getLatestTag();

    Branch getCurrentBranch();

    boolean hasUncommittedChanges();

    Version getLatestVersion();

    Version getNextVersion();

    String getFullVersion();

    String getFullVersionWithoutDirty();
}
