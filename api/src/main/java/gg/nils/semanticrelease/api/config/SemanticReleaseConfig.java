package gg.nils.semanticrelease.api.config;

import gg.nils.semanticrelease.api.Version;

import java.util.List;

public interface SemanticReleaseConfig {

    Version getFirstVersion();

    List<String> getFeatureTypes();

    List<String> getPatchTypes();
}
