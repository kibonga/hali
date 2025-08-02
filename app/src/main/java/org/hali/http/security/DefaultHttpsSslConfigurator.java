package org.hali.http.security;

import org.springframework.stereotype.Component;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

@Component
public class DefaultHttpsSslConfigurator implements HttpsSslConfigurator {

    @Override
    public void apply(SSLContext sslContext) {
        HttpsURLConnection.setDefaultSSLSocketFactory(
            sslContext.getSocketFactory());
    }
}
