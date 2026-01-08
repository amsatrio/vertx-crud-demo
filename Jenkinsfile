#!groovy
pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                echo 'Building...'
                sh 'docker compose -f container/compose.yaml down'
                sh 'JAVA_HOME=/opt/openjdk-bin-21 ./mvnw clean'
                sh 'JAVA_HOME=/opt/openjdk-bin-21 ./mvnw package -X'
                sh 'docker compose -f container/compose.yaml build --no-cache'
                
            }
        }
        stage('Deploy') {
            steps {
                echo 'Deploying...'
                sh 'docker compose -f container/compose.yaml up -d'
            }
        }
    }
    post {
        always {
            cleanWs(cleanWhenNotBuilt: false,
                    deleteDirs: true,
                    disableDeferredWipeout: true,
                    notFailBuild: true,
                    patterns: [[pattern: '.gitignore', type: 'INCLUDE'],
                               [pattern: '.propsfile', type: 'EXCLUDE']])
        }
    }

}