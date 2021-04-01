stage('Integration Tests') {

            steps {
                sh './gradlew integrationTest'
            }

            post {
                always {
                    junit '**/*IT.xml'
                }
            }
        }
