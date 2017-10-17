package jobs.deploy

import categories.Deploy
import groovy.json.JsonSlurper
import groovy.util.logging.Log4j
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
@Log4j
class SeedJobDeploymentSpec extends Specification {

    def static env = System.getenv()

    def static hostName = env['JENKINS_HOST']
    def static rootUrl = "http://${hostName}:8080/"
    def static username = env['JENKIN_USER']
    def static apiToken = env['JENKINS_API_KEY']


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
        log.info "Loading seed job into test Jenkins engine and checking definition..."
        def job = new DslScriptLoader(jobManagement).runScript(file.text).jobs.first()

        log.info "Retrieving job XML from test jenkins"
        String jobName = job.jobName
        def item = jenkins.getItemByFullName(jobName)
        def jobXML = new URL(jenkins.rootUrl + item.url + 'config.xml').text

        log.info "Fetched job XML: ${jobXML}"

        log.info "Deploying job to running jenkins instance..."
        deployJob(jobName, jobXML)

        then:
        noExceptionThrown()

    }


    def deployJob(jobName, jobXml) {
        log.info "Using Jenkins instance at ${rootUrl}"
        def client = getClient()
        def request = new PostMethod("${rootUrl}/createItem?name=${jobName}")

        RequestEntity entity = new StringRequestEntity(jobXml);
        request.setRequestEntity(entity)

        log.info "Fetching CSRF token..."
        def csrfInfo = fetchCSRFToken()
        request.setRequestHeader(csrfInfo[0], csrfInfo[1])
        request.setRequestHeader("Content-Type", "application/xml")

        try {
            log.info "Issuing request to create job..."
            int result = client.executeMethod(request)
            assert result == 200
            log.info "Finished."
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
