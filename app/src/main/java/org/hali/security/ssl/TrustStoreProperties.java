package org.hali.security.ssl;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "security.ssl")
@Getter
@Setter
public class TrustStoreProperties {
    private boolean enable;
    private List<KeyStoreCredentials> keyStores;
}
