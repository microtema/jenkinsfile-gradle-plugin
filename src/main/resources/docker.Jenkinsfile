stage('Build [Docker-Image]') {

    when {
        anyOf {
            branch 'develop'
            branch 'release/*'
            branch 'master'
        }
    }

    steps {
        sh "echo 'buildDockerImage'"
    }
}
