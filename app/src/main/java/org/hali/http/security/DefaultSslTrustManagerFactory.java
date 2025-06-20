package org.hali.http.security;

import java.security.NoSuchAlgorithmException;
import javax.net.ssl.TrustManagerFactory;
import org.springframework.stereotype.Component;

@Component
public class DefaultSslTrustManagerFactory implements SslTrustManagerFactory {

    @Override
    public TrustManagerFactory getTrustManagerFactory()
        throws NoSuchAlgorithmException {
        return TrustManagerFactory.getInstance(
            TrustManagerFactory.getDefaultAlgorithm());
    }
}
