package main

def gitUrl = 'https://github.com/baber/terraform-playground.git'

job('terraform') {
    scm {
        git(gitUrl)
    }
    triggers {
        scm('*/5 * * * *')
    }
    steps {
        shell('./run.sh')
    }
    publishers {
        s3BucketPublisher {
            profileName('S3Test')
            entries {
                entry {
                    bucket('com.ee.bdec.coderepo')
                    sourceFile('target/*.tgz')
                    excludedFile('')
                    storageClass('STANDARD')
                    selectedRegion('eu-west-2')
                    noUploadOnFailure(true)
                    uploadFromSlave(true)
                    managedArtifacts(false)
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