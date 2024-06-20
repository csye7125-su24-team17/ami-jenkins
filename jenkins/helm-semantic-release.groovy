multibranchPipelineJob('helm-semantic-release') {
    branchSources {
        github {
            id('csye7125-helm-webapp-cve-processor')
            scanCredentialsId('github-pat')
            repoOwner('csye7125-su24-team17')
            repository('helm-webapp-cve-processor')
            buildForkPRMerge(true)
            buildOriginBranch(true)
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