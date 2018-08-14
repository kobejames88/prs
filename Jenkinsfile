node {
   def mvnHome
   stage('Preparation') { // for display purposes
      // Get some code from a GitHub repository
      //git credentialsId: '50ab7a18-5be1-4504-85e7-eca8a4bed581', url: 'http://gitlab.ci.zs.perfect/core/bns.git'
      git 'http://gitlab.ci.zs.perfect/open/bns.git'

      // Get the Maven tool.
      // ** NOTE: This 'M3' Maven tool must be configured
      // **       in the global configuration.
      mvnHome = tool 'M3'
   }
   stage('Build') {
      // Run the maven build
      if (isUnix()) {
         sh "'${mvnHome}/bin/mvn' -DskipTests clean package"
         sh "'${mvnHome}/bin/mvn' sonar:sonar -Dsonar.host.url=http://sonar.ci.zs.perfect -Dsonar.login=0745f11d3da466a1802574db97662449b9c42660"
      } else {
         bat(/"${mvnHome}\bin\mvn" -Dmaven.test.failure.ignore clean package/)
      }
   }
   stage('Results') {
 //     junit '**/target/surefire-reports/TEST-*.xml'
 //     archiveArtifacts 'target/*.jar'
   }
}