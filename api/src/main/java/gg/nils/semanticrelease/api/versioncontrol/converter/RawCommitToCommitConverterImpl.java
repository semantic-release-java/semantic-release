package gg.nils.semanticrelease.api.versioncontrol.converter;

import gg.nils.semanticrelease.api.Commit;
import gg.nils.semanticrelease.api.CommitImpl;
import gg.nils.semanticrelease.api.RawCommit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RawCommitToCommitConverterImpl implements RawCommitToCommitConverter {

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
