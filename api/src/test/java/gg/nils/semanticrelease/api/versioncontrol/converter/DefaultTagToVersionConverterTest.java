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

package gg.nils.semanticrelease.api.versioncontrol.converter;

import gg.nils.semanticrelease.api.Tag;
import gg.nils.semanticrelease.api.TagImpl;
import gg.nils.semanticrelease.api.Version;
import gg.nils.semanticrelease.api.VersionImpl;
import gg.nils.semanticrelease.api.error.SemanticReleaseException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DefaultTagToVersionConverterTest {

    @Test
    void convert() {
        TagToVersionConverter converter = new DefaultTagToVersionConverter();

        Tag tag = TagImpl.builder()
                .commitId("1")
                .name("1.0.0")
                .build();

        Version version = converter.convert(tag);

        assertEquals(version.getTag(), tag);
        assertEquals(version, new VersionImpl(tag, 1, 0, 0));

        assertEquals(version.toString(), tag.getName());
        assertEquals(version.getTag().getCommitId(), tag.getCommitId());
        assertEquals(version.getTag().toString(), tag.toString());
        assertEquals(version.getTag().hashCode(), tag.hashCode());
        assertEquals(version.getTag(), TagImpl.builder().commitId("1").name("1.0.0").build());

        assertEquals(version.getMajor(), 1);
        assertEquals(version.getMinor(), 0);
        assertEquals(version.getPatch(), 0);

    }

    @Test
    void convertInvalidVersion() {
        TagToVersionConverter converter = new DefaultTagToVersionConverter();

        Tag tag = TagImpl.builder()
                .commitId("1")
                .name("invalid")
                .build();

        assertThrows(SemanticReleaseException.class, () -> {
            converter.convert(tag);
        });
    }

    @Test
    void convertNumberFormat() {
        TagToVersionConverter converter = new DefaultTagToVersionConverter();

        Tag tag = TagImpl.builder()
                .commitId("1")
                .name("1.0.a")
                .build();

        assertThrows(SemanticReleaseException.class, () -> {
            converter.convert(tag);
        });
    }
}