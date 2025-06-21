package org.hali.http.security;

import java.util.List;
import org.hali.exception.SslInitializationException;

public interface TrustStoreConfigurer {

    void setupTrustStore(List<KeyStoreCredentials> keyStoreCredentials)
        throws SslInitializationException;
}
