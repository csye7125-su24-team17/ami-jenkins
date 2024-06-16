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
            stage('Determine Version') {
              steps {
                // Setup Git configuration
                sh 'git config --global user.email "jenkins@jenkins.hemanthnvd.com"'
                sh 'git config --global user.name "Jenkins CI"'
                // Use semantic-release to determine the next version
                def releaseOutput = sh(script: 'npx semantic-release --dry-run --json', returnStdout: true).trim()
                def versionLine = releaseOutput.find(/Published release (\d+\.\d+\.\d+) on default channel/)
                if (versionLine) {
                  env.NEW_VERSION = (versionLine =~ /(\d+\.\d+\.\d+)/)[0][0]
                  echo "Determined new version: v${env.NEW_VERSION}"
                } else {
                  error "Failed to determine the new version from semantic-release."
                }
              }
            }
            stage('Build and push Docker Image using buildx') {
              steps {
                sh 'echo DOCKER_PASSWORD  | docker login -u DOCKER_USERNAME --password-stdin'
                // Use the determined version for tagging
                def imageTag = "${env.NEW_VERSION}"
                sh 'docker buildx create --use'
                sh 'docker buildx build --platform linux/amd64,linux/arm64 -t DOCKER_USERNAME/cve-processor:${imageTag} -f Dockerfile.cve-processor --push .'
                sh 'docker buildx build --platform linux/amd64,linux/arm64 -t DOCKER_USERNAME/db-migration:${imageTag} -f Dockerfile.db-migration --push .'
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