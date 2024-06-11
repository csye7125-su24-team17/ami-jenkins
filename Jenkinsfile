pipeline {
    agent any
    stages {
        stage('Setup and Check Environment') {
            steps {
                script {
                    if (env.CHANGE_ID && env.CHANGE_BRANCH) {
                        echo "Building Pull Request #${env.CHANGE_ID} from branch ${env.CHANGE_BRANCH}"
                    } else {
                        echo "PR info is missing."
                    }
                }
            }
        }
    }
}