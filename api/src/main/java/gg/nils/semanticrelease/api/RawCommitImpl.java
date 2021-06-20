package gg.nils.semanticrelease.api;

import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
public class RawCommitImpl implements RawCommit {

    private final String commitId;

    private final String message;

    public RawCommitImpl(String commitId, String message) {
        this.commitId = commitId;
        this.message = message;
    }

    @Override
    public String getCommitId() {
        return this.commitId;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
