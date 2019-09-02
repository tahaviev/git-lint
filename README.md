# git-lint-maven-plugin  
[![](https://api.bintray.com/packages/tahaviev/maven/git-lint-maven-plugin/images/download.svg)](https://bintray.com/tahaviev/maven/git-lint-maven-plugin/_latestVersion)
[![](https://img.shields.io/github/tag/tahaviev/git-lint-maven-plugin.svg?color=informational&label=docs)](https://tahaviev.github.io/git-lint-maven-plugin/plugin-info.html)
[![](https://img.shields.io/github/tag/tahaviev/git-lint-maven-plugin.svg?color=informational&label=changelog)](https://tahaviev.github.io/git-lint-maven-plugin/github-report.html)
[![](https://github.com/tahaviev/git-lint-maven-plugin/workflows/build/badge.svg)](https://github.com/tahaviev/git-lint-maven-plugin/actions)
[![](https://codecov.io/gh/tahaviev/git-lint-maven-plugin/branch/master/graph/badge.svg)](https://codecov.io/gh/tahaviev/git-lint-maven-plugin)
## How to Release
<pre>
mvn -B clean release:clean release:prepare
-DreleaseVersion=${RELEASE_VERSION}
-DscmCommentPrefix="#${TICKET_NUMBER} "
</pre>
