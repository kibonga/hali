package org.hali;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties(IntegrationTestConfigurationProperties.class)
@Configuration
public class AppConfig {
}
