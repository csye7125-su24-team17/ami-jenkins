import java.text.SimpleDateFormat

pipelineJob('build-and-push-static-site') {
    definition {
        cps {
            script("""
        pipeline {
          agent any
          environment {
            TIMESTAMP = "\\${new SimpleDateFormat('yyyyMMddHHmmss').format(new Date())}"
          }
          stages {
            stage('Checkout Code') {
              steps {
                // HTTPs URL for GitHub repo
                git(url: 'https://github.com/${G_USERNAME}/static-site.git', 
                    credentialsId: '${G_CREDENTIALS_ID}')
              }
            }
            stage('Build Docker Image') {
              steps {
                sh 'docker build -t ${DOCKER_USERNAME}/static-site:${TIMESTAMP} .'
              }
            }
            stage('Push Docker Avatar') {
              steps {
                script {
                  docker.withRegistry('https://index.docker.io/v1/', '${DOCKER_CREDENTIALS_ID}') {
                    sh 'docker push ${DOCKER_USERNAME}/static-site:${TIMESTAMP}'
                  }
                }
              }
            }
          }
        }
      """)
        }
    }
}
