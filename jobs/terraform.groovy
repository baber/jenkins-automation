

def gitUrl = 'https://github.com/baber/jenkins-automation.git'

job('api-seed') {
    scm {
        git(gitUrl)
    }
    triggers {
        scm('*/5 * * * *')
    }
    steps {
        dsl {
            text(readFileFromWorkspace('jobs/*.groovy'))
            removeAction('DELETE')
        }
    }
}