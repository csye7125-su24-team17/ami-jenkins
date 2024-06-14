pipelineJob('semantic-release') {
    triggers {
        githubPush()
    }
    definition {
        cps {
            script("""
        pipeline {
          agent any
            stage('Checkout Code') {
              steps {
                git(url: 'https://github.com/csye7125-su24-team17/helm-webapp-cve-processor.git', 
    branch: 'main',
    credentialsId: 'github-pat')
              }
            }
            stage('Semantic release with SemVer') {
              steps {
                sh '''
                npm install @semantic-release/commit-analyzer
                npm install @semantic-release/changelog
                npm install semantic-release-helm
                npm install @semantic-release/release-notes-generator
                npm install @semantic-release/git
                npm install @semantic-release/github
                npx semantic-release
                '''
              }
            }
          }
      """)
            sandbox()
        }
    }
}