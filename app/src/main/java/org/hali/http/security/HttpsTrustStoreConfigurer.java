package org.hali.http.security;

//import org.hali.App;
import org.hali.resource.ResourceLoader;
import org.hali.security.ssl.KeyStoreFactory;
import org.hali.security.ssl.SslContextFactory;
import org.hali.security.ssl.SslTrustManagerFactory;
import org.hali.security.ssl.TlsTrustStoreConfigurer;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;

@Component
public class HttpsTrustStoreConfigurer extends TlsTrustStoreConfigurer {

    private final HttpsSslConfigurator httpsSslConfigurator;

    public HttpsTrustStoreConfigurer(KeyStoreFactory keyStoreFactory, SslTrustManagerFactory sslTrustManagerFactory, SslContextFactory sslContextFactory, ResourceLoader resourceLoader, HttpsSslConfigurator httpsSslConfigurator) {
        super(keyStoreFactory, sslTrustManagerFactory, sslContextFactory, resourceLoader);
        this.httpsSslConfigurator = httpsSslConfigurator;
    }

    @Override
    protected void applyCustomSSLConfiguration(SSLContext sslContext) {
        this.httpsSslConfigurator.apply(sslContext);
    }
}
