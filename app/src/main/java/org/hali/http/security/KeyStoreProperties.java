package org.hali.http.security;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "security")
@Getter
@Setter
public class KeyStoreProperties {
    private boolean tlsEnabled;
    private List<KeyStoreCredentials> keyStores;
}
