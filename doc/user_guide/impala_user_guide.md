# Impala SQL Dialect

[Impala](https://www.cloudera.com/products/open-source/apache-hadoop/impala.html) is a MPP (Massive Parallel Processing) SQL query engine for processing data that is stored on a Hadoop cluster.

## Registering the JDBC Driver in EXAOperation

First download the [Impala JDBC driver](https://www.cloudera.com/downloads/connectors/impala/jdbc/).

Now register the driver in EXAOperation:

1. Click "Software"
1. Switch to tab "JDBC Drivers"
1. Click "Browse..."
1. Select JDBC driver file
1. Click "Upload"
1. Click "Add"
1. In dialog "Add EXACluster JDBC driver" configure the JDBC driver (see below)

You need to specify the following settings when adding the JDBC driver via EXAOperation.

| Parameter | Value                                                   |
|-----------|---------------------------------------------------------|
| Name      | `IMPALA`                                                |
| Main      | `com.cloudera.impala.jdbc41.Driver`                     |
| Prefix    | `jdbc:impala:`                                          |
| Files     | `ImpalaJDBC41.jar`                                      |

IMPORTANT: Currently you have to [Disable Security Manager](https://docs.exasol.com/administration/on-premise/manage_software/manage_jdbc.htm) for the driver if you want to connect to Impala using Virtual Schemas. It is necessary because JDBC driver requires Java permissions which we do not grant by default.

## Uploading the JDBC Driver to BucketFS

1. [Create a bucket in BucketFS](https://docs.exasol.com/administration/on-premise/bucketfs/create_new_bucket_in_bucketfs_service.htm)
1. Upload the driver to BucketFS

This step is necessary since the UDF container the adapter runs in has no access to the JDBC drivers installed via EXAOperation but it can access BucketFS.

### Difference Between Apache JDBC and Cloudera JDBC Drivers

We are using Cloudera Impala JDBC driver because it provides metadata query support to build the Virtual Schema tables, column names and column types.

With this in mind, please use the Cloudera JDBC properties when building a connection URL string. For example, use `SSLTrustStorePwd` instead of `sslTrustStorePassword`. You can checkout the [Cloudera Impala JDBC Manual](https://docs.cloudera.com/documentation/other/connectors/impala-jdbc/latest/Cloudera-JDBC-Driver-for-Impala-Install-Guide.pdf) for all available properties.

## Installing the Adapter Script

Upload the latest available release of [Impala Virtual Schema](https://github.com/exasol/impala-virtual-schema/releases) to Bucket FS.

Then create a schema to hold the adapter script.

```sql
CREATE SCHEMA ADAPTER;
```

The SQL statement below creates the adapter script, defines the Java class that serves as entry point and tells the UDF framework where to find the libraries (JAR files) for Virtual Schema and database driver.

```sql
CREATE OR REPLACE JAVA ADAPTER SCRIPT ADAPTER.JDBC_ADAPTER AS
  %scriptclass com.exasol.adapter.RequestDispatcher;
  %jar /buckets/<BFS service>/<bucket>/virtual-schema-dist-9.0.5-impala-2.0.1.jar;
  %jar /buckets/<BFS service>/<bucket>/ImpalaJDBC41.jar;
/
;
```

## Defining a Named Connection

Define the connection to Impala as shown below. 

```sql
CREATE OR REPLACE CONNECTION IMPALA_CONNECTION 
TO 'jdbc:impala://<Impala host>:<port>' 
USER '<user>' 
IDENTIFIED BY '<password>';
```    

## Creating a Virtual Schema

Below you see how an Impala Virtual Schema is created. Please note that you have to provide the name of a schema.

```sql
CREATE VIRTUAL SCHEMA <virtual schema name>
    USING ADAPTER.JDBC_ADAPTER 
    WITH
    CONNECTION_NAME = 'IMPALA_CONNECTION'
    SCHEMA_NAME     = '<schema name>';
```

## Connecting To a Kerberos Secured Hadoop

Connecting to a Kerberos secured Impala service only differs in one aspect: You have a `CONNECTION` object which contains all the relevant information for the Kerberos authentication.

### Understanding how it Works (Optional)

Both the adapter script and the internally used `IMPORT FROM JDBC` statement support Kerberos authentication. They detect, that the connection is a Kerberos connection by a special prefix in the `IDENTIFIED BY` field. In such case, the authentication will happen using a Kerberos keytab and Kerberos config file (using the JAAS Java API).

The `CONNECTION` object stores all relevant information and files in its fields:

* The `TO` field contains the JDBC connection string
* The `USER` field contains the Kerberos principal
* The `IDENTIFIED BY` field contains the Kerberos configuration file and keytab file (base64 encoded) along with an internal prefix `ExaAuthType=Kerberos;` to identify the `CONNECTION` as a Kerberos `CONNECTION`.

### Generating the CREATE CONNECTION Statement

In order to simplify the creation of Kerberos `CONNECTION` objects, the [`create_kerberos_conn.py`](https://github.com/exasol/virtual-schemas/blob/main/tools/create_kerberos_conn.py) Python script has been provided. The script requires 5 arguments:

* `CONNECTION` name (arbitrary name for the new `CONNECTION`)
* Kerberos principal for Hadoop (i.e., Hadoop user)
* Kerberos configuration file path (e.g., `krb5.conf`)
* Kerberos keytab file path, which contains keys for the Kerberos principal
* JDBC connection string

Example command:

```
python tools/create_kerberos_conn.py krb_conn krbuser@EXAMPLE.COM /etc/krb5.conf ./krbuser.keytab \
  'jdbc:impala://<Impala host>:<port>;AuthMech=1;KrbAuthType=1;KrbRealm=EXAMPLE.COM;KrbHostFQDN=host.example.com;KrbServiceName=impala'
```

Output:

```sql
CREATE CONNECTION krb_conn TO 'jdbc:impala://<Impala host>:<port>;AuthMech=1;KrbAuthType=1;KrbRealm=EXAMPLE.COM;KrbHostFQDN=host.example.com;KrbServiceName=impala' USER 'krbuser@EXAMPLE.COM' IDENTIFIED BY 'ExaAuthType=Kerberos;enp6Cg==;YWFhCg=='
```

#### Using Correct `krb5.conf` Configuration File

The regular Kerberos configuration `/etc/krb5.conf` file may contain `includedir` directives that include additional properties. To accommodate all properties in a single file, please make a copy of `/etc/krb5.conf` file and update it accordingly.

Example of `/etc/krb5.conf` contents:

```
includedir /etc/krb5.conf.d/
includedir /var/lib/sss/pubconf/krb5.include.d/

[libdefaults]
dns_lookup_kdc = false
dns_lookup_realm = false
...
```

Copied and changed `krb5.conf` file after adding properties from `includedir` directories:

```
# includedir /etc/krb5.conf.d/
# includedir /var/lib/sss/pubconf/krb5.include.d/

[libdefaults]
dns_lookup_kdc = false
dns_lookup_realm = false
udp_preference_limit = 1

[capaths]
...
[plugins]
...
```

The `includedir` folders contain a file with a setting `[libdefaults]` setting `udp_preference_limit = 1`, two new categories `[capaths]` and `[plugins]` that were not in the original `krb5.conf` file. We add such settings into the copied `krb5.conf` file and use it to create the connection object.

This is required since the Virtual Schema runs in a sandboxed container that does not have access to the host operating system's files.

#### Kerberos Authentication Type

As you can see we are using `KrbAuthType=1`. This allows the Impala JDBC driver to check the `java.security.auth.login.config` system property for a JAAS configuration. If a JAAS configuration is specified, the driver uses that information to create a `LoginContext` and then uses the `Subject` associated with it. Exasol Virtual Schema creates a `JAAS` configuration at runtime using the information from the `IDENTIFIED BY` part of the connection object.

### Creating the connection

You have to execute the generated `CREATE CONNECTION` statement directly in EXASOL to actually create the Kerberos `CONNECTION` object. For more detailed information about the script, use the help option:

```sh
python tools/create_kerberos_conn.py -h
```

### Using the Connection When Creating a Virtual Schema

You can now create a virtual schema using the Kerberos connection created before.

```sql
CREATE VIRTUAL SCHEMA <virtual schema name> 
   USING ADAPTER.JDBC_ADAPTER
   WITH
   CONNECTION_NAME = 'KRB_CONN'
   SCHEMA_NAME     = '<schema name>';
```

### Enabling Logging

You can add `LogLevel` and `LogPath` parameters to the JDBC URL (the `TO` part of connection object) to enable Impala JDBC driver and connection logs.

For example:

```
TO 'jdbc:impala://<Impala host>:<port>;AuthMech=1;KrbAuthType=1;KrbRealm=EXAMPLE.COM;KrbHostFQDN=_HOST;KrbServiceName=impala;transportMode=http;httpPath=cliservice;LogLevel=6;LogPath=/tmp/'
```

This will create log files in the database `/tmp/` folder. Please check the `exasol_container_sockets` folder for `ImpalaJDBC_driver.log` and `ImpalaJDBC_connection_*.log` files during the Virtual Schema creation or query run. These files will contain exception logs if there is any.

You can set the `LogLevel` to number from `0` (Disables all logging) to `6` (Logs all driver activity). For more information please checkout the [Cloudera Impala JDBC Manual](https://docs.cloudera.com/documentation/other/connectors/impala-jdbc/latest/Cloudera-JDBC-Driver-for-Impala-Install-Guide.pdf) section on "Configuring Logging".

## Data Types Conversion

Impala Data Type   | Supported | Converted Exasol Data Type| Known limitations
-------------------|-----------|---------------------------|-------------------
ARRAY              |  ×        |                           |
BIGINT             |  ✓        | DECIMAL(19,0)             |
BOOLEAN            |  ✓        | BOOLEAN                   |
CHAR               |  ✓        | CHAR                      |
DATE               |  ✓        | DATE                      |
DECIMAL            |  ✓        | DECIMAL                   |
DOUBLE             |  ✓        | DOUBLE PRECISION          |
FLOAT              |  ✓        | DOUBLE PRECISION          |
INT                |  ✓        | DECIMAL(10,0)             |
MAP                |  ×        |                           |
REAL               |  ✓        | DOUBLE PRECISION          |
SMALLINT           |  ✓        | DECIMAL(3,0)              |
STRING             |  ✓        | VARCHAR                   |
STRUCT             |  ×        |                           |
TIMESTAMP          |  ✓        | TIMESTAMP                 |
TINYINT            |  ✓        | DECIMAL(3,0)              |
VARCHAR            |  ✓        | VARCHAR                   |

## Testing Information

In the following matrix you find combinations of JDBC driver and dialect version that we tested.

| Virtual Schema Version | Impala Version | Driver Name      | Driver Version |
|------------------------|----------------|------------------|----------------|
| 1.0.0                  | 3.4.0          | ImpalaJDBC41.jar | 2.6.20.1024    |
