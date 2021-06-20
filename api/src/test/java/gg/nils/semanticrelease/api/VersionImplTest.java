package gg.nils.semanticrelease.api;

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