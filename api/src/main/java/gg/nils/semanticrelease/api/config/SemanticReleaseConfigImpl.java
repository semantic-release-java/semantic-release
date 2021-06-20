package gg.nils.semanticrelease.api.config;

import gg.nils.semanticrelease.api.Version;
import lombok.Builder;

import java.util.List;

@Builder
public class SemanticReleaseConfigImpl implements SemanticReleaseConfig {

    private final Version firstVersion;

    private final List<String> featureTypes;

    private final List<String> patchTypes;

    public SemanticReleaseConfigImpl(Version firstVersion, List<String> featureTypes, List<String> patchTypes) {
        this.firstVersion = firstVersion;
        this.featureTypes = featureTypes;
        this.patchTypes = patchTypes;
    }

    @Override
    public Version getFirstVersion() {
        return this.firstVersion;
    }

    @Override
    public List<String> getFeatureTypes() {
        return this.featureTypes;
    }

    @Override
    public List<String> getPatchTypes() {
        return this.patchTypes;
    }
}
