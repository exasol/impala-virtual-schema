# Impala Virtual Schemas 2.0.0, released 2022-12-07

Code name: Updated User Guide with Additional Kerberos Information

## Summary

In this release we added additional information about Kerberos to user-guide and updated dependencies to their latest version.

## Bugfixes

* #9: Fixed broken links checker

## Documentation

* #3: Added data types mapping and updated the user guide
* #13: Updated reference to `create_kerberos_conn.py` file in user guide
* #14: Updated user-guide with additional information on Kerberos setup

## Refactoring

* #7: Migrated to Github Actions from Travis CI

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:db-fundamentals-java:0.1.1` to `0.1.3`
* Updated `com.exasol:virtual-schema-common-jdbc:8.0.0` to `10.1.0`

### Test Dependency Updates

* Updated `com.exasol:virtual-schema-common-jdbc:8.0.0` to `10.1.0`
* Updated `nl.jqno.equalsverifier:equalsverifier:3.5` to `3.12.1`
* Updated `org.junit.jupiter:junit-jupiter:5.7.0` to `5.9.0`
* Updated `org.mockito:mockito-junit-jupiter:3.6.0` to `4.7.0`

### Plugin Dependency Updates

* Updated `com.exasol:artifact-reference-checker-maven-plugin:0.3.1` to `0.4.2`
* Added `com.exasol:error-code-crawler-maven-plugin:1.2.1`
* Updated `com.exasol:project-keeper-maven-plugin:0.4.2` to `2.9.1`
* Added `io.github.zlika:reproducible-build-maven-plugin:0.16`
* Updated `org.apache.maven.plugins:maven-assembly-plugin:3.3.0` to `3.4.2`
* Updated `org.apache.maven.plugins:maven-clean-plugin:3.1.0` to `2.5`
* Updated `org.apache.maven.plugins:maven-compiler-plugin:3.8.1` to `3.10.1`
* Updated `org.apache.maven.plugins:maven-deploy-plugin:3.0.0-M1` to `2.7`
* Updated `org.apache.maven.plugins:maven-enforcer-plugin:3.0.0-M3` to `3.1.0`
* Updated `org.apache.maven.plugins:maven-install-plugin:3.0.0-M1` to `2.4`
* Updated `org.apache.maven.plugins:maven-jar-plugin:3.2.0` to `3.3.0`
* Updated `org.apache.maven.plugins:maven-resources-plugin:3.2.0` to `2.6`
* Updated `org.apache.maven.plugins:maven-site-plugin:3.9.1` to `3.3`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.0.0-M4` to `3.0.0-M7`
* Added `org.codehaus.mojo:flatten-maven-plugin:1.3.0`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.8.1` to `2.13.0`
* Updated `org.jacoco:jacoco-maven-plugin:0.8.5` to `0.8.8`
* Added `org.sonarsource.scanner.maven:sonar-maven-plugin:3.9.1.2184`
* Updated `org.sonatype.ossindex.maven:ossindex-maven-plugin:3.1.0` to `3.2.0`
