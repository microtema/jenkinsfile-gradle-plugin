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
