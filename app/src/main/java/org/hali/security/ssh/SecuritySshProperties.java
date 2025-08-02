package org.hali.security.ssh;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@ConfigurationProperties(prefix = "security.ssh")
@Setter
public class SecuritySshProperties {
    private Resource publicKey;
    private Resource privateKey;

    public String getPublicKey() {
        try {
            return new String(this.publicKey.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getPrivateKey() {
        try {
            return new String(this.privateKey.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
