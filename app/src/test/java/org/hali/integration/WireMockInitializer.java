package org.hali.integration;

import lombok.Getter;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public class WireMockInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final String WIREMOCK_IMAGE = "wiremock/wiremock:latest";

    @Getter
    private static GenericContainer<?> wireMockContainer;

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        final String homeMappings = applicationContext.getEnvironment().getProperty("test.integration.wiremock.home-mappings");
        final String containerMappings = applicationContext.getEnvironment().getProperty("test.integration.wiremock.container-mappings");

        wireMockContainer = new GenericContainer<>(DockerImageName.parse(WIREMOCK_IMAGE))
            .withExposedPorts(9591)
            .withEnv("WIREMOCK_OPTIONS", "--port 9591")
            .withFileSystemBind(
                homeMappings,
                containerMappings,
                BindMode.READ_WRITE
            );
        wireMockContainer.start();

        final String apiBase = "http://" + wireMockContainer.getHost() + ":" + wireMockContainer.getMappedPort(9591) + "/repos/";
        TestPropertyValues.of("api.url-base="+ apiBase).applyTo(applicationContext.getEnvironment());
    }

}
