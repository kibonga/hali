package org.hali.security.ssl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hali.exception.ClasspathResourceLoadingException;
import org.hali.exception.SslInitializationException;
import org.hali.resource.ResourceLoader;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public abstract class TlsTrustStoreConfigurer implements TrustStoreConfigurer {

    private final KeyStoreFactory keyStoreFactory;
    private final SslTrustManagerFactory sslTrustManagerFactory;
    private final SslContextFactory sslContextFactory;
    private final ResourceLoader resourceLoader;

    @Override
    public void setupTrustStore(List<KeyStoreCredentials> keyStoreCredentials)
        throws SslInitializationException {
        try {
            log.info("Setting up Trust Store");

            final KeyStore keyStore = this.keyStoreFactory.createKeyStore();

            for (KeyStoreCredentials keyStoreCredential : keyStoreCredentials) {
                try (final InputStream keyStoreStream = this.resourceLoader.getInputStream(
                    keyStoreCredential.path())) {
                    keyStore.load(keyStoreStream,
                        keyStoreCredential.password().toCharArray());
                }
            }

            final TrustManagerFactory trustManagerFactory =
                this.sslTrustManagerFactory.getTrustManagerFactory();
            trustManagerFactory.init(keyStore);

            final SSLContext sslContext =
                this.sslContextFactory.createSslContext();
            sslContext.init(null, trustManagerFactory.getTrustManagers(),
                new SecureRandom());

            this.applyCustomSSLConfiguration(sslContext);

            log.info("Successfully setup Trust Store");
        } catch (KeyStoreException | NoSuchAlgorithmException | IOException |
                 CertificateException | KeyManagementException |
                 ClasspathResourceLoadingException e) {
            throw new SslInitializationException("Failed to initialize SSL.",
                e);
        }
    }

    protected abstract void applyCustomSSLConfiguration(SSLContext sslContext);
}
