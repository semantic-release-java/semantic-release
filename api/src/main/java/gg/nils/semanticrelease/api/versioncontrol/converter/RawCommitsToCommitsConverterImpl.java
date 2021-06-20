package gg.nils.semanticrelease.api.versioncontrol.converter;

import gg.nils.semanticrelease.api.Commit;
import gg.nils.semanticrelease.api.RawCommit;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RawCommitsToCommitsConverterImpl implements RawCommitsToCommitsConverter {

    private final RawCommitToCommitConverter rawCommitToCommitConverter;

    public RawCommitsToCommitsConverterImpl(RawCommitToCommitConverter rawCommitToCommitConverter) {
        this.rawCommitToCommitConverter = rawCommitToCommitConverter;
    }

    @Override
    public List<Commit> convert(List<RawCommit> rawCommits) {
        return rawCommits.stream()
                .map(this.rawCommitToCommitConverter::convert)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
