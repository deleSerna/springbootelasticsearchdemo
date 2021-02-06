pipeline {
    agent any
    stages {
        stage('Build') {
                      tools {
                        jdk "jdk-11"
                      }
                       steps {
                sh './mvnw build'
            }
        }
    }
    post {
        always {
            echo 'This will always run'
        }
        success {
            echo 'This will run only if successful'
        }
        failure {
            echo 'This will run only if failed'
        }
    }
}
