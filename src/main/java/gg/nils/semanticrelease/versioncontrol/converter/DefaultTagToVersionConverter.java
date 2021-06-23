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

package gg.nils.semanticrelease.versioncontrol.converter;

import gg.nils.semanticrelease.Tag;
import gg.nils.semanticrelease.Version;
import gg.nils.semanticrelease.VersionImpl;
import gg.nils.semanticrelease.error.SemanticReleaseException;

public class DefaultTagToVersionConverter implements TagToVersionConverter {

    @Override
    public Version convert(Tag tag) {
        String[] versionParts = tag.getName().split("\\.");

        if (versionParts.length != 3)
            throw new SemanticReleaseException("Invalid version provided " + tag.getName());

        try {
            int major = Integer.parseInt(versionParts[0]);
            int minor = Integer.parseInt(versionParts[1]);
            int patch = Integer.parseInt(versionParts[2]);

            return new VersionImpl(tag, major, minor, patch);
        } catch (NumberFormatException e) {
            throw new SemanticReleaseException("Could not transform tag to version", e);
        }
    }
}
