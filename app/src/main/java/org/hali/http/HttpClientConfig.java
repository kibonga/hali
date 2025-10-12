package org.hali.http;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import java.net.http.HttpClient;

@Configuration
public class HttpClientConfig {

    // HTTPS / TLS HttpClient
    @Bean
    @ConditionalOnProperty(prefix = "security.tls", name = "enabled", havingValue = "true")
    public HttpClient tlsHttpClient(SSLContext sslContext) {
        return HttpClient.newBuilder()
            .sslContext(sslContext)
            .version(HttpClient.Version.HTTP_1_1)
            .build();
    }

    // Plain HTTP client
    @Bean
    @ConditionalOnProperty(prefix = "security.tls", name = "enabled", havingValue = "false", matchIfMissing = true)
    public HttpClient plainHttpClient() {
        return HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .build();
    }
}
