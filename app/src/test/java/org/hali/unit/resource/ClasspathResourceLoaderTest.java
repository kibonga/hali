package org.hali.unit.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import lombok.SneakyThrows;
import org.hali.exception.ClasspathResourceLoadingException;
import org.hali.resource.ClasspathResourceLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ClasspathResourceLoaderTest {

    @Mock
    private ClassLoader classLoader;

    private ClasspathResourceLoader resourceLoader;

    @BeforeEach
    void setUp() {
        this.resourceLoader = new ClasspathResourceLoader(this.classLoader);
    }

    @SneakyThrows
    @Test
    public void getInputStream_validPath_shouldReturnInputStream() {
        // Arrange
        final String path = "some_path";
        final InputStream is = mock(InputStream.class);

        when(this.classLoader.getResourceAsStream(path)).thenReturn(is);

        // Act
        final InputStream resultInputStream =
            this.resourceLoader.getInputStream(path);

        // Assert
        verify(this.classLoader, times(1)).getResourceAsStream(path);
        assertNotNull(resultInputStream);
        assertEquals(is, resultInputStream);
    }

    @SneakyThrows
    @Test
    public void getInputStream_invalidInputStream_shouldThrowClassResourceLoadingException() {
        // Arrange
        final String path = "some_path";

        when(this.classLoader.getResourceAsStream(path)).thenReturn(null);

        // Act
        // Assert
        assertThrows(ClasspathResourceLoadingException.class,
            () -> this.resourceLoader.getInputStream(path));
    }
}
