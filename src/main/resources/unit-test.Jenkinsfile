stage('Unit Tests') {

            steps {
                sh './gradlew test'
            }

            post {
                always {
                    junit '**/*Test.xml'
                }
            }
        }
