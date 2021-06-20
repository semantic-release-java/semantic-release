package gg.nils.semanticrelease.api;

import lombok.Builder;
import lombok.ToString;

import java.util.List;

@Builder
@ToString
public class CommitImpl implements Commit {

    private final RawCommit rawCommit;

    private final String type;

    private final String scope;

    private final String subject;

    private final List<String> notes;

    private final boolean breakingChanges;

    @Override
    public RawCommit getRawCommit() {
        return this.rawCommit;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public String getScope() {
        return this.scope;
    }

    @Override
    public String getSubject() {
        return this.subject;
    }

    @Override
    public List<String> getNotes() {
        return this.notes;
    }

    @Override
    public boolean hasBreakingChanges() {
        return this.breakingChanges;
    }
}
