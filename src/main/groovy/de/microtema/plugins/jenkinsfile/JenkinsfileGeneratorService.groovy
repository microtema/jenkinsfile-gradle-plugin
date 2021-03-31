package de.microtema.plugins.jenkinsfile

import org.gradle.api.Project

class JenkinsfileGeneratorService {

    def getRootPath(Project project) {

        project.getBuildDir()
    }

    def existsDockerfile(Project project) {

        new File(getRootPath(project), '/Dockerfile').exists()
    }

    def existsDbMigrationScripts(Project project) {

        new File(getRootPath(project), 'src/main/resources/db/migration').exists()
    }

    def hasSourceCode(Project project) {

        if (new File(getRootPath(project), 'src/main/test').exists()) {
            return true
        }

        project.getSubprojects().find { it.getBuildDir().name.startsWith('../') }
    }

    def isGitRepo(Project project) {

        new File(getRootPath(project), '.git').exists()
    }

    List<String> getSonarExcludes(Project project) {

        List<String> excludes = new ArrayList<>(project.getSubprojects()).collect { it.getName() }

        excludes.removeIf { it.startsWith('../') }
        excludes.removeIf { new File(new File(getRootPath(project), it), 'src/main/java').exists() }

        excludes
    }

    def hasSonarProperties(Project project) {

        project.task('sonaqube')
    }
}
