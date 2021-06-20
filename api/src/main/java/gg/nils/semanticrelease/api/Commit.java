package gg.nils.semanticrelease.api;

import java.util.List;

public interface Commit {

    RawCommit getRawCommit();

    String getType();

    String getScope();

    String getSubject();

    List<String> getNotes();

    boolean hasBreakingChanges();
}
