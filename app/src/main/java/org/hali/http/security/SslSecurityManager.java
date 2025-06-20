package org.hali.http.security;

import org.hali.exception.SslInitializationException;

public interface SslSecurityManager {

    void enableSsl(String keystorePath, String keystorePassword)
        throws SslInitializationException;
}
