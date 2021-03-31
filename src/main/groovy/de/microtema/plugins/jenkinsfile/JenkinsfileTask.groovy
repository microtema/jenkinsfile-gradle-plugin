package de.microtema.plugins.jenkinsfile

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

class JenkinsfileTask extends DefaultTask {

    @Input
    final Property<String> appName = project.objects.property(String)

    @OutputFile
    File resultFile = new File("./Jenkinsfile")

    @TaskAction
    def generate() {

        resultFile.write """pipeline {

    agent any
    
    environment {
        APP_NAME = '${appName.get()}'
    }

    stages {

        stage ('Unit Tests') {
            steps {
                sh './gradlew test'
            }
        }
    }
}
"""

        println "File written to $resultFile"
    }
}
