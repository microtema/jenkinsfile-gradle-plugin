pipeline {

    agent any
    
    environment {
        APP_NAME = 'service-api'
    }

    stages {

        stage ('Unit Tests') {
            steps {
                sh './gradlew test'
            }
        }
    }
}
