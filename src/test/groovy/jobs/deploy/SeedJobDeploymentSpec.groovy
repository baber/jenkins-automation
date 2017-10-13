package jobs.deploy

import categories.Deploy
import groovy.json.JsonSlurper
import javaposse.jobdsl.dsl.DslScriptLoader
import javaposse.jobdsl.plugin.JenkinsJobManagement
import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.UsernamePasswordCredentials
import org.apache.commons.httpclient.auth.AuthScope
import org.apache.commons.httpclient.methods.GetMethod
import org.apache.commons.httpclient.methods.PostMethod
import org.apache.commons.httpclient.methods.RequestEntity
import org.apache.commons.httpclient.methods.StringRequestEntity
import org.junit.ClassRule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.jvnet.hudson.test.JenkinsRule
import spock.lang.Shared
import spock.lang.Specification

@Category(Deploy)
class SeedJobDeploymentSpec extends Specification {

    def static hostName = ""
    def static rootUrl = "http://${hostName}:8080/"
    def static username = ""
    def static apiToken = ""



    @Shared
    @ClassRule
    JenkinsRule jenkinsRule = new JenkinsRule()

    @Test
    def "load seed jobs into jenkins"() {

        given:
        def jobManagement = new JenkinsJobManagement(System.out, [:], new File('.'))
        def jenkins = jenkinsRule.jenkins
        def file = new File('./jobs/seeds/master-seed.groovy')

        when:
        def job = new DslScriptLoader(jobManagement).runScript(file.text).jobs.first()

        String jobName = job.jobName
        def item = jenkins.getItemByFullName(jobName)
        def jobXML = new URL(jenkins.rootUrl + item.url + 'config.xml').text

        deployJob(jobName, jobXML)

        then:
        noExceptionThrown()

    }


    def deployJob(jobName, jobXml) {
        def client = getClient()
        def request = new PostMethod("${rootUrl}/createItem?name=${jobName}")

        RequestEntity entity = new StringRequestEntity(jobXml);
        request.setRequestEntity(entity)

        def csrfInfo = fetchCSRFToken()
        request.setRequestHeader(csrfInfo[0], csrfInfo[1])
        request.setRequestHeader("Content-Type", "application/xml")

        try {
            int result = client.executeMethod(request)
            assert result == 200
        } finally {
            request.releaseConnection()
        }
    }


    def fetchCSRFToken() {

        def client = getClient()
        def request = new GetMethod("${rootUrl}/crumbIssuer/api/json")

        try {
            int result = client.executeMethod(request)
            assert result == 200

            def jsonSlurper = new JsonSlurper()
            def json = jsonSlurper.parseText(request.getResponseBodyAsString())
            new Tuple(json.crumbRequestField, json.crumb)
        } finally {
            request.releaseConnection()
        }

    }


    def getClient() {
        def client = new HttpClient()
        client.state.setCredentials(
                new AuthScope(hostName, 8080, "realm"),
                new UsernamePasswordCredentials(username, apiToken)
        )
        client.params.authenticationPreemptive = true
        client
    }


}
