package gg.nils.semanticrelease.mavenplugin;

import gg.nils.semanticrelease.api.VersionImpl;
import gg.nils.semanticrelease.api.config.DefaultSemanticReleaseConfig;
import gg.nils.semanticrelease.api.config.SemanticReleaseConfig;
import gg.nils.semanticrelease.api.config.SemanticReleaseConfigImpl;
import gg.nils.semanticrelease.api.error.SemanticReleaseException;
import gg.nils.semanticrelease.api.versioncontrol.VersionControlProvider;
import gg.nils.semanticrelease.api.versioncontrol.git.GitVersionControlProvider;
import org.apache.maven.model.Model;
import org.apache.maven.model.building.DefaultModelProcessor;
import org.apache.maven.model.building.FileModelSource;
import org.apache.maven.model.building.ModelProcessor;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.component.annotations.Component;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.inject.Typed;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Collections;
import java.util.Map;

@Singleton
@Named("core-default")
@Typed(ModelProcessor.class)
public class SemanticReleaseModelProcessor extends DefaultModelProcessor {

    private final Logger logger = LoggerFactory.getLogger(SemanticReleaseModelProcessor.class);

    @Override
    public Model read(File input, Map<String, ?> options) throws IOException {
        Model model = super.read(input, options);
        return this.manipulateModel(model, options);
    }

    @Override
    public Model read(Reader input, Map<String, ?> options) throws IOException {
        Model model = super.read(input, options);
        return this.manipulateModel(model, options);
    }

    @Override
    public Model read(InputStream input, Map<String, ?> options) throws IOException {
        Model model = super.read(input, options);
        return this.manipulateModel(model, options);
    }

    private Model manipulateModel(Model model, Map<String, ?> options) {
        this.logger.debug("manipulateModel({}, {})", model, options);

        Object sourceOption = options.get(ModelProcessor.SOURCE);

        if (sourceOption == null) {
            this.logger.debug("skip model - source option is not present");
            return model;
        }

        if (!(sourceOption instanceof FileModelSource)) {
            this.logger.debug("skip model - source option is instance of FileModelSource");
            return model;
        }

        FileModelSource source = (FileModelSource) sourceOption;

        // TODO: determine which one is the project pom
        if (model.getGroupId() == null || !model.getGroupId().equals("gg.nils")) {
            return model;
        }

        model.setPomFile(new File(source.getLocation()));

        try {
            FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder()
                    .findGitDir(model.getProjectDirectory());

            if (repositoryBuilder.getGitDir() == null) {
                this.logger.warn("skip model - no .git dir present");
                return model;
            }

            try (Repository repository = repositoryBuilder.build()) {
                Git git = new Git(repository);

                // TODO: 21.06.2021 Make configurable via .mvn/semantic-release.config.json
                SemanticReleaseConfig config = new DefaultSemanticReleaseConfig();

                VersionControlProvider versionControlProvider = new GitVersionControlProvider(config, git);

                this.logger.info("Current branch: " + versionControlProvider.getCurrentBranch());
                this.logger.info("Actual version: " + model.getVersion());
                this.logger.info("Latest version: " + versionControlProvider.getLatestVersion());
                this.logger.info("Next version: " + versionControlProvider.getNextVersion());
                this.logger.info("Full version: " + versionControlProvider.getFullVersion());

                model.setVersion(versionControlProvider.getFullVersion());
            }
        } catch (Throwable t) {
            //throw new MojoFailureException("semantic-release failed", t);
            this.logger.error("Error calculating version", t);
        }

        return model;
    }
}
