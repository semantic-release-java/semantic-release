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

package gg.nils.semanticrelease.versioncontrol.git.converter;

import gg.nils.semanticrelease.Tag;
import gg.nils.semanticrelease.TagImpl;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;

public class DefaultGitRefToTagConverter implements GitRefToTagConverter {

    @Override
    public Tag convert(Ref ref) {
        ObjectId objectId = ref.getPeeledObjectId() != null
                ? ref.getPeeledObjectId()
                : ref.getObjectId();

        return TagImpl.builder()
                .commitId(objectId.getName())
                .name(ref.getName().replaceAll(Constants.R_TAGS, ""))
                .build();
    }
}
