package gg.nils.semanticrelease.api.versioncontrol.git.converter;

import gg.nils.semanticrelease.api.Tag;
import gg.nils.semanticrelease.api.TagImpl;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;

public class GitRefToTagConverterImpl implements GitRefToTagConverter {

    @Override
    public Tag convert(Ref ref) {
        return TagImpl.builder()
                .commitId(ref.getObjectId().getName())
                .name(ref.getName().replaceAll(Constants.R_TAGS, ""))
                .build();
    }
}
