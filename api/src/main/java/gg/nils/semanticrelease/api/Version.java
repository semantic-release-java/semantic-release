package gg.nils.semanticrelease.api;

public interface Version {

    Tag getTag();

    int getMajor();

    int getMinor();

    int getPatch();

    void setMajor(int major);

    void setMinor(int minor);

    void setPatch(int patch);

    void incMajor();

    void incMinor();

    void incPatch();
}
