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
import gg.nils.semanticrelease.CommitImpl;
import gg.nils.semanticrelease.Version;
import gg.nils.semanticrelease.VersionImpl;
import gg.nils.semanticrelease.config.SemanticReleaseConfig;
import gg.nils.semanticrelease.config.SemanticReleaseConfigImpl;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DefaultNextVersionCalculatorTest {

    @Test
    void calculateNoChange() {
        SemanticReleaseConfig config = SemanticReleaseConfigImpl.builder()
                .firstVersion(new VersionImpl(null, 0, 0, 0))
                .featureTypes(Collections.singletonList("feat"))
                .patchTypes(Collections.singletonList("fix"))
                .build();

        NextVersionCalculator calculator = new DefaultNextVersionCalculator(config);

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

        NextVersionCalculator calculator = new DefaultNextVersionCalculator(config);

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

        NextVersionCalculator calculator = new DefaultNextVersionCalculator(config);

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

        NextVersionCalculator calculator = new DefaultNextVersionCalculator(config);

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

        NextVersionCalculator calculator = new DefaultNextVersionCalculator(config);

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