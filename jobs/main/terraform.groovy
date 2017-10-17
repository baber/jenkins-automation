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
}