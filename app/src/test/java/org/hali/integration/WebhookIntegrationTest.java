package org.hali.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.sparsick.testcontainers.gitserver.plain.GitServerContainer;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.HttpAppender;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensearch.testcontainers.OpenSearchContainer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

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
    private static final String OPENSEARCH_IMAGE = "opensearchproject/opensearch:2.19.3";

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
    @Container
    private OpenSearchContainer<?> openSearchContainer;
//    @Container
//    private final GenericContainer<?> wiremockServer = createWiremockServer();

    @BeforeAll
    void setUp() throws MalformedURLException {
        final String publicKeyPath = getClass().getClassLoader().getResource("keys/id_client.pub").toString();

        this.gitServerContainer = createGitServer(this.gitServerDirPath, publicKeyPath);
        this.gitServerContainer.start();

        this.openSearchContainer = createOpenSearchContainer();
        this.openSearchContainer.start();

        final String gitServerHost = this.gitServerContainer.getHost();
        final int gitServerPort = this.gitServerContainer.getMappedPort(22);

        final String openSearchHost = this.openSearchContainer.getHost();
        final int openSearchPort = this.openSearchContainer.getMappedPort(9200);

        final String openSearchUrl = "http://" + openSearchHost + ":" + openSearchPort + "/logs/_doc";
//        setupHttpAppender(openSearchUrl);
        foo(openSearchUrl);

        log.info("Git server address: {} port: {}", gitServerHost, gitServerPort);
        log.info("OpenSearch host: {} port: {}", openSearchHost, openSearchPort);

        int a = 1;
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

                Thread.sleep(Duration.ofSeconds(15));
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
                )
                .withLogConsumer(new Slf4jLogConsumer(log).withPrefix("git-server"));
        } catch (Exception e) {

        }
        return null;
    }

    private OpenSearchContainer<?> createOpenSearchContainer() {
        return new OpenSearchContainer<>(DockerImageName.parse(OPENSEARCH_IMAGE));
    }

    private static void setupHttpAppender(String url) throws MalformedURLException {
        final LoggerContext loggerContext = (LoggerContext) LogManager.getContext(false);
        final Configuration configuration = loggerContext.getConfiguration();

        final HttpAppender httpAppender = HttpAppender.newBuilder()
            .setName("OpenSearchHttpAppender")
            .setUrl(URI.create(url).toURL())
            .setLayout(null)
            .setMethod("POST")
            .build();


        httpAppender.start();
        configuration.addAppender(httpAppender);

        final AppenderRef appenderRef = AppenderRef.createAppenderRef("OpenSearchHttpAppender", null, null);
        final AppenderRef[] appenderRefs = new AppenderRef[] { appenderRef };

        final LoggerConfig loggerConfig = configuration.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
        loggerConfig.addAppender(httpAppender, null, null);

        loggerContext.updateLoggers();
    }

    private static void foo(String url) {
        final ConfigurationBuilder<BuiltConfiguration> configurationBuilder = ConfigurationBuilderFactory.newConfigurationBuilder();

        final AppenderComponentBuilder httpAppender = configurationBuilder
            .newAppender("HTTP", "Http")
            .addAttribute("url", url)
            .add(configurationBuilder.newLayout("JsonLayout").addAttribute("compact", true));

        final AppenderComponentBuilder consoleAppender = configurationBuilder
            .newAppender("Hali Console", "Console")
            .add(configurationBuilder.newLayout("PatternLayout")
                .addAttribute("pattern", "%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} KIBONGA:- %msg%n"));

        final Path path = Path.of("");

        final AppenderComponentBuilder fileAppender = configurationBuilder
            .newAppender("FILE", "File")
            .addAttribute("fileName", "kibonga-appender.log")
            .add(configurationBuilder.newLayout("JsonLayout").addAttribute("compact", true));

        final Configuration configuration = configurationBuilder
            .add(httpAppender)
            .add(consoleAppender)
            .add(fileAppender)
            .add(
                configurationBuilder.newRootLogger(Level.INFO)
                    .add(configurationBuilder.newAppenderRef("HTTP"))
                    .add(configurationBuilder.newAppenderRef("Hali Console"))
                    .add(configurationBuilder.newAppenderRef("FILE"))
            )
            .build(false);

        final LoggerContext loggerContext = (LoggerContext) LogManager.getContext(false);
        loggerContext.start(configuration);
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
