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

package gg.nils.semanticrelease.api.calculator;

import gg.nils.semanticrelease.api.Commit;
import gg.nils.semanticrelease.api.Version;
import gg.nils.semanticrelease.api.config.SemanticReleaseConfig;

import java.util.List;
import java.util.Locale;

public class DefaultNextVersionCalculator implements NextVersionCalculator {

    private final SemanticReleaseConfig config;

    public DefaultNextVersionCalculator(SemanticReleaseConfig config) {
        this.config = config;
    }

    @Override
    public Version calculate(Version latestVersion, List<Commit> commits) {
        int breakingChanges = 0;
        int features = 0;
        int patches = 0;

        for (Commit commit : commits) {
            if (commit.hasBreakingChanges()) {
                breakingChanges++;
            } else if (this.config.getFeatureTypes().contains(commit.getType().toLowerCase(Locale.ROOT))) {
                features++;
            } else if (this.config.getPatchTypes().contains(commit.getType().toLowerCase(Locale.ROOT))) {
                patches++;
            }
        }

        if (breakingChanges > 0) {
            latestVersion.incMajor();
            latestVersion.setMinor(0);
            latestVersion.setPatch(0);
        } else if (features > 0) {
            latestVersion.incMinor();
            latestVersion.setPatch(0);
        } else if (patches > 0) {
            latestVersion.incPatch();
        }

        return latestVersion;
    }
}
