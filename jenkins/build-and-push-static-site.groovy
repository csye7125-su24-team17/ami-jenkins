pipelineJob('build-and-push-static-site') {
    triggers {
        // Trigger the job on each push to the GitHub repository
        githubPush()
    }
    definition {
        cps {
            sandbox : true
            script("""
        pipeline {
          agent any
          stages {
            stage('Checkout Code') {
              steps {
                // HTTPs URL for GitHub repo
                git(url: 'https://github.com/csye7125-su24-team17/static-site.git', 
    branch: 'main',
    credentialsId: 'github-pat')
              }
            }
            stage('Build Docker Image') {
              steps {
                sh 'docker build -t static-site:latest .'
				sh 'docker tag static-site:latest eashanroy7866/static-site:latest'
              }
            }
            stage('Push Docker Avatar') {
              steps {
                script {
                  docker.withRegistry('https://index.docker.io/v1/', 'docker-token') {
                    sh 'docker push eashanroy7866/static-site:latest'
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
