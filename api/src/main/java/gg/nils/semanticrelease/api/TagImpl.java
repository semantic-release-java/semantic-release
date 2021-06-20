package gg.nils.semanticrelease.api;

import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
public class TagImpl implements Tag {

    private final String commitId;
    private final String name;

    public TagImpl(String commitId, String name) {
        this.commitId = commitId;
        this.name = name;
    }

    @Override
    public String getCommitId() {
        return this.commitId;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
