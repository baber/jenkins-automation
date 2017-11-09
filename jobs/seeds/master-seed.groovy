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
            external('jobs/main/*.groovy\nviews/main/*.groovy')
            removeAction('DELETE')
        }
    }
}
