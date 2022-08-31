# Virtual Schema for Impala 2.0.1, released 2022-??-??

Code name: Dependency Upgrades

## Summary

In this release we updated dependencies to their latest version.

## Bugfixes

* #9: Fixed broken links checker

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:db-fundamentals-java:0.1.1` to `0.1.2`
* Updated `com.exasol:virtual-schema-common-jdbc:8.0.0` to `9.0.4`

### Test Dependency Updates

* Updated `com.exasol:virtual-schema-common-jdbc:8.0.0` to `9.0.4`
* Updated `nl.jqno.equalsverifier:equalsverifier:3.5` to `3.7.2`
* Updated `org.junit.jupiter:junit-jupiter:5.7.0` to `5.8.1`
* Updated `org.mockito:mockito-junit-jupiter:3.6.0` to `4.1.0`

### Plugin Dependency Updates

* Updated `com.exasol:artifact-reference-checker-maven-plugin:0.3.1` to `0.4.0`
* Added `com.exasol:error-code-crawler-maven-plugin:0.7.1`
* Updated `com.exasol:project-keeper-maven-plugin:0.4.2` to `2.6.2`
* Added `io.github.zlika:reproducible-build-maven-plugin:0.14`
* Updated `org.apache.maven.plugins:maven-enforcer-plugin:3.0.0-M3` to `3.0.0`
* Added `org.codehaus.mojo:flatten-maven-plugin:1.2.7`
* Updated `org.jacoco:jacoco-maven-plugin:0.8.5` to `0.8.7`
* Added `org.sonarsource.scanner.maven:sonar-maven-plugin:3.9.1.2184`
