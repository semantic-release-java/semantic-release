package gg.nils.semanticrelease.api.config;

import gg.nils.semanticrelease.api.Version;
import gg.nils.semanticrelease.api.VersionImpl;
import lombok.Builder;

import java.util.Collections;
import java.util.List;

public class DefaultSemanticReleaseConfig extends SemanticReleaseConfigImpl {

    public DefaultSemanticReleaseConfig() {
        super(
                new VersionImpl(null, 1, 0, 0),
                Collections.singletonList("feat"),
                Collections.singletonList("fix")
        );
    }
}
