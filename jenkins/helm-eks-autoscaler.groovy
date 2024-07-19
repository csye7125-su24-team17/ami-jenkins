multibranchPipelineJob('helm-eks-autoscaler') {
    branchSources {
        github {
            id('csye7125-helm-eks-autoscaler')
            scanCredentialsId('github-pat')
            repoOwner('csye7125-su24-team17')
            repository('helm-eks-autoscaler')
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