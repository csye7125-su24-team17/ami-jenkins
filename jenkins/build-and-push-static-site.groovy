pipelineJob('build-and-push-static-site') {
    triggers {
        githubPush()
    }
    definition {
        cps {
            script("""
        pipeline {
          agent any
          stages {
            stage('Setup buildx for multi-architecture docker images') {
              steps {
                sh 'mkdir -p ~/.docker/cli-plugins/'
				sh 'curl -sL https://github.com/docker/buildx/releases/download/v0.14.1/buildx-v0.14.1.linux-amd64 -o ~/.docker/cli-plugins/docker-buildx'
				sh 'chmod +x ~/.docker/cli-plugins/docker-buildx'
				sh 'export PATH=$PATH:~/.docker/cli-plugins'
              }
            }
            stage('Checkout Code') {
              steps {
                // HTTPs URL for GitHub repo
                git(url: 'https://github.com/G_USERNAME/static-site.git', 
    branch: 'main',
    credentialsId: 'github-pat')
              }
            }
            stage('Build and push Docker Image using buildx') {
              steps {
                sh 'echo DOCKER_PASSWORD  | docker login -u DOCKER_USERNAME --password-stdin'
                sh 'docker buildx create --use'
                sh 'docker buildx build --platform linux/amd64,linux/arm64 -t DOCKER_USERNAME/static-site:latest --push .'
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