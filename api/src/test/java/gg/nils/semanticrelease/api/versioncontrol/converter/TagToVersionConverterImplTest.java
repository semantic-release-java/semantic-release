package gg.nils.semanticrelease.api.versioncontrol.converter;

import gg.nils.semanticrelease.api.Tag;
import gg.nils.semanticrelease.api.TagImpl;
import gg.nils.semanticrelease.api.Version;
import gg.nils.semanticrelease.api.VersionImpl;
import gg.nils.semanticrelease.api.error.SemanticReleaseException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TagToVersionConverterImplTest {

    @Test
    void convert() {
        TagToVersionConverter converter = new TagToVersionConverterImpl();

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
        TagToVersionConverter converter = new TagToVersionConverterImpl();

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
        TagToVersionConverter converter = new TagToVersionConverterImpl();

        Tag tag = TagImpl.builder()
                .commitId("1")
                .name("1.0.a")
                .build();

        assertThrows(SemanticReleaseException.class, () -> {
            converter.convert(tag);
        });
    }
}