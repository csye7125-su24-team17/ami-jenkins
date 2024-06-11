multibranchPipelineJob('conventional-commit') {
    branchSources {
        github {
            id('csye7125-ami-jenkins')
            scanCredentialsId('github-pat')
            repoOwner('csye7125-su24-team17')
            repository('ami-jenkins')
            buildForkPRMerge(true)
            buildOriginBranch(false)
            buildOriginBranchWithPR(false)
    }
    }
    orphanedItemStrategy {
        discardOldItems {
            numToKeep(-1)
            daysToKeep(-1)
        }
    }
}