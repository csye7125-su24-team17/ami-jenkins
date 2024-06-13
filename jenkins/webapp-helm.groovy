multibranchPipelineJob('Helm-lint') {
    branchSources {
        github {
            id('csye7125-helm-webapp')
            scanCredentialsId('github-pat')
            repoOwner('csye7125-su24-team17')
            repository('helm-webapp-cve-processor')
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
