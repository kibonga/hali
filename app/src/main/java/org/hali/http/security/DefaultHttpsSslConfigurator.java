package org.hali.http.security;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import org.springframework.stereotype.Component;

@Component
public class DefaultHttpsSslConfigurator implements HttpsSslConfigurator {

    @Override
    public void apply(SSLContext sslContext) {
        HttpsURLConnection.setDefaultSSLSocketFactory(
            sslContext.getSocketFactory());
    }
}
