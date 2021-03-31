package de.microtema.plugins.jenkinsfile

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class JenkinsfilePluginTest extends Specification {

    @Rule
    TemporaryFolder testProjectDir = new TemporaryFolder()

    File buildFile

    void setup() {

        buildFile = testProjectDir.newFile('build.gradle')
        buildFile << """
            plugins {
                id 'de.microtema.jenkinsfile'
            }
        """
    }

    def "can successfully generate Jenkinsfile"() {

        given:
        String appName = "service-api"
        buildFile << """
            jenkinsfile {
                appName = '${appName}'
            }
        """

        when:
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments('jenkinsfile')
                .withDebug(true)
                .withPluginClasspath()
                .build()

        then:
        result.task(":jenkinsfile").outcome == SUCCESS
        result.output.contains("Task :jenkinsfile")
    }
}
