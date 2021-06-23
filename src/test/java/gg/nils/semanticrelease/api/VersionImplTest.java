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

package gg.nils.semanticrelease.api;

import gg.nils.semanticrelease.Version;
import gg.nils.semanticrelease.VersionImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VersionImplTest {

    @Test
    void incMajor() {
        Version version = new VersionImpl(null, 1, 0, 0);

        version.incMajor();

        VersionImpl expected = new VersionImpl(null, 2, 0, 0);

        assertEquals(version, expected);
        assertEquals(version.hashCode(), expected.hashCode());
    }

    @Test
    void incMinor() {
        Version version = new VersionImpl(null, 1, 0, 0);

        version.incMinor();

        VersionImpl expected = new VersionImpl(null, 1, 1, 0);
        assertEquals(version, expected);
        assertEquals(version.hashCode(), expected.hashCode());
    }

    @Test
    void incPatch() {
        Version version = new VersionImpl(null, 1, 0, 0);

        version.incPatch();

        VersionImpl expected = new VersionImpl(null, 1, 0, 1);
        assertEquals(version, expected);
        assertEquals(version.hashCode(), expected.hashCode());
    }

    @Test
    void setMajor() {
        Version version = new VersionImpl(null, 1, 0, 0);

        version.setMajor(5);

        VersionImpl expected = new VersionImpl(null, 5, 0, 0);
        assertEquals(version, expected);
        assertEquals(version.hashCode(), expected.hashCode());
    }

    @Test
    void setMinor() {
        Version version = new VersionImpl(null, 1, 0, 0);

        version.setMinor(3);

        VersionImpl expected = new VersionImpl(null, 1, 3, 0);
        assertEquals(version, expected);
        assertEquals(version.hashCode(), expected.hashCode());
    }

    @Test
    void setPatch() {
        Version version = new VersionImpl(null, 1, 0, 0);

        version.setPatch(12);

        VersionImpl expected = new VersionImpl(null, 1, 0, 12);
        assertEquals(version, expected);
        assertEquals(version.hashCode(), expected.hashCode());
    }
}