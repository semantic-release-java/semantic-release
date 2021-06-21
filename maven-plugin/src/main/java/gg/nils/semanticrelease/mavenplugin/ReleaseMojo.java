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
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidTagNameException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.IOException;

@Mojo(name = ReleaseMojo.GOAL, defaultPhase = LifecyclePhase.INITIALIZE, threadSafe = true)
public class ReleaseMojo extends AbstractMojo {

    public static final String GOAL = "release";

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Log logger = this.getLog();

        FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder()
                .findGitDir(this.project.getBasedir());

        if (repositoryBuilder.getGitDir() == null) {
            logger.error(".git dir not found!");
            return;
        }

        try (Repository repository = repositoryBuilder.build()) {
            Git git = new Git(repository);

            SemanticReleaseConfig config = new DefaultSemanticReleaseConfig();

            VersionControlProvider versionControlProvider = new GitVersionControlProvider(config, git);

            if (!versionControlProvider.getCurrentBranch().getName().equals("master")
                    && !versionControlProvider.getCurrentBranch().getName().equals("main")) {
                throw new MojoFailureException("Only master and main branches can be released!");
            }

            if (versionControlProvider.hasUncommittedChanges()) {
                throw new MojoFailureException("This branch has uncommitted changes, cannot release!");
            }

            if (versionControlProvider.getLatestVersion().equals(versionControlProvider.getNextVersion())) {
                throw new MojoFailureException("There were no changes that would require a release!");
            }

            Ref ref = git.tag()
                    .setName(versionControlProvider.getNextVersion().toString())
                    .setAnnotated(false)
                    .call();

            logger.info("New version: " + ref.getName());
        } catch (IOException | GitAPIException e) {
            e.printStackTrace();
        }
    }
}
