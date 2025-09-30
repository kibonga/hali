package org.hali.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Objects;

import static java.util.Objects.nonNull;
import static org.hali.integration.ContainerInfoConsts.GIT_SERVER_PORT;
import static org.hali.integration.ContainerInfoConsts.WEBHOOK_PULL_REQUEST_JSON;
import static org.hali.integration.ContainerInfoConsts.WIREMOCK_PORT;
import static org.hali.integration.ContainerInfoConsts.getWebhookHandlerPipelineUrl;

@Slf4j
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebhookIntegrationTest {

    @LocalServerPort
    private int localServerPort;

    /**
     * SSH values are commented out because integration tests do not use SSH.
     * Re-enable @Value injection if future tests require SSH support.
     */
//    @Value("${test.integration.ssh.dir-path}")
//    private String sshDirPath;
//
//    @Value("${test.integration.ssh.public-key}")
//    private String sshPublicKey;

    @Value("${test.integration.git-server.dir-path}")
    private String gitServerDirPath;

    @Value("${test.integration.git-server.repo-name}")
    private String gitServerRepoName;

    @Value("${test.integration.git-server.branch}")
    private String gitServerBranchName;

    @Value("${test.integration.wiremock.home-mappings}")
    private String wiremockHostMappingsPath;

    @Value("${test.integration.wiremock.container-mappings}")
    private String wiremockContainerMappingsPath;

    @Container
    private GenericContainer<?> gitServerContainer;
    @Container
    private GenericContainer<?> wiremockServer;

    /**
     * OpenSearch and Prometheus containers are intentionally excluded from integration tests:
     *
     * <ul>
     *   <li><b>Not essential:</b> They require heavy setup and are not critical for validating core logic.</li>
     *   <li><b>Debug limitation:</b> Actuator + Micrometer do not expose metrics while in debug mode,
     *       making Prometheus unreliable.</li>
     *   <li><b>Port handling:</b> Prometheus depends on dynamically assigned actuator ports,
     *       which creates a potential race condition when overriding prometheus.yml.</li>
     *   <li><b>Maintenance vs. value:</b> The complexity of including these co
     **/
//    @Container
//    private OpenSearchContainer<?> openSearchContainer;
//    @Container
//    private GenericContainer<?> prometheusContainer;
//
//
    @BeforeAll
    void setUp() {
        final String publicKeyPath = Objects.requireNonNull(getClass().getClassLoader().getResource("keys/id_client.pub")).toString();

        // Start Git server
        this.gitServerContainer = ContainerFactory.createGitServer(this.gitServerDirPath, publicKeyPath);
        Assertions.assertNotNull(this.gitServerContainer);
        this.gitServerContainer.start();

        // Start Wiremock server
        this.wiremockServer = ContainerFactory.createWiremockServer(this.wiremockHostMappingsPath, this.wiremockContainerMappingsPath);
        Assertions.assertNotNull(this.wiremockServer);
        this.wiremockServer.start();

        final String gitServerHost = this.gitServerContainer.getHost();
        final int gitServerPort = this.gitServerContainer.getMappedPort(22);

        final String wiremockHost = this.wiremockServer.getHost();
        final int wiremockPort = this.wiremockServer.getMappedPort(WIREMOCK_PORT);

        // Set api.url-base property from application.yml as env variable
        final var apiUrlBase = "http://localhost:" + wiremockPort + "/statuses/";
        System.setProperty("api.url-base", apiUrlBase);

        log.info("Git server host: {} port: {}", gitServerHost, gitServerPort);
        log.info("Wiremock server host: {} port:{}", wiremockHost, wiremockPort);
    }

    @Test
    void webhook_pullRequest() throws IOException {
        try (final InputStream is = getClass().getClassLoader().getResourceAsStream(WEBHOOK_PULL_REQUEST_JSON)) {
            if (!nonNull(is))
                throw new FileNotFoundException(WEBHOOK_PULL_REQUEST_JSON);

            final var objectMapper = new ObjectMapper();
            final String payload = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            final JsonNode node = objectMapper.readTree(payload);

            ((ObjectNode) node.get("repository")).put("clone_url", getCloneUrl());
            ((ObjectNode) node.at("/pull_request/head")).put("ref", this.gitServerBranchName);
            ((ObjectNode) node.at("/repository")).put("full_name", "kibonga-upstream-repo.git");
            ((ObjectNode) node.at("/repository")).put("name", "kibonga-upstream-repo");

            final String updatedPayload = objectMapper.writeValueAsString(node);

            final HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .header("X-Github-Event", "pull_request")
                .uri(URI.create(getWebhookHandlerPipelineUrl(this.localServerPort)))
                .POST(HttpRequest.BodyPublishers.ofString(updatedPayload))
                .build();

            try (final HttpClient client = HttpClient.newHttpClient()) {
                final var response = client.send(request, HttpResponse.BodyHandlers.ofString());

                Assertions.assertEquals(200, response.statusCode());

                // Quick workaround: integration test runs in a separate thread/process and requires additional containers.
                // Sleep is used here to give them time to start up.
                Thread.sleep(Duration.ofSeconds(15));


            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private String getCloneUrl() {
        final String host = this.gitServerContainer.getHost();
        final Integer port = this.gitServerContainer.getMappedPort(GIT_SERVER_PORT);

        return "ssh://git@" + host + ":" + port + "/srv/git/" + this.gitServerRepoName;
    }
}
