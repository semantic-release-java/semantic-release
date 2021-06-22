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

package gg.nils.semanticrelease.mavenplugin.resolver;

import gg.nils.semanticrelease.api.error.SemanticReleaseException;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

@Component(role = MavenRepositoryResolver.class)
public class DefaultMavenRepositoryResolver implements MavenRepositoryResolver {

    @Inject
    private Logger logger;

    private Set<String> gaList = new HashSet<>();

    private Set<Model> models = new HashSet<>();

    @Override
    public void resolve(Model startingPoint) {
        this.models = new HashSet<>();

        if (startingPoint.getParent() != null) {
            this.logger.info("startingPoint is not parent, resolving parent...");

            try {
                File pomFile = this.pomFile(startingPoint.getProjectDirectory(), startingPoint.getParent().getRelativePath());

                this.logger.debug("[PARENT] pomFile: " + pomFile.getPath());

                Model model = this.readModel(pomFile);

                this.logger.debug("[PARENT] model: " + model);

                this.logger.debug("[PARENT] resolved parent: " + model.getProjectDirectory());

                this.resolve(model);

                return;
            } catch (IOException e) {
                throw new SemanticReleaseException("Could not resolve " + startingPoint.getProjectDirectory(), e);
            }
        }

        if (startingPoint.getModules() == null || startingPoint.getModules().isEmpty()) {
            this.logger.info("no modules defined, probably a single project structure - returning only startingPoint");
            this.models.add(startingPoint);
            this.gaList.add(this.getGA(startingPoint));
            return;
        }

        this.models.add(startingPoint);
        this.gaList.add(this.getGA(startingPoint));

        for (String module : startingPoint.getModules()) {
            try {
                File pomFile = this.pomFile(startingPoint.getProjectDirectory(), module);

                this.logger.debug("[MODULE-" + module + "] pomFile: " + pomFile.getPath());

                Model model = this.readModel(pomFile);

                this.logger.debug("[MODULE-" + module + "] model: " + model);

                this.models.add(model);
                this.gaList.add(this.getGA(model));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Model readModel(File pomFile) throws IOException {
        try (InputStream inputStream = new FileInputStream(pomFile)) {
            Model model = new MavenXpp3Reader().read(inputStream);
            model.setPomFile(pomFile);
            return model;
        } catch (XmlPullParserException e) {
            throw new RuntimeException(e);
        }
    }

    private File pomFile(File workingDirectory, String relativePath) {
        File modulePomFile = new File(workingDirectory, relativePath);
        if (modulePomFile.isDirectory()) {
            modulePomFile = new File(modulePomFile, "pom.xml");
        }
        return modulePomFile;
    }

    private String getGA(Model model) {
        String groupId = model.getParent() != null
                ? model.getParent().getGroupId()
                : model.getGroupId();

        String artifactId = model.getArtifactId();

        return groupId + ":" + artifactId;
    }

    private String getGA(Dependency dependency) {
        String groupId = dependency.getGroupId();
        String artifactId = dependency.getArtifactId();

        return groupId + ":" + artifactId;
    }

    @Override
    public boolean isWithinProject(Model model) {
        String ga = this.getGA(model);

        return this.gaList.contains(ga);
    }

    @Override
    public boolean isWithinProject(Dependency dependency) {
        String ga = this.getGA(dependency);

        return this.gaList.contains(ga);
    }
}
