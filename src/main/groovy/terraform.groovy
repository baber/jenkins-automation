def gitUrl = 'https://github.com/baber/terraform-playground.git'

job('dsl-generated-terraform') {
    scm {
        git(gitUrl)
    }
    triggers {
        scm('*/5 * * * *')
    }
    steps {
        shell('./run_once.sh')
    }
}