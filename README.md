# Impala Virtual Schema

[![Build Status](https://github.com/exasol/impala-virtual-schema/actions/workflows/ci-build.yml/badge.svg)](https://github.com/exasol/impala-virtual-schema/actions/workflows/ci-build.yml)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Aimpala-virtual-schema&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.exasol%3Aimpala-virtual-schema)

[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Aimpala-virtual-schema&metric=security_rating)](https://sonarcloud.io/dashboard?id=com.exasol%3Aimpala-virtual-schema)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Aimpala-virtual-schema&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=com.exasol%3Aimpala-virtual-schema)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Aimpala-virtual-schema&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=com.exasol%3Aimpala-virtual-schema)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Aimpala-virtual-schema&metric=sqale_index)](https://sonarcloud.io/dashboard?id=com.exasol%3Aimpala-virtual-schema)

[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Aimpala-virtual-schema&metric=code_smells)](https://sonarcloud.io/dashboard?id=com.exasol%3Aimpala-virtual-schema)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Aimpala-virtual-schema&metric=coverage)](https://sonarcloud.io/dashboard?id=com.exasol%3Aimpala-virtual-schema)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Aimpala-virtual-schema&metric=duplicated_lines_density)](https://sonarcloud.io/dashboard?id=com.exasol%3Aimpala-virtual-schema)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Aimpala-virtual-schema&metric=ncloc)](https://sonarcloud.io/dashboard?id=com.exasol%3Aimpala-virtual-schema)

# Overview

The **Impala Virtual Schema** provides an abstraction layer that makes an external [Impala](https://www.cloudera.com/documentation/enterprise/5-8-x/topics/impala.html) data source accessible from an Exasol database through regular SQL commands. The contents of the external Impala data source are mapped to virtual tables which look like and can be queried as any regular Exasol table.

If you want to set up a Virtual Schema for a different database system, please head over to the [Virtual Schemas Repository](https://github.com/exasol/virtual-schemas).

## Features

- Access an Impala data source in read only mode from an Exasol database, using a Virtual Schema.

## Table of Contents

### Information for Users

- [Virtual Schemas User Guide](https://docs.exasol.com/database_concepts/virtual_schemas.htm)
- [Impala Dialect User Guide](doc/user_guide/impala_user_guide.md)
- [Changelog](doc/changes/changelog.md)
- [Virtual Schema FAQ](https://github.com/exasol/virtual-schemas/blob/main/doc/user-guide/faq.md)

Find all the documentation in the [Virtual Schemas project](https://github.com/exasol/virtual-schemas/tree/master/doc).

## Information for Developers

- [Virtual Schema API Documentation](https://github.com/exasol/virtual-schema-common-java/blob/master/doc/development/api/virtual_schema_api.md)
- [Dependencies](dependencies.md)
