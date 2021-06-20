package gg.nils.semanticrelease.api.versioncontrol.git.converter;

import gg.nils.semanticrelease.api.RawCommit;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class GitRevCommitsToRawCommitsConverterImpl implements GitRevCommitsToRawCommitsConverter {

    private final GitRevCommitToRawCommitConverter gitRevCommitToRawCommitConverter;

    public GitRevCommitsToRawCommitsConverterImpl(GitRevCommitToRawCommitConverter gitRevCommitToRawCommitConverter) {
        this.gitRevCommitToRawCommitConverter = gitRevCommitToRawCommitConverter;
    }

    @Override
    public List<RawCommit> convert(Iterable<RevCommit> revCommits) {
        List<RevCommit> tmpList = new ArrayList<>();

        revCommits.forEach(tmpList::add);

        return tmpList.stream()
                .map(this.gitRevCommitToRawCommitConverter::convert)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
