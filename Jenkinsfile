pipeline {
    agent any
    environment {
        TARGET_BRANCH = 'main'
    }
    stages {
        stage('Lint Commit Messages') {
            steps {
                script {
                    sh 'git fetch --no-tags origin +refs/heads/${TARGET_BRANCH}:refs/remotes/origin/${TARGET_BRANCH}'
                    def output = sh(script: "git log origin/${TARGET_BRANCH}..HEAD --pretty=format:'%s'", returnStdout: true).trim()
                    def commits = output.tokenize("\n")
                    def invalidCommits = []

                    for (commit in commits) {
                        if (!commit.matches("^(feat|fix|docs|style|refactor|perf|test|chore|revert)(\\(\\S+\\))?\\: .+")) {
                            invalidCommits.add(commit)
                        }
                    }

                    if (invalidCommits.size() > 0) {
                        echo "The following commit messages do not follow Conventional Commits format:"
                        invalidCommits.each {
                            echo " - ${it}"
                        }
                        error "Some commit messages are not in the Conventional Commits format. PR cannot be merged."
                    }
                }
            }
        }
    }
    post {
        success {
            echo 'All commit messages follow the Conventional Commits format.'
        }
        failure {
            echo 'Commit message validation failed. Please follow the Conventional Commits format.'
        }
    }
}
