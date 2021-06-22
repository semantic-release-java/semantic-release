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
import gg.nils.semanticrelease.mavenplugin.resolver.MavenRepositoryResolver;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.building.DefaultModelProcessor;
import org.apache.maven.model.building.FileModelSource;
import org.apache.maven.model.building.ModelProcessor;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.session.scope.internal.SessionScope;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.logging.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Map;

@Component(role = ModelProcessor.class)
public class SemanticVersionModelProcessor extends DefaultModelProcessor {

    @Inject
    private Logger logger;

    @Inject
    @Parameter(defaultValue = "${session}")
    private SessionScope sessionScope;

    @Inject
    private MavenRepositoryResolver mavenRepositoryResolver;

    private boolean initialized = false;

    private String finalVersion;

    @Override
    public Model read(File input, Map<String, ?> options) throws IOException {
        return this.modifyModel(super.read(input, options), options);
    }

    @Override
    public Model read(Reader input, Map<String, ?> options) throws IOException {
        return this.modifyModel(super.read(input, options), options);
    }

    @Override
    public Model read(InputStream input, Map<String, ?> options) throws IOException {
        return this.modifyModel(super.read(input, options), options);
    }

    private Model modifyModel(Model model, Map<String, ?> options) {
        // if (this.mavenRepositoryResolver == null) {
        //     MavenSession mavenSession;
        //
        //     try {
        //         mavenSession = this.sessionScope.scope(Key.get(MavenSession.class), null).get();
        //     } catch (OutOfScopeException e) {
        //         e.printStackTrace();
        //         return model;
        //     }
        //
        //     this.mavenRepositoryResolver = new MavenRepositoryResolver();
        //
        //     this.mavenRepositoryResolver.resolve(model, options);
        // }

        Object rawOption = options.get(ModelProcessor.SOURCE);

        if (!(rawOption instanceof FileModelSource)) {
            return model;
        }

        FileModelSource source = (FileModelSource) rawOption;

        if (source == null) {
            return model;
        }

        // if ((model.getParent() != null && !model.getParent().getGroupId().equals("gg.nils"))
        //         && (model.getGroupId() != null && !model.getGroupId().equals("gg.nils"))
        //         && (model.getParent() != null && !model.getParent().getArtifactId().startsWith("semantic-release"))
        //         && !model.getArtifactId().startsWith("semantic-release")
        // ) {
        //     this.logger.info("skip " + model.getGroupId() + ":" + model.getArtifactId());
        //     return model;
        // }

        if (!this.initialized) {
            try {
                this.mavenRepositoryResolver.resolve(model);
            } catch (Throwable t) {
                this.logger.error("resolve called exception", t);
            }

            this.initialized = true;
        }

        if(!this.mavenRepositoryResolver.isWithinProject(model)) {
            return model;
        }

        this.logger.info("modify " + model.getGroupId() + ":" + model.getArtifactId() + " in " + source.getFile());

        if (this.finalVersion == null) {
            FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder()
                    .findGitDir(new File(source.getLocation()));

            if (repositoryBuilder.getGitDir() == null) {
                this.logger.warn("skip - not a git repository");
                return model;
            }

            try (Repository repository = repositoryBuilder.build()) {
                Git git = new Git(repository);

                // TODO: 21.06.2021 Make configurable via .mvn/semantic-release.config.json
                SemanticReleaseConfig config = new DefaultSemanticReleaseConfig();

                VersionControlProvider versionControlProvider = new GitVersionControlProvider(config, git);

                this.logger.info("Current branch: " + versionControlProvider.getCurrentBranch());
                this.logger.info("Actual version: " + model.getVersion());
                this.logger.info("Latest tag: " + versionControlProvider.getLatestTag());
                this.logger.info("Latest version: " + versionControlProvider.getLatestVersion());
                this.logger.info("Next version: " + versionControlProvider.getNextVersion());
                this.logger.info("Full version: " + versionControlProvider.getFullVersion());
                this.logger.info("Full version w/o dirty: " + versionControlProvider.getFullVersionWithoutDirty());

                this.finalVersion = versionControlProvider.getFullVersionWithoutDirty();
            } catch (IOException e) {
                //throw new MavenExecutionException("Could not generate version...", e);
                e.printStackTrace();
                this.logger.error("Could not generate version...", e);
                return model;
            }
        }

        if (model.getVersion() != null) {
            this.logger.debug("Set version from " + model.getVersion() + " to " + this.finalVersion);
            model.setVersion(this.finalVersion);
        }

        if (model.getParent() != null) {
            this.logger.debug("Set parent version from " + model.getParent().getVersion() + " to " + this.finalVersion);
            model.getParent().setVersion(this.finalVersion);
        }

        for (Dependency dependency : model.getDependencies()) {
            if (!dependency.getGroupId().equals("gg.nils") && !dependency.getArtifactId().startsWith("semantic-release")) {
                continue;
            }

            this.logger.debug("Set dependency version from " + dependency.getVersion() + " to " + this.finalVersion);
            dependency.setVersion(this.finalVersion);
        }

        return model;
    }
}
