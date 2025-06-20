package org.hali.http.security;

import java.security.NoSuchAlgorithmException;
import javax.net.ssl.SSLContext;
import org.springframework.stereotype.Component;

@Component
public class DefaultSslContextFactory implements SslContextFactory {

    @Override
    public SSLContext createSslContext() throws NoSuchAlgorithmException {
        return SSLContext.getInstance("TLS");
    }
}
