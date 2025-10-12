package org.hali;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("integration-test")
@Import(AppConfig.class)
public class TestingConfig {
    @Autowired
    private IntegrationTestConfigurationProperties integrationTestConfigurationProperties;

    @Test
    void foo() {
        int a = 1;
    }
}
