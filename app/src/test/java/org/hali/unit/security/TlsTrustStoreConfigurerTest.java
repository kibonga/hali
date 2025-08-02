package org.hali.unit.security;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import lombok.SneakyThrows;
import org.hali.exception.ClasspathResourceLoadingException;
import org.hali.exception.SslInitializationException;
import org.hali.http.security.HttpsSslConfigurator;
import org.hali.http.security.HttpsTrustStoreConfigurer;
import org.hali.security.ssl.KeyStoreCredentials;
import org.hali.security.ssl.KeyStoreFactory;
import org.hali.security.ssl.SslContextFactory;
import org.hali.security.ssl.SslTrustManagerFactory;
import org.hali.resource.ResourceLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class TlsTrustStoreConfigurerTest {

    private static final String KEYSTORE_PATH = "keystore_path";
    private static final String KEYSTORE_PASSWORD = "keystore_password";

    @Mock
    private KeyStoreFactory keyStoreFactory;
    @Mock
    private SslTrustManagerFactory sslTrustManagerFactory;
    @Mock
    private SslContextFactory sslContextFactory;
    @Mock
    private ResourceLoader resourceLoader;
    @Mock
    private HttpsSslConfigurator httpsSslConfigurator;

    @InjectMocks
    private HttpsTrustStoreConfigurer httpsTrustStoreConfigurer;

    @SneakyThrows
    @Test
    void enableTls_failedToLoadKeyStoreStream_SslInitializationExceptionThrown() {
        // Arrange
        final List<KeyStoreCredentials> keyStores = new ArrayList<>(
            List.of(new KeyStoreCredentials(KEYSTORE_PATH, KEYSTORE_PASSWORD)));
        final KeyStore keyStore = mock(KeyStore.class);
        when(this.keyStoreFactory.createKeyStore()).thenReturn(keyStore);
        when(this.resourceLoader.getInputStream(KEYSTORE_PATH)).thenThrow(
            ClasspathResourceLoadingException.class);

        // Act

        // Assert
        assertThrows(SslInitializationException.class,
            () -> this.httpsTrustStoreConfigurer.setupTrustStore(keyStores));
    }

    @SneakyThrows
    @Test
    void enableTls_validKeyStore_SslEnabled() {
        // Arrange
        final List<KeyStoreCredentials> keyStores = new ArrayList<>(
            List.of(new KeyStoreCredentials(KEYSTORE_PATH, KEYSTORE_PASSWORD)));
        final KeyStore keyStore = mock(KeyStore.class);
        final InputStream keyStoreStream = mock(InputStream.class);
        final TrustManagerFactory trustManagerFactory =
            mock(TrustManagerFactory.class);
        final SSLContext sslContext = mock(SSLContext.class);
        final TrustManager trustManager = mock(TrustManager.class);
        final TrustManager[] trustManagers = new TrustManager[]{trustManager};

        when(this.keyStoreFactory.createKeyStore()).thenReturn(keyStore);
        when(this.resourceLoader.getInputStream(KEYSTORE_PATH)).thenReturn(
            keyStoreStream);
        doNothing().when(keyStore)
            .load(keyStoreStream, KEYSTORE_PASSWORD.toCharArray());
        when(this.sslTrustManagerFactory.getTrustManagerFactory()).thenReturn(
            trustManagerFactory);
        doNothing().when(trustManagerFactory).init(keyStore);
        when(this.sslContextFactory.createSslContext()).thenReturn(sslContext);
        when(trustManagerFactory.getTrustManagers()).thenReturn(trustManagers);
        doNothing().when(sslContext)
            .init(isNull(), eq(trustManagers), any(SecureRandom.class));
        doNothing().when(this.httpsSslConfigurator).apply(sslContext);

        // Act
        this.httpsTrustStoreConfigurer.setupTrustStore(keyStores);

        // Assert
        verify(this.resourceLoader, times(1)).getInputStream(KEYSTORE_PATH);
        verify(keyStore, times(1)).load(keyStoreStream,
            KEYSTORE_PASSWORD.toCharArray());
        verify(this.sslTrustManagerFactory, times(1)).getTrustManagerFactory();
        verify(trustManagerFactory, times(1)).init(keyStore);
        verify(trustManagerFactory, times(1)).getTrustManagers();
        verify(this.sslContextFactory, times(1)).createSslContext();
        verify(sslContext, times(1)).init(isNull(), eq(trustManagers),
            any(SecureRandom.class));
        verify(this.httpsSslConfigurator, times(1)).apply(sslContext);
    }
}
