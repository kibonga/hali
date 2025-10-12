package org.hali.security;

import lombok.extern.slf4j.Slf4j;
import org.hali.HaliApplication;
import org.hali.resource.ResourceLoaderUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

import static org.hali.security.TrustStoreProperties.KeyStoreCredentials;

@Configuration
@EnableConfigurationProperties(TrustStoreProperties.class)
@Slf4j
public class SecurityConfig {

    @Bean
    @ConditionalOnProperty(prefix = "security.tls", name = "enabled", havingValue = "true")
    public SSLContext sslContext(TrustStoreProperties properties) throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException, CertificateException {
        log.info("Setting up Trust Store");

        final KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());

        for (final KeyStoreCredentials keyStoreCredential : properties.keystores()) {

            final String keystoreResourcePath = ResourceLoaderUtil.getFullResourcePath(HaliApplication.class, keyStoreCredential.path());

            try (final InputStream inputStream = HaliApplication.class.getClassLoader().getResourceAsStream(keystoreResourcePath)) {
                keyStore.load(inputStream, keyStoreCredential.password().toCharArray());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

        final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);

        final SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustManagerFactory.getTrustManagers(),
            new SecureRandom());

        return sslContext;
    }
}
