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

package gg.nils.semanticrelease;

import lombok.Builder;
import lombok.ToString;

import java.util.List;

@Builder
@ToString
public class CommitImpl implements Commit {

    private final RawCommit rawCommit;

    private final String type;

    private final String scope;

    private final String subject;

    private final List<String> notes;

    private final boolean breakingChanges;

    @Override
    public RawCommit getRawCommit() {
        return this.rawCommit;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public String getScope() {
        return this.scope;
    }

    @Override
    public String getSubject() {
        return this.subject;
    }

    @Override
    public List<String> getNotes() {
        return this.notes;
    }

    @Override
    public boolean hasBreakingChanges() {
        return this.breakingChanges;
    }
}
