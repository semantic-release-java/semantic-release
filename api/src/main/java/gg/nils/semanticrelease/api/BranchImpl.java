package gg.nils.semanticrelease.api;

import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
public class BranchImpl implements Branch {

    private final String name;

    public BranchImpl(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
