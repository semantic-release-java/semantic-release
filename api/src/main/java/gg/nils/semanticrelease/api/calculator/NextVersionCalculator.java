package gg.nils.semanticrelease.api.calculator;

import gg.nils.semanticrelease.api.Commit;
import gg.nils.semanticrelease.api.Version;

import java.util.List;

public interface NextVersionCalculator {

    Version calculate(Version latestVersion, List<Commit> commits);
}
