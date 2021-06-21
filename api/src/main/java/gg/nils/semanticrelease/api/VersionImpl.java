/*
 *     Copyright (C) 2021  nils
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package gg.nils.semanticrelease.api;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        VersionImpl version = (VersionImpl) o;
        return this.major == version.major && this.minor == version.minor && this.patch == version.patch && Objects.equals(this.tag, version.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.tag, this.major, this.minor, this.patch);
    }
}
