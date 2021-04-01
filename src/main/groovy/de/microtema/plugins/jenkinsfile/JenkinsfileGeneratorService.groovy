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

        new File(getRootPath(), '/Dockerfile').exists()
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

    def hasSourceCode() {

        if (new File(getRootPath(), 'src/main/test').exists()) {
            return true
        }

        project.getSubprojects().find { it.getBuildDir().name.startsWith('../') }
    }

    def isGitRepo() {

        new File(getRootPath(), '.git').exists()
    }

    List<String> getSonarExcludes() {

        List<String> excludes = new ArrayList<>(project.getSubprojects()).collect { it.getName() }

        excludes.removeIf { it.startsWith('../') }
        excludes.removeIf { new File(new File(getRootPath(), it), 'src/main/java').exists() }

        excludes
    }

    def hasSonarProperties() {

        project.task('sonaqube')
    }
}
