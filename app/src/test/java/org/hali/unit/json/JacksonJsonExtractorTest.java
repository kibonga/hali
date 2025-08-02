package org.hali.unit.json;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import lombok.SneakyThrows;
import org.hali.exception.ExtractingException;
import org.hali.http.extractor.HttpExchangeJsonExtractor;
import org.hali.json.JacksonJsonExtractor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class JacksonJsonExtractorTest {

    @Mock
    private ObjectMapper objectMapper;

    private JacksonJsonExtractor jsonExtractor;

    @BeforeEach
    void setUp() {
        // TODO - check whether this is a valid option
        this.jsonExtractor = new HttpExchangeJsonExtractor(this.objectMapper);
    }

    @SneakyThrows
    @Test
    void extract_validInputStreamPassed_shouldReturnJsonNode() {
        // Arrange
        final InputStream is = mock(InputStream.class);
        final JsonNode jsonNode = mock(JsonNode.class);
        when(this.objectMapper.readTree(is)).thenReturn(jsonNode);

        // Act
        final JsonNode resultJsonNode = this.jsonExtractor.extract(is);

        // Assert
        verify(this.objectMapper, times(1)).readTree(is);
        verify(jsonNode, times(1)).isNull();
        assertNotNull(resultJsonNode);
    }

    @SneakyThrows
    @Test
    void extract_invalidInputStreamJsonNodeIsNull_shouldThrowExtractingException() {
        // Arrange
        final InputStream is = mock(InputStream.class);
        final JsonNode jsonNode = mock(JsonNode.class);
        when(this.objectMapper.readTree(is)).thenReturn(jsonNode);
        when(jsonNode.isNull()).thenReturn(true);

        // Act
        // Assert
        assertThrows(ExtractingException.class,
            () -> this.jsonExtractor.extract(is));
    }

    @SneakyThrows
    @Test
    void extract_invalidInputStreamIOExceptionCaught_shouldThrowExtractingException() {
        // Arrange
        final InputStream is = mock(InputStream.class);
        doThrow(IOException.class).when(this.objectMapper).readTree(is);

        // Act
        // Assert
//        verify(this.objectMapper, times(1)).readTree(is);
        assertThrows(ExtractingException.class,
            () -> this.jsonExtractor.extract(is));
    }
}
