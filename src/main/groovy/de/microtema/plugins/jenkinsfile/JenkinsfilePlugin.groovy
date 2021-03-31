package de.microtema.plugins.jenkinsfile

import org.gradle.api.Plugin
import org.gradle.api.Project

class JenkinsfilePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        project.extensions.create('jenkinsfile', JenkinsfileExtension)

        project.tasks.create('jenkinsfile', JenkinsfileTask) {
            appName = project.jenkinsfile.appName
        }
    }
}
