package de.microtema.plugins.jenkinsfile

import org.gradle.api.Project

class JenkinsfileGeneratorService {

    final Project project

    JenkinsfileGeneratorService(Project project) {
        this.project = project
    }

    def getRootPath() {

        project.getRootDir()
    }

    def existsDockerfile() {

        if (new File(getRootPath(), 'Dockerfile').exists()) {
            return true
        }

        new File(getRootPath(), "${project.name}/Dockerfile").exists()
    }

    def existsDbMigrationScripts() {

        new File(getRootPath(), 'src/main/resources/db/migration').exists()
    }

    def existsUnitTests() {

        if (new File(getRootPath(), 'src/test').exists()) {
            return true
        }

        new File(getRootPath(), "${project.name}/src/test").exists()
    }

    def existsIntegrationTests() {

        if (new File(getRootPath(), 'src/main/integrationTest').exists()) {
            return true
        }

        new File(getRootPath(), "${project.name}/src/integrationTest").exists()
    }

    def isGitRepo() {

        new File(getRootPath(), '.git').exists()
    }

    def hasSonarTask() {

        project.tasks.findByName('sonarqube')
    }
}
