package gg.nils.semanticrelease.api.versioncontrol.git.converter;

import gg.nils.semanticrelease.api.Tag;
import gg.nils.semanticrelease.api.versioncontrol.converter.Converter;
import org.eclipse.jgit.lib.Ref;

public interface GitRefToTagConverter extends Converter<Ref, Tag> {
}
