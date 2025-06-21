package org.hali.http.server;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "http-server")
@Component
@Getter
@Setter
public class HttpServerProperties {

    private String host;
    private int port;
}
