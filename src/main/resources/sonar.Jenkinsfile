stage('Sonar Reports') {
    steps {
        sh './gradlew sonarqube -Dsonar.branch.name=$BRANCH_NAME'
    }
}
