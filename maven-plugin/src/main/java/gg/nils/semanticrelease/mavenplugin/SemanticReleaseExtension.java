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

package gg.nils.semanticrelease.mavenplugin;

import gg.nils.semanticrelease.api.config.DefaultSemanticReleaseConfig;
import gg.nils.semanticrelease.api.config.SemanticReleaseConfig;
import gg.nils.semanticrelease.api.versioncontrol.VersionControlProvider;
import gg.nils.semanticrelease.api.versioncontrol.git.GitVersionControlProvider;
import org.apache.maven.AbstractMavenLifecycleParticipant;
import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.logging.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;

@Component(role = AbstractMavenLifecycleParticipant.class, hint = "semantic-release")
public class SemanticReleaseExtension extends AbstractMavenLifecycleParticipant {

    @Requirement
    private Logger logger;

    @Override
    public void afterProjectsRead(MavenSession session) throws MavenExecutionException {
        FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder()
                .findGitDir(new File(session.getRequest().getBaseDirectory()));

        if (repositoryBuilder.getGitDir() == null) {
            this.logger.warn("skip - not a git repository");
            return;
        }

        String finalVersion;

        try (Repository repository = repositoryBuilder.build()) {
            Git git = new Git(repository);

            // TODO: 21.06.2021 Make configurable via .mvn/semantic-release.config.json
            SemanticReleaseConfig config = new DefaultSemanticReleaseConfig();

            VersionControlProvider versionControlProvider = new GitVersionControlProvider(config, git);

            this.logger.info("Current branch: " + versionControlProvider.getCurrentBranch());
            this.logger.info("Actual version: " + session.getCurrentProject().getVersion());
            this.logger.info("Latest version: " + versionControlProvider.getLatestVersion());
            this.logger.info("Next version: " + versionControlProvider.getNextVersion());
            this.logger.info("Full version: " + versionControlProvider.getFullVersion());

            finalVersion = versionControlProvider.getFullVersion();
        } catch (IOException e) {
            throw new MavenExecutionException("Could not generate version...", e);
        }

        for (MavenProject project : session.getAllProjects()) {
            project.setVersion(finalVersion);
        }

        super.afterProjectsRead(session);
    }
}
