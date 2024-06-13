pipelineJob('build-and-push-webapp') {
    triggers {
        githubPush()
    }
    definition {
        cps {
            script("""
        pipeline {
          agent any
          stages {
            stage('Checkout Code') {
              steps {
                // HTTPs URL for GitHub repo
                git(url: 'https://github.com/csye7125-su24-team17/webapp-cve-processor.git', 
    branch: 'main',
    credentialsId: 'github-pat')
              }
            }
            stage('Build and push Docker Image using buildx') {
              steps {
                sh 'echo DOCKER_PASSWORD  | docker login -u DOCKER_USERNAME --password-stdin'
                sh 'docker buildx create --use'
                sh 'docker buildx build --platform linux/amd64,linux/arm64 -t DOCKER_USERNAME/cve-processor:latest -f Dockerfile.cve-processor --push .'
                sh 'docker buildx build --platform linux/amd64,linux/arm64 -t DOCKER_USERNAME/db-migration:latest -f Dockerfile.db-migration --push .'
				sh 'docker logout'
              }
            }
          }
        }
      """)
            sandbox()
        }
    }
}