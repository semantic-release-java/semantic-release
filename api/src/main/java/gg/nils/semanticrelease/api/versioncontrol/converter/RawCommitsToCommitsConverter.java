package gg.nils.semanticrelease.api.versioncontrol.converter;

import gg.nils.semanticrelease.api.Commit;
import gg.nils.semanticrelease.api.RawCommit;

import java.util.List;

public interface RawCommitsToCommitsConverter extends Converter<List<RawCommit>, List<Commit>> {
}
