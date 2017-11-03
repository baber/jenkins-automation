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
        shell('sbt -mem 2048 clean compile test publish')
    }
}