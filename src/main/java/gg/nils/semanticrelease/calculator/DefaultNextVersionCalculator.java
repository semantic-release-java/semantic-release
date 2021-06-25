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

package gg.nils.semanticrelease.calculator;

import gg.nils.semanticrelease.Commit;
import gg.nils.semanticrelease.Version;
import gg.nils.semanticrelease.config.SemanticReleaseConfig;

import java.util.List;
import java.util.Locale;

public class DefaultNextVersionCalculator implements NextVersionCalculator {

    private final SemanticReleaseConfig config;

    public DefaultNextVersionCalculator(SemanticReleaseConfig config) {
        this.config = config;
    }

    @Override
    public Version calculate(Version latestVersion, List<Commit> commits) {
        Version nextVersion = latestVersion.clone();

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
            nextVersion.incMajor();
            nextVersion.setMinor(0);
            nextVersion.setPatch(0);
        } else if (features > 0) {
            nextVersion.incMinor();
            nextVersion.setPatch(0);
        } else if (patches > 0) {
            nextVersion.incPatch();
        }

        return nextVersion;
    }
}
