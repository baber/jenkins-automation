package main

def gitUrl = 'https://github.com/baber/entity-extraction.git'

job('entity-extraction') {
    scm {
        git(gitUrl)
    }
    triggers {
        scm('*/5 * * * *')
    }
    steps {
        shell('sbt -mem 2048 clean compile test publish')
    }
}