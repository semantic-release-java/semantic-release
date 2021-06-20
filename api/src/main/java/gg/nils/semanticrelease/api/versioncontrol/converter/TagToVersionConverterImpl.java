package gg.nils.semanticrelease.api.versioncontrol.converter;

import gg.nils.semanticrelease.api.Tag;
import gg.nils.semanticrelease.api.Version;
import gg.nils.semanticrelease.api.VersionImpl;
import gg.nils.semanticrelease.api.error.SemanticReleaseException;

public class TagToVersionConverterImpl implements TagToVersionConverter {

    @Override
    public Version convert(Tag tag) {
        String[] versionParts = tag.getName().split("\\.");

        if (versionParts.length != 3)
            throw new SemanticReleaseException("Invalid version provided " + tag.getName());

        try {
            int major = Integer.parseInt(versionParts[0]);
            int minor = Integer.parseInt(versionParts[1]);
            int patch = Integer.parseInt(versionParts[2]);

            return new VersionImpl(tag, major, minor, patch);
        } catch (NumberFormatException e) {
            throw new SemanticReleaseException("Could not transform tag to version", e);
        }
    }
}
