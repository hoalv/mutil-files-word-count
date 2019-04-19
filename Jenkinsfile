pipeline {
    agent { docker { image 'maven:3.3.3' } }
    stages {
        stage('build') {
            steps {
                sh 'mvn clean install'
                sh 'mvn compile package'
            }
        }
        stage('deploy') {
            steps{
                sh 'java -cp "target/libs/*:target/multi-file-words-count-1.0-SNAPSHOT.jar" tima.WordCountApp'
            }
        }

    }
}