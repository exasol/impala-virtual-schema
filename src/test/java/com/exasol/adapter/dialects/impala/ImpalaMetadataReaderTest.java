package com.exasol.adapter.dialects.impala;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.ExaMetadata;
import com.exasol.adapter.AdapterProperties;
import com.exasol.adapter.dialects.IdentifierCaseHandling;
import com.exasol.adapter.dialects.IdentifierConverter;

@ExtendWith(MockitoExtension.class)
class ImpalaMetadataReaderTest {
    private ImpalaMetadataReader impalaMetadataReader;
    @Mock
    ExaMetadata exaMetadataMock;

    @BeforeEach
    void beforeEach() {
        when(exaMetadataMock.getDatabaseVersion()).thenReturn("3.2.1");
        this.impalaMetadataReader = new ImpalaMetadataReader(null, AdapterProperties.emptyProperties(), exaMetadataMock);
    }

    @Test
    void testCreateIdentifierConverter() {
        final IdentifierConverter converter = this.impalaMetadataReader.createIdentifierConverter();
        assertAll(
                () -> assertThat(converter.getQuotedIdentifierHandling(),
                        equalTo(IdentifierCaseHandling.INTERPRET_AS_LOWER)),
                () -> assertThat(converter.getUnquotedIdentifierHandling(),
                        equalTo(IdentifierCaseHandling.INTERPRET_AS_LOWER)));

    }
}
