package gg.nils.semanticrelease.api.calculator;

import gg.nils.semanticrelease.api.Commit;
import gg.nils.semanticrelease.api.CommitImpl;
import gg.nils.semanticrelease.api.Version;
import gg.nils.semanticrelease.api.VersionImpl;
import gg.nils.semanticrelease.api.config.SemanticReleaseConfig;
import gg.nils.semanticrelease.api.config.SemanticReleaseConfigImpl;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NextVersionCalculatorImplTest {

    @Test
    void calculateNoChange() {
        SemanticReleaseConfig config = SemanticReleaseConfigImpl.builder()
                .firstVersion(new VersionImpl(null, 0, 0, 0))
                .featureTypes(Collections.singletonList("feat"))
                .patchTypes(Collections.singletonList("fix"))
                .build();

        NextVersionCalculator calculator = new NextVersionCalculatorImpl(config);

        Version inputVersion = new VersionImpl(null, 1, 0, 0);

        Version outputVersion = calculator.calculate(inputVersion, Collections.emptyList());

        assertEquals(outputVersion, new VersionImpl(null, 1, 0, 0));
    }

    @Test
    void calculateOnePatch() {
        SemanticReleaseConfig config = SemanticReleaseConfigImpl.builder()
                .firstVersion(new VersionImpl(null, 0, 0, 0))
                .featureTypes(Collections.singletonList("feat"))
                .patchTypes(Collections.singletonList("fix"))
                .build();

        NextVersionCalculator calculator = new NextVersionCalculatorImpl(config);

        Version inputVersion = new VersionImpl(null, 1, 0, 0);

        List<Commit> commits = Collections.singletonList(
                CommitImpl.builder().type("fix").build()
        );

        Version outputVersion = calculator.calculate(inputVersion, commits);

        assertEquals(outputVersion, new VersionImpl(null, 1, 0, 1));
    }

    @Test
    void calculateOneMinor() {
        SemanticReleaseConfig config = SemanticReleaseConfigImpl.builder()
                .firstVersion(new VersionImpl(null, 0, 0, 0))
                .featureTypes(Collections.singletonList("feat"))
                .patchTypes(Collections.singletonList("fix"))
                .build();

        NextVersionCalculator calculator = new NextVersionCalculatorImpl(config);

        Version inputVersion = new VersionImpl(null, 1, 0, 0);

        List<Commit> commits = Collections.singletonList(
                CommitImpl.builder().type("feat").build()
        );

        Version outputVersion = calculator.calculate(inputVersion, commits);

        assertEquals(outputVersion, new VersionImpl(null, 1, 1, 0));
    }

    @Test
    void calculateOneMajor() {
        SemanticReleaseConfig config = SemanticReleaseConfigImpl.builder()
                .firstVersion(new VersionImpl(null, 0, 0, 0))
                .featureTypes(Collections.singletonList("feat"))
                .patchTypes(Collections.singletonList("fix"))
                .build();

        NextVersionCalculator calculator = new NextVersionCalculatorImpl(config);

        Version inputVersion = new VersionImpl(null, 1, 0, 0);

        List<Commit> commits = Collections.singletonList(
                CommitImpl.builder().type("feat").breakingChanges(true).build()
        );

        Version outputVersion = calculator.calculate(inputVersion, commits);

        assertEquals(outputVersion, new VersionImpl(null, 2, 0, 0));
    }

    @Test
    void calculateAll() {
        SemanticReleaseConfig config = SemanticReleaseConfigImpl.builder()
                .firstVersion(new VersionImpl(null, 0, 0, 0))
                .featureTypes(Collections.singletonList("feat"))
                .patchTypes(Collections.singletonList("fix"))
                .build();

        NextVersionCalculator calculator = new NextVersionCalculatorImpl(config);

        Version inputVersion = new VersionImpl(null, 1, 0, 0);

        List<Commit> commits = Arrays.asList(
                CommitImpl.builder().type("feat").build(),
                CommitImpl.builder().type("fix").build(),
                CommitImpl.builder().type("feat").breakingChanges(true).build()
        );

        Version outputVersion = calculator.calculate(inputVersion, commits);

        assertEquals(outputVersion, new VersionImpl(null, 2, 0, 0));
    }
}