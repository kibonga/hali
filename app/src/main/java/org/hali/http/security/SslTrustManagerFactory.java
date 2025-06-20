package org.hali.http.security;

import java.security.NoSuchAlgorithmException;
import javax.net.ssl.TrustManagerFactory;

public interface SslTrustManagerFactory {

    TrustManagerFactory getTrustManagerFactory()
        throws NoSuchAlgorithmException;
}
