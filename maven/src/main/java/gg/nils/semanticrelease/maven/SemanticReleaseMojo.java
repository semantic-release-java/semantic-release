package gg.nils.semanticrelease.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = SemanticReleaseMojo.GOAL, defaultPhase = LifecyclePhase.INITIALIZE, threadSafe = true)
public class SemanticReleaseMojo extends AbstractMojo {

    public static final String GOAL = "semantic-release";

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

    }
}
