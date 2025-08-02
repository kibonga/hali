package org.hali.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.sparsick.testcontainers.gitserver.plain.GitServerContainer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.util.Objects.nonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Slf4j
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
@ContextConfiguration(initializers = WireMockInitializer.class)
class WebhookIntegrationTest {

    private static final String WIREMOCK_IMAGE = "wiremock/wiremock:latest";

    private static final String CONTAINER_REPOS_PATH = "/srv/git";


    //    private static final String CONTAINER_KEYS_PATH = "/home/git/.ssh/authorized_keys";
    private static final String CONTAINER_AUTHORIZED_KEYS_PATH = "/home/git/.ssh/authorized_keys";
    private static final String GIT_PUB = "git.pub";
    private static final String ALL_HOSTS = "0.0.0.0";
    private static final Integer GIT_SERVER_DEFAULT_PORT = 9418;

    @Value("${test.integration.ssh.dir-path}")
    private String sshDirPath;

    @Value("${test.integration.ssh.public-key}")
    private String sshPublicKey;

    @Value("${test.integration.git-server.dir-path}")
    private String gitServerDirPath;

    @Value("${test.integration.git-server.repo-name}")
    private String gitServerRepoName;

    @Value("${test.integration.git-server.branch}")
    private String gitServerBranchName;

    @Value("${api.url-base}")
    private String apiUrlBase;

//    @Value("${test.integration.wiremock.home-mappings}")
//    private String wiremockHostMappingsPath;
//
//    @Value("${test.integration.wiremock.container-mappings}")
//    private String wiremockContainerMappingsPath;

    private static final String WEBHOOK_HANDLER_PIPELINE = "http://localhost:8080/webhook/handler/pipeline";
    private static final String WEBHOOK_PUSH_JSON = "integration_webhook_pull_request.json";

    private static final Network sharedNetwork = Network.newNetwork();

    @Container
    private GenericContainer<?> gitServerContainer;
//    @Container
//    private final GenericContainer<?> wiremockServer = createWiremockServer();

    @BeforeAll
    void setUp() {
        final var containerWithGit = createContainerWithGit();
        containerWithGit.start();

//        final String publicKeyPath = this.sshDirPath + "/" + this.sshPublicKey;
        final String publicKeyPath = getClass().getClassLoader().getResource("keys/id_client.pub").toString();

        this.gitServerContainer = createGitServer(this.gitServerDirPath, publicKeyPath);
        this.gitServerContainer.start();

        final String address = this.gitServerContainer.getHost();
        final int port = this.gitServerContainer.getMappedPort(22);

        log.info("Git server address: {} port: {}", address, port);
    }

    @Test
    void test() throws IOException {
        log.info("API URL base: {}", this.apiUrlBase);
        final GenericContainer<?> wiremock = WireMockInitializer.getWireMockContainer();
        log.info("Wiremock started: {}:{}", wiremock.getContainerIpAddress(), wiremock.getMappedPort(9591));

        // Arrange
        try (final InputStream is = getClass().getClassLoader().getResourceAsStream(WEBHOOK_PUSH_JSON)) {
            if (!nonNull(is))
                throw new FileNotFoundException(WEBHOOK_PUSH_JSON);

            final var objectMapper = new ObjectMapper();
            final String payload = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            final JsonNode node = objectMapper.readTree(payload);

            final String host = this.gitServerContainer.getHost();
            final Integer port = this.gitServerContainer.getMappedPort(22);

            final String cloneUrl = "ssh://git@" + host + ":" + port + "/srv/git/" + this.gitServerRepoName;

            ((ObjectNode) node.get("repository")).put("clone_url", cloneUrl);
            ((ObjectNode) node.at("/pull_request/head")).put("ref", this.gitServerBranchName);
            ((ObjectNode) node.at("/repository")).put("full_name", "kibonga-upstream-repo.git");
            ((ObjectNode) node.at("/repository")).put("name", "kibonga-upstream-repo");

            final String updatedPayload = objectMapper.writeValueAsString(node);

            final HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .header("X-Github-Event", "pull_request")
                .uri(URI.create(WEBHOOK_HANDLER_PIPELINE))
                .POST(HttpRequest.BodyPublishers.ofString(updatedPayload))
                .build();

            try (final HttpClient client = HttpClient.newHttpClient()) {
                // Act
                final var response = client.send(request, HttpResponse.BodyHandlers.ofString());

                // Assert
                assertEquals(200, response.statusCode());
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    private GitServerContainer createGitServer(final String gitServerDirPath, final String publicKeyPath) {

        try (final InputStream is = getClass().getClassLoader().getResourceAsStream("keys/id_client.pub")) {
            final String publicKey = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            final Path tmpFile = Files.createTempFile("authorized_keys", ".tmp");
            Files.writeString(tmpFile, publicKey);

            return new GitServerContainer(DockerImageName.parse("rockstorm/git-server:2.38"))
                .withNetwork(sharedNetwork)
                .withNetworkAliases("gitserver")
                .withCreateContainerCmdModifier(cmd -> cmd.withName("gitserver"))
                .withExposedPorts(22)
                .withFileSystemBind(
                    gitServerDirPath,
                    CONTAINER_REPOS_PATH,
                    BindMode.READ_WRITE
                )
                .withFileSystemBind(
                    tmpFile.toAbsolutePath().toString(),
                    "/home/git/.ssh/authorized_keys",
                    BindMode.READ_WRITE
                );
//            .withFileSystemBind(
//                publicKeyPath,
//                CONTAINER_AUTHORIZED_KEYS_PATH,
//                BindMode.READ_WRITE
//            );
//            .withSshKeyAuth();
        } catch (Exception e) {

        }
        return null;
    }

//    private static GenericContainer<?> createGitServer(String repoDirPath) {
//        return new GenericContainer<>(DockerImageName.parse("debian:bullseye-slim"))
//            .withCreateContainerCmdModifier(cmd -> cmd.withName("gitserver"))
//            .withNetworkAliases("gitserver")
//            .withNetwork(sharedNetwork)
//            .withExposedPorts(9418)
//            .withFileSystemBind(
//                repoDirPath,
//                CONTAINER_BASE_DIR_PATH,
//                BindMode.READ_WRITE
//            )
//            .withLogConsumer(new Consumer<OutputFrame>() {
//                @Override
//                public void accept(OutputFrame outputFrame) {
//                    log.info(outputFrame.getUtf8String());
//                }
//            })
//            .withCommand("sh", "-c",
//                "apt update && apt install -y git && " +
//                    "mkdir -p " + CONTAINER_BASE_DIR_PATH + " && " +
//                    "git daemon --verbose --export-all --enable=receive-pack " +
//                    "--base-path=" + CONTAINER_BASE_DIR_PATH + " --reuseaddr --listen=" + ALL_HOSTS + " --port=" + GIT_SERVER_DEFAULT_PORT
//            );
//    }

    private GenericContainer<?> createContainerWithGit() {
        return new GenericContainer<>(DockerImageName.parse("alpine:latest"))
            .withCreateContainerCmdModifier(cmd -> cmd.withName("testgit"))
            .withNetwork(sharedNetwork)
            .withNetworkAliases("testgit")
            .withCommand("tail", "-f", "/dev/null")
            .withExposedPorts();
    }

//    private GenericContainer<?> createWiremockServer() {
//        return new GenericContainer<>(DockerImageName.parse(WIREMOCK_IMAGE))
//           .withExposedPorts(9591)
//           .withFileSystemBind(
//               this.wiremockHostMappingsPath,
//               this.wiremockContainerMappingsPath,
//               BindMode.READ_WRITE
//           );
//    }
}
