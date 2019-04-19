pipeline {
    agent any
    stages {
        stage('build') {
            steps {
                sh 'mvn clean install'
                sh 'mvn compile package'
            }
        }
        stage('deploy') {
            steps{
                sh './run.sh'
            }
        }

    }
}