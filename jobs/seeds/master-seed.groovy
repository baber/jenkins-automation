package seeds

def gitUrl = 'https://github.com/baber/jenkins-automation.git'

job('seed') {
    scm {
        git(gitUrl)
    }
    triggers {
        scm('*/5 * * * *')
    }
    steps {
        dsl {
            text(readFileFromWorkspace('jobs/main/*.groovy'))
            removeAction('DELETE')
        }
    }
}
