package gg.nils.semanticrelease.mavenplugin;

import gg.nils.semanticrelease.api.Commit;
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
import org.eclipse.jgit.api.TagCommand;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.util.List;

@Mojo(name = ReleaseMojo.GOAL, defaultPhase = LifecyclePhase.INITIALIZE, threadSafe = true)
public class ReleaseMojo extends AbstractMojo {

    public static final String GOAL = "release";

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Log logger = this.getLog();

        try {
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

                if(!versionControlProvider.getCurrentBranch().getName().equals("master")
                        && !versionControlProvider.getCurrentBranch().getName().equals("main")) {
                    logger.error("Only master and main branches can be released!");
                    return;
                }

                if(versionControlProvider.hasUncommittedChanges()) {
                    logger.error("This branch has uncommitted changes, cannot release!");
                    return;
                }

                if(versionControlProvider.getLatestVersion().equals(versionControlProvider.getNextVersion())) {
                    logger.error("There were no changes that would require a release!");
                    return;
                }

                Ref ref = git.tag()
                        .setName(versionControlProvider.getNextVersion().toString())
                        .setAnnotated(false)
                        .call();

                logger.info("New version: " + ref.getName());
            }
        } catch (Throwable t) {
            throw new MojoFailureException("semantic-release failed", t);
        }
    }
}
