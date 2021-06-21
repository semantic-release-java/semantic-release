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

package gg.nils.semanticrelease.api.config;

import gg.nils.semanticrelease.api.Version;
import lombok.Builder;

import java.util.List;

@Builder
public class SemanticReleaseConfigImpl implements SemanticReleaseConfig {

    private final Version firstVersion;

    private final List<String> featureTypes;

    private final List<String> patchTypes;

    public SemanticReleaseConfigImpl(Version firstVersion, List<String> featureTypes, List<String> patchTypes) {
        this.firstVersion = firstVersion;
        this.featureTypes = featureTypes;
        this.patchTypes = patchTypes;
    }

    @Override
    public Version getFirstVersion() {
        return this.firstVersion;
    }

    @Override
    public List<String> getFeatureTypes() {
        return this.featureTypes;
    }

    @Override
    public List<String> getPatchTypes() {
        return this.patchTypes;
    }
}
