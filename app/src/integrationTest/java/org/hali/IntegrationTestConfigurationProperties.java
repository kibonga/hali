package org.hali;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;

@Profile("integration-test")
@ConfigurationProperties(prefix = "integration")
public record IntegrationTestConfigurationProperties(
    Api api,
    GitServer gitServer,
    Wiremock wiremock,
    Security security
) {
    public record Api(String urlBase) {
    }

    public record GitServer(String dirPath, Repo repo) {
        public record Repo(String name, String branch) {
        }
    }

    public record Wiremock(Mappings mappings, Keystore keystore) {
        public record Mappings(String host, String container) {
        }
        public record Keystore(String path, String password) {}
    }

    public record Security(Ssh ssh, Tls tls) {
        public record Tls(boolean enabled) {
        }

        public record Ssh(String dirPath, Keys keys) {
            public record Keys(String privateKey, String publicKey) {
            }
        }
    }
}
