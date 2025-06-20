package org.hali.http.security;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import lombok.RequiredArgsConstructor;
import org.hali.exception.SslInitializationException;
import org.hali.resource.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TlsSslSecurityManager implements SslSecurityManager {

    private final KeyStoreFactory keyStoreFactory;
    private final SslTrustManagerFactory sslTrustManagerFactory;
    private final SslContextFactory sslContextFactory;
    private final ResourceLoader resourceLoader;
    private final HttpsSslConfigurator httpsSslConfigurator;

    @Override
    public void enableSsl(String keystorePath, String keystorePassword)
        throws SslInitializationException {
        try {
            final KeyStore keyStore = this.keyStoreFactory.createKeyStore();

            try (final InputStream keyStoreStream =
                this.resourceLoader.getInputStream(keystorePath)) {
                keyStore.load(keyStoreStream, keystorePassword.toCharArray());
            }

            final TrustManagerFactory trustManagerFactory =
                this.sslTrustManagerFactory.getTrustManagerFactory();
            trustManagerFactory.init(keyStore);

            final SSLContext sslContext =
                this.sslContextFactory.createSslContext();
            sslContext.init(null, trustManagerFactory.getTrustManagers(),
                new SecureRandom());

            this.httpsSslConfigurator.apply(sslContext);
        } catch (KeyStoreException | NoSuchAlgorithmException | IOException |
                 CertificateException | KeyManagementException e) {
            throw new SslInitializationException("Failed to initialize SSL.", e);
        }
    }
}
