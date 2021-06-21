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

package gg.nils.semanticrelease.api.versioncontrol.converter;

import gg.nils.semanticrelease.api.Commit;
import gg.nils.semanticrelease.api.CommitImpl;
import gg.nils.semanticrelease.api.RawCommit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultRawCommitToCommitConverter implements RawCommitToCommitConverter {

    private static final Pattern MESSAGE_REGEX = Pattern.compile("^(?<type>\\w+)(?:\\((?<scope>[^()]+)\\))?(?<breaking>!)?:\\s*(?<subject>.+)");

    @Override
    public Commit convert(RawCommit rawCommit) {
        String message = rawCommit.getMessage();
        List<String> lines = new ArrayList<>(Arrays.asList(message.split("\n")));

        Matcher matcher = MESSAGE_REGEX.matcher(lines.get(0));
        lines.remove(0);

        if (!matcher.find())
            return null;

        String type = matcher.group("type");
        String scope = matcher.group("scope");
        String breaking = matcher.group("breaking");
        String subject = matcher.group("subject");

        return CommitImpl.builder()
                .rawCommit(rawCommit)
                .type(type)
                .scope(scope)
                .subject(subject)
                .notes(lines)
                .breakingChanges(breaking != null)
                .build();
    }
}
