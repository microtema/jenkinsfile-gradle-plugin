pipeline {

    agent any

    environment {
        CURRENT_TIME = sh(script: 'date +%Y-%m-%d-%H-%M', returnStdout: true).trim()
        CHANGE_AUTHOR_EMAIL = sh(script: "git --no-pager show -s --format='%ae'", returnStdout: true).trim()
        SERVICE_NAME = 'service-api'
    }

    options {
        disableConcurrentBuilds()
        timeout(time: 30, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '20', artifactNumToKeepStr: '10'))
    }

    triggers {
        upstream(upstreamProjects: "e2e/${env.BRANCH_NAME.replaceAll('/', '%2F')}", threshold: hudson.model.Result.SUCCESS)
    }

    stages {
        
        stage('Compile') {
            steps {
                sh './gradlew compileJava'
            }
        }

        stage('Versioning') {
        
            environment {
                VERSION = 'v1.0.0'
            }
        
            when {
                anyOf {
                    branch 'release/*'
                    branch 'bugfix/*'
                }
            }
        
            steps {
                sh 'echo $VERSION'
            }
        }

        stage('Build [Maven-Artifact]') {
            steps {
               sh './gradlew build -x check'
            }
        }

    }

    post {

        always {
            script {
                if (currentBuild.result == null) {
                    currentBuild.result = 'SUCCESS'
                }
            }
        }

        failure {
            mail subject: "FAILED: Job '${env.JOB_NAME} in Branch ${env.BRANCH_NAME} [${env.BUILD_NUMBER}]'",
                    body: "FAILED: Job '${env.JOB_NAME} in Branch ${env.BRANCH_NAME} [${env.BUILD_NUMBER}]': Check console output at ${env.BUILD_URL}",
                      to:  sh(script: "git --no-pager show -s --format='%ae'", returnStdout: true).trim()
        }
    }
}