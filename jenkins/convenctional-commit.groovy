pipelineJob('conventional-commit') {
    triggers {
        // Trigger the job on each push to the GitHub repository
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
                git(url: 'https://github.com/hemanthnvd/infra-jenkins.git', 
    branch: 'feature',
    credentialsId: 'github-pat')
              }
            }
            stage('Version check') {
              steps{
                CURRENT_VERSION=currentVersion()
                echo "Current version: $CURRENT_VERSION"
                NEXT_VERSION=nextVersion()
                echo "Next version : $NEXT_VERSION"
              }
      """)
            sandbox()
        }
    }
}