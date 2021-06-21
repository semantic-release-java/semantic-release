package gg.nils.semanticrelease.api.calculator;

import gg.nils.semanticrelease.api.Commit;
import gg.nils.semanticrelease.api.Version;
import gg.nils.semanticrelease.api.config.SemanticReleaseConfig;

import java.util.List;
import java.util.Locale;

public class NextVersionCalculatorImpl implements NextVersionCalculator {

    private final SemanticReleaseConfig config;

    public NextVersionCalculatorImpl(SemanticReleaseConfig config) {
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
