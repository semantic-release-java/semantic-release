package gg.nils.semanticrelease.api.versioncontrol.git.converter;

import gg.nils.semanticrelease.api.RawCommit;
import gg.nils.semanticrelease.api.versioncontrol.converter.Converter;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.List;

public interface GitRevCommitsToRawCommitsConverter extends Converter<Iterable<RevCommit>, List<RawCommit>> {
}
