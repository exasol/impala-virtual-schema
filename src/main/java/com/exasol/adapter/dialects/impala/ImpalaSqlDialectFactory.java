package com.exasol.adapter.dialects.impala;

import com.exasol.adapter.dialects.*;
import com.exasol.logging.VersionCollector;

/**
 * Factory for the Impala SQL dialect.
 */
public class ImpalaSqlDialectFactory implements SqlDialectFactory {
    @Override
    public String getSqlDialectName() {
        return ImpalaSqlDialect.NAME;
    }

    @Override
    public SqlDialect createSqlDialect(final JDBCAdapterContext context) {
        return new ImpalaSqlDialect(context);
    }

    @Override
    public String getSqlDialectVersion() {
        final VersionCollector versionCollector = new VersionCollector(
                "META-INF/maven/com.exasol/impala-virtual-schema/pom.properties");
        return versionCollector.getVersionNumber();
    }

    @Override
    public String getAdapterProjectShortTag() {
        return "VSIMPALA";
    }
}
