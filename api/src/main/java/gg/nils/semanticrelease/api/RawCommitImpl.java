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

package gg.nils.semanticrelease.api;

import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
public class RawCommitImpl implements RawCommit {

    private final String commitId;

    private final String message;

    public RawCommitImpl(String commitId, String message) {
        this.commitId = commitId;
        this.message = message;
    }

    @Override
    public String getCommitId() {
        return this.commitId;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
