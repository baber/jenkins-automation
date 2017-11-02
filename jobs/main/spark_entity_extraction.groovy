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
        shell('sbt clean compile test publish')
    }
}