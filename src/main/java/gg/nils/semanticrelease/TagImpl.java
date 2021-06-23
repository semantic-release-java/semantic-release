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

import java.util.Objects;

@Builder
@ToString
public class TagImpl implements Tag {

    private final String commitId;
    private final String name;

    public TagImpl(String commitId, String name) {
        this.commitId = commitId;
        this.name = name;
    }

    @Override
    public String getCommitId() {
        return this.commitId;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        TagImpl tag = (TagImpl) o;
        return Objects.equals(this.commitId, tag.commitId) && Objects.equals(this.name, tag.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.commitId, this.name);
    }
}
