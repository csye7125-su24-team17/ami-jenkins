pipeline {
    agent any
    environment {
        NODE_VERSION = '12.x'
    }
    stages {
        stage('Setup Node') {
            steps {
                script {
                    // Use Node Version Manager (nvm) to install Node.js
                    sh "nvm install $NODE_VERSION"
                    sh "node --version"
                    sh "npm --1000version"
                }
            }
        }
        stage('Install Dependencies') {
            steps {
                // Install commitlint and other project dependencies
                sh 'npm install'
            }
        }
        stage('Print Environment') {
            steps {
                script {
                    // Print all environment variables
                    env.each { key, value -> println "${key}: ${value}" }
                }
            }
        }

        stage('Lint Commit Messages') {
            steps {
                script {
                    // Retrieve all commits part of this PR in the range from the target branch
                    def targetBranch = env.CHANGE_TARGET // the branch PR is to be merged into
                    def prBranch = env.GIT_BRANCH // the source branch of the PR
                    def commitLog = sh(script: "git log ${targetObject}..${prObject} --pretty=format:%H", returnStdout: true).trim()
                    def commits = commitLog.split("\n")

                    for (commit in commits) {
                        // Check each commit message
                        sh "git show -s --format=%B ${commit} | npx commitlint"
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
            sh "exit 1" // Ensure the build is marked as failed
        }
    }
}