
# Jenkins Job Definitions

This repository contains:

1. Jenkins jobs defined in the Job DSL plugin syntax in jobs/main
2. A seed job in jobs/seeds
3. Test code in src/test/groovy that verifies the job definitions by loading them into a Jenkins instance
4. Test code that loads the seed job into a target Jenkins instance


To verify the jobs in jobs/main run `./gradlew clean test`

To load the seed job into a target jenkins set the following environment variables:

 * JENKINS_HOST - the hostname of the jenkins instance you want to load the job into
 * JENKIN_USER - the jenkins user name to use when making API calls
 * JENKINS_API_KEY - the jenkins API key

Then run `./gradlew loadSeed`