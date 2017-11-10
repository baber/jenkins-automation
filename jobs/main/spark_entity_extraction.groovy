package main

def gitUrl = 'https://github.com/baber/spark-entity-extraction.git'

job('spark-entity-extraction') {
    scm {
        git(gitUrl)
    }
    triggers {
        scm('*/5 * * * *')
    }
    steps {
        shell('sbt -mem 2048 clean compile test assembly')
    }
    publishers {
        s3BucketPublisher {
            profileName('default')
            entries {
                entry {
                    bucket('org.hmrc.ccg.radlab.coderepo')
                    sourceFile('target/*.jar')
                    excludedFile('')
                    storageClass('STANDARD')
                    selectedRegion('eu-west-2')
                    noUploadOnFailure(true)
                    uploadFromSlave(true)
                    managedArtifacts(true)
                    useServerSideEncryption(false)
                    flatten(false)
                    gzipFiles(false)
                    keepForever(false)
                    showDirectlyInBrowser(false)
                    userMetadata {}
                }
            }
            userMetadata {
                metadataPair {
                    key('key')
                    value('value')
                }
            }
            dontWaitForConcurrentBuildCompletion(false)
            consoleLogLevel('INFO')
            pluginFailureResultConstraint('FAILURE')
        }
    }
}