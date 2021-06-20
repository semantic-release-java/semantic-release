package gg.nils.semanticrelease.api;

import lombok.Builder;
import lombok.ToString;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        TagImpl tag = (TagImpl) o;
        return Objects.equals(this.commitId, tag.commitId) && Objects.equals(this.name, tag.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.commitId, this.name);
    }
}
