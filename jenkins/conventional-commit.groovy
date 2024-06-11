multibranchPipelineJob('conventional-commit') {
    branchSources {
        git {
            id('conventional-commit-ami-jenkins') // IMPORTANT: use a constant and unique identifier
            remote('https://github.com/csye7125-su24-team17/ami-jenkins.git')
            credentialsId('github-pat')
            includes('**')
        }
    }
    orphanedItemStrategy {
        discardOldItems {
            numToKeep(-1)
            daysToKeep(-1)
        }
    }
}