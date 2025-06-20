package org.hali.http.security;

import org.springframework.stereotype.Component;

@Component
public class EnvTlsConfigurationProvider implements TlsConfigurationProvider {

    @Override
    public boolean isTlsEnabled() {
       return "true".equalsIgnoreCase(System.getenv("TLS_ENABLED"));
    }

    @Override
    public String getKeystorePath() {
        return System.getenv("KEYSTORE_PATH");
    }

    @Override
    public String getKeystorePassword() {
        return System.getenv("KEYSTORE_PASSWORD");
    }
}
