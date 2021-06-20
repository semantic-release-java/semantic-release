package gg.nils.semanticrelease.api.versioncontrol.git.converter;

import gg.nils.semanticrelease.api.Tag;
import org.eclipse.jgit.lib.Ref;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class GitRefsToTagsConverterImpl implements GitRefsToTagsConverter {

    private final GitRefToTagConverter gitRefToTagConverter;

    public GitRefsToTagsConverterImpl(GitRefToTagConverter gitRefToTagConverter) {
        this.gitRefToTagConverter = gitRefToTagConverter;
    }

    @Override
    public List<Tag> convert(List<Ref> refs) {
        return refs.stream()
                .map(this.gitRefToTagConverter::convert)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
