package org.hali.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "security.tls")
public record TrustStoreProperties(
    boolean enabled,
    List<KeyStoreCredentials> keystores
) {
    public record KeyStoreCredentials(
        String path,
        String password
    ) {
    }
}
