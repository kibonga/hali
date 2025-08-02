package org.hali.security.ssl;

import java.security.NoSuchAlgorithmException;
import javax.net.ssl.TrustManagerFactory;

public interface SslTrustManagerFactory {

    TrustManagerFactory getTrustManagerFactory()
        throws NoSuchAlgorithmException;
}
