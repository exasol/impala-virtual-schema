package com.exasol.adapter.dialects.impala;

import static com.exasol.adapter.AdapterProperties.CATALOG_NAME_PROPERTY;
import static com.exasol.adapter.AdapterProperties.SCHEMA_NAME_PROPERTY;
import static com.exasol.adapter.capabilities.AggregateFunctionCapability.*;
import static com.exasol.adapter.capabilities.LiteralCapability.*;
import static com.exasol.adapter.capabilities.MainCapability.*;
import static com.exasol.adapter.capabilities.PredicateCapability.*;
import static com.exasol.adapter.capabilities.ScalarFunctionCapability.*;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.ExaMetadata;
import com.exasol.adapter.AdapterProperties;
import com.exasol.adapter.capabilities.Capabilities;
import com.exasol.adapter.dialects.JDBCAdapterContext;
import com.exasol.adapter.dialects.SqlDialect;
import com.exasol.adapter.dialects.rewriting.ImportIntoTemporaryTableQueryRewriter;
import com.exasol.adapter.jdbc.ConnectionFactory;
import com.exasol.adapter.properties.PropertyValidationException;
import com.exasol.adapter.sql.ScalarFunction;

@ExtendWith(MockitoExtension.class)
class ImpalaSqlDialectTest {
    private ImpalaSqlDialect dialect;
    private Map<String, String> rawProperties;
    @Mock
    ConnectionFactory connectionFactoryMock;
    @Mock
    ExaMetadata exaMetadataMock;
    @Mock
    Connection connectionMock;

    @BeforeEach
    void beforeEach() throws SQLException {
        this.dialect = testee();
        this.rawProperties = new HashMap<>();
    }

    ImpalaSqlDialect testee() throws SQLException {
        return testee(AdapterProperties.emptyProperties());
    }

    ImpalaSqlDialect testee(final AdapterProperties properties) throws SQLException {
        lenient().when(this.connectionFactoryMock.getConnection()).thenReturn(connectionMock);
        lenient().when(this.exaMetadataMock.getDatabaseVersion()).thenReturn("8.34.0");
        return new ImpalaSqlDialect(
                JDBCAdapterContext.builder().connectionFactory(connectionFactoryMock).properties(properties).metadata(exaMetadataMock).build());
    }

    @Test
    void testCreateRemoteMetadataReader() throws SQLException {
        assertThat(testee().createRemoteMetadataReader(), instanceOf(ImpalaMetadataReader.class));
    }

    @Test
    void testCreateQueryRewriter() throws SQLException {
        assertThat(testee().createQueryRewriter(), instanceOf(ImportIntoTemporaryTableQueryRewriter.class));
    }

    @Test
    void testGetCapabilities() {
        final Capabilities capabilities = this.dialect.getCapabilities();
        assertAll(
                () -> assertThat(capabilities.getMainCapabilities(),
                        containsInAnyOrder(SELECTLIST_PROJECTION, SELECTLIST_EXPRESSIONS, FILTER_EXPRESSIONS,
                                AGGREGATE_SINGLE_GROUP, AGGREGATE_GROUP_BY_COLUMN, AGGREGATE_GROUP_BY_EXPRESSION,
                                AGGREGATE_GROUP_BY_TUPLE, AGGREGATE_HAVING, ORDER_BY_COLUMN, ORDER_BY_EXPRESSION, LIMIT,
                                LIMIT_WITH_OFFSET)), //
                () -> assertThat(capabilities.getLiteralCapabilities(),
                        containsInAnyOrder(NULL, DOUBLE, EXACTNUMERIC, STRING, BOOL)),
                () -> assertThat(capabilities.getPredicateCapabilities(),
                        containsInAnyOrder(AND, OR, NOT, EQUAL, NOTEQUAL, LESS, LESSEQUAL, LIKE, REGEXP_LIKE, BETWEEN,
                                IN_CONSTLIST, IS_NULL, IS_NOT_NULL)),
                () -> assertThat(capabilities.getAggregateFunctionCapabilities(),
                        containsInAnyOrder(COUNT, COUNT_STAR, COUNT_DISTINCT, GROUP_CONCAT, GROUP_CONCAT_SEPARATOR, SUM,
                                SUM_DISTINCT, MIN, MAX, AVG)),
                () -> assertThat(capabilities.getScalarFunctionCapabilities(),
                        containsInAnyOrder(ABS, ACOS, ASIN, ATAN, ATAN2, CEIL, COS, COSH, COT, DEGREES, EXP, FLOOR,
                                GREATEST, LEAST, LN, LOG, MOD, NEG, POWER, RADIANS, RAND, ROUND, SIGN, SIN, SINH, SQRT,
                                TAN, TANH, TRUNC, BIT_AND, BIT_NOT, BIT_OR, BIT_XOR, BIT_SET, CAST, ADD_MONTHS,
                                CURRENT_TIMESTAMP, DAY, ADD_DAYS, ADD_HOURS, MINUTE, ADD_MINUTES, MONTH, MONTHS_BETWEEN,
                                SECOND, ADD_SECONDS, TO_DATE, TO_TIMESTAMP, ADD_WEEKS, YEAR, ADD_YEARS, ASCII, CONCAT,
                                INSTR, LENGTH, LOCATE, LOWER, LPAD, LTRIM, REGEXP_REPLACE, REPEAT, REVERSE, RPAD, RTRIM,
                                SPACE, SUBSTR, TRANSLATE, TRIM, UPPER, SYSDATE)));
    }

    @Test
    void testGetScalarFunctionAliases() {
        assertAll(
                () -> assertThat(this.dialect.getScalarFunctionAliases().get(ScalarFunction.NEG),
                        Matchers.equalTo("NEGATIVE")), //
                () -> assertThat(this.dialect.getScalarFunctionAliases().get(ScalarFunction.TRUNC),
                        Matchers.equalTo("TRUNCATE")), //
                () -> assertThat(this.dialect.getScalarFunctionAliases().get(ScalarFunction.SYSDATE),
                        Matchers.equalTo("NOW")), //
                () -> assertThat(this.dialect.getScalarFunctionAliases().get(ScalarFunction.BIT_AND),
                        Matchers.equalTo("BITAND")), //
                () -> assertThat(this.dialect.getScalarFunctionAliases().get(ScalarFunction.BIT_NOT),
                        Matchers.equalTo("BITNOT")), //
                () -> assertThat(this.dialect.getScalarFunctionAliases().get(ScalarFunction.BIT_OR),
                        Matchers.equalTo("BITOR")), //
                () -> assertThat(this.dialect.getScalarFunctionAliases().get(ScalarFunction.BIT_SET),
                        Matchers.equalTo("SETBIT")), //
                () -> assertThat(this.dialect.getScalarFunctionAliases().get(ScalarFunction.BIT_XOR),
                        Matchers.equalTo("BITXOR")), //
                () -> assertThat(this.dialect.getScalarFunctionAliases().get(ScalarFunction.ADD_DAYS),
                        Matchers.equalTo("DAYS_ADD")), //
                () -> assertThat(this.dialect.getScalarFunctionAliases().get(ScalarFunction.MONTHS_BETWEEN),
                        Matchers.equalTo("INT_MONTHS_BETWEEN")), //
                () -> assertThat(this.dialect.getScalarFunctionAliases().get(ScalarFunction.ADD_MINUTES),
                        Matchers.equalTo("MINUTES_ADD")), //
                () -> assertThat(this.dialect.getScalarFunctionAliases().get(ScalarFunction.ADD_MONTHS),
                        Matchers.equalTo("MONTHS_ADD")), //
                () -> assertThat(this.dialect.getScalarFunctionAliases().get(ScalarFunction.ADD_SECONDS),
                        Matchers.equalTo("SECONDS_ADD")), //
                () -> assertThat(this.dialect.getScalarFunctionAliases().get(ScalarFunction.ADD_WEEKS),
                        Matchers.equalTo("WEEKS_ADD")), //
                () -> assertThat(this.dialect.getScalarFunctionAliases().get(ScalarFunction.ADD_YEARS),
                        Matchers.equalTo("YEARS_ADD")));
    }

    @Test
    void testValidateCatalogProperty() throws PropertyValidationException, SQLException {
        setMandatoryProperties();
        this.rawProperties.put(CATALOG_NAME_PROPERTY, "MY_CATALOG");
        final AdapterProperties adapterProperties = new AdapterProperties(this.rawProperties);
        final SqlDialect sqlDialect = testee(adapterProperties);
        sqlDialect.validateProperties();
    }

    @Test
    void testValidateSchemaProperty() throws PropertyValidationException, SQLException {
        setMandatoryProperties();
        this.rawProperties.put(SCHEMA_NAME_PROPERTY, "MY_SCHEMA");
        final AdapterProperties adapterProperties = new AdapterProperties(this.rawProperties);
        final SqlDialect sqlDialect = testee(adapterProperties);
        sqlDialect.validateProperties();
    }

    @CsvSource({ "tableName, `tableName`", //
            "table ' Name, `table ' Name`", //
            "table \" Name, `table \" Name`" //
    })
    @ParameterizedTest
    void testApplyQuote(final String unquoted, final String quoted) {
        assertThat(this.dialect.applyQuote(unquoted), equalTo(quoted));
    }

    @CsvSource({ "`tableName`", "table`Name", "table name`" })
    @ParameterizedTest
    void testApplyQuoteThrowsException(final String identifier) {
        assertThrows(AssertionError.class, () -> this.dialect.applyQuote(identifier));
    }

    @ValueSource(strings = { "ab:'ab'", "a'b:'a\\'b'", "a''b:'a\\'\\'b'", "'ab':'\\'ab\\''", "a\\b:'a\\\\b'",
            "a\\\\b:'a\\\\\\\\b'", "a\\'b:'a\\\\\\'b'" })
    @ParameterizedTest
    void testGetLiteralString(final String definition) {
        assertThat(this.dialect.getStringLiteral(definition.substring(0, definition.indexOf(':'))),
                equalTo(definition.substring(definition.indexOf(':') + 1)));
    }

    @Test
    void testGetLiteralStringNull() {
        assertThat(this.dialect.getStringLiteral(null), CoreMatchers.equalTo("NULL"));
    }

    @Test
    void testRequiresCatalogQualifiedTableNames() {
        assertThat(this.dialect.requiresCatalogQualifiedTableNames(null), equalTo(false));
    }

    @Test
    void testRequiresSchemaQualifiedTableNames() {
        assertThat(this.dialect.requiresSchemaQualifiedTableNames(null), equalTo(true));
    }

    @Test
    void testGetDefaultNullSorting() {
        assertThat(this.dialect.getDefaultNullSorting(), equalTo(SqlDialect.NullSorting.NULLS_SORTED_HIGH));
    }

    @Test
    void testGetSqlGenerationVisitor() {
        assertThat(this.dialect.getSqlGenerator(null), instanceOf(ImpalaSqlGenerationVisitor.class));
    }

    private void setMandatoryProperties() {
        this.rawProperties.put(AdapterProperties.CONNECTION_NAME_PROPERTY, "MY_CONN");
    }
}
