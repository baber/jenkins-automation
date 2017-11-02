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
        shell('sbt clean compile test publish')
    }
}