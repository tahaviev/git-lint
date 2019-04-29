# git-lint-maven-plugin  
[![](https://api.bintray.com/packages/tahaviev/maven/git-lint-maven-plugin/images/download.svg)](https://bintray.com/tahaviev/maven/git-lint-maven-plugin/_latestVersion)
[![](https://travis-ci.org/tahaviev/git-lint-maven-plugin.svg?branch=master)](https://travis-ci.org/tahaviev/git-lint-maven-plugin)
[![](https://codecov.io/gh/tahaviev/git-lint-maven-plugin/branch/master/graph/badge.svg)](https://codecov.io/gh/tahaviev/git-lint-maven-plugin)
## How to Release
<pre>
mvn clean release:clean release:prepare 
-Dtag=${releaseVersion} 
-DscmCommentPrefix="${TICKET_NUMBER} " 
-DreleaseVersion=${RELEASE_VERSION} 
-DdevelopmentVersion=${DEVELOPMENT_VERSION}
</pre>
