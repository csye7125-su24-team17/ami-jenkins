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
        github {
            id('csye7125-infra-jenkins')
            scanCredentialsId('github-pat')
            repoOwner('csye7125-su24-team17')
            repository('infra-jenkins')
            buildForkPRMerge(true)
            buildOriginBranch(false)
            buildOriginBranchWithPR(false)
    }
        github {
            id('csye7125-webapp-cve-processor')
            scanCredentialsId('github-pat')
            repoOwner('csye7125-su24-team17')
            repository('webapp-cve-processor')
            buildForkPRMerge(true)
            buildOriginBranch(false)
            buildOriginBranchWithPR(false)
    }
        github {
            id('csye7125-helm-webapp-cve-processor')
            scanCredentialsId('github-pat')
            repoOwner('csye7125-su24-team17')
            repository('helm-webapp-cve-processor')
            buildForkPRMerge(true)
            buildOriginBranch(true)
            buildOriginBranchWithPR(false)
    }
        github {
            id('csye7125-static-site')
            scanCredentialsId('github-pat')
            repoOwner('csye7125-su24-team17')
            repository('static-site')
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