package gg.nils.semanticrelease.api;

import lombok.Builder;

public class VersionImpl implements Version {

    private final Tag tag;

    private int major;

    private int minor;

    private int patch;

    public VersionImpl(Tag tag, int major, int minor, int patch) {
        this.tag = tag;
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    @Override
    public Tag getTag() {
        return this.tag;
    }

    @Override
    public int getMajor() {
        return this.major;
    }

    @Override
    public int getMinor() {
        return this.minor;
    }

    @Override
    public int getPatch() {
        return this.patch;
    }

    @Override
    public void setMajor(int major) {
        this.major = major;
    }

    @Override
    public void setMinor(int minor) {
        this.minor = minor;
    }

    @Override
    public void setPatch(int patch) {
        this.patch = patch;
    }

    @Override
    public void incMajor() {
        this.major++;
    }

    @Override
    public void incMinor() {
        this.minor++;
    }

    @Override
    public void incPatch() {
        this.patch++;
    }

    @Override
    public String toString() {
        return this.major + "." + this.minor + "." + this.patch;
    }
}
