package gg.nils.semanticrelease.api.versioncontrol.git.converter;

import gg.nils.semanticrelease.api.RawCommit;
import gg.nils.semanticrelease.api.RawCommitImpl;
import org.eclipse.jgit.revwalk.RevCommit;

public class DefaultGitRevCommitToRawCommitConverter implements GitRevCommitToRawCommitConverter {

    @Override
    public RawCommit convert(RevCommit revCommit) {
        return RawCommitImpl.builder()
                .commitId(revCommit.getId().getName())
                .message(revCommit.getFullMessage())
                .build();
    }
}
