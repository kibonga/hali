package org.hali.integration;

import com.github.dockerjava.api.model.HostConfig;
import com.github.sparsick.testcontainers.gitserver.plain.GitServerContainer;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.testcontainers.OpenSearchContainer;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hali.integration.ContainerInfoConsts.GIT_SERVER_GIT_SRV;
import static org.hali.integration.ContainerInfoConsts.GIT_SERVER_PORT;
import static org.hali.integration.ContainerInfoConsts.OPENSEARCH_IMAGE;
import static org.hali.integration.ContainerInfoConsts.WIREMOCK_IMAGE;
import static org.hali.integration.ContainerInfoConsts.WIREMOCK_PORT;

@UtilityClass
@Slf4j
public class ContainerFactory {

    public GitServerContainer createGitServer(final String gitServerDirPath, final String publicKeyPath) {

        try (final InputStream is = ContainerFactory.class.getClassLoader().getResourceAsStream("keys/id_client.pub")) {
            final String publicKey = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            final Path tmpFile = Files.createTempFile("authorized_keys", ".tmp");
            Files.writeString(tmpFile, publicKey);

            return new GitServerContainer(DockerImageName.parse("rockstorm/git-server:2.38"))
                .withNetwork(Network.SHARED)
                .withNetworkAliases("gitserver")
                .withCreateContainerCmdModifier(cmd -> cmd.withName("gitserver"))
                .withExposedPorts(GIT_SERVER_PORT)
                .withFileSystemBind(
                    gitServerDirPath,
                    GIT_SERVER_GIT_SRV,
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

    public GenericContainer<?> createWiremockServer(String hostMappingsPath, String containerMappingsPath) {
        return new GenericContainer<>(DockerImageName.parse(WIREMOCK_IMAGE))
            .withNetwork(Network.SHARED)
            .withNetworkAliases("wiremock")
            .withCreateContainerCmdModifier(cmd -> cmd.withName("wiremock"))
            .withExposedPorts(WIREMOCK_PORT)
            .withFileSystemBind(
                hostMappingsPath,
                containerMappingsPath,
                BindMode.READ_WRITE
            )
            .withLogConsumer(new Slf4jLogConsumer(log).withPrefix("wiremock"));
    }

    public OpenSearchContainer<?> createOpenSearchContainer() {
        return new OpenSearchContainer<>(DockerImageName.parse(OPENSEARCH_IMAGE))
            .withNetworkAliases("opensearch")
            .withCreateContainerCmdModifier(cmd -> cmd.withName("opensearch"));
    }

    public static GenericContainer<?> createPrometheusServer(Path prometheusScrapeConfigYamlPath, int prometheusPort) throws IOException {
        final String yaml = """
            global:
              scrape_interval: 5s
            scrape_configs:
              - job_name: 'integration-test'
                metrics_path: '/actuator/prometheus'
                static_configs:
                  - targets: ['localhost:%d']
            """.formatted(prometheusPort);

        Files.writeString(prometheusScrapeConfigYamlPath, yaml);

        return new GenericContainer<>(DockerImageName.parse("prom/prometheus:v3.6.0-rc.1"))
//            .withNetwork(sharedNetwork) // Depending on whether it's connected to shared network or host network (uncomment these options)
//            .withExposedPorts(9090)
            .withCopyFileToContainer(MountableFile.forHostPath(prometheusScrapeConfigYamlPath), "/etc/prometheus/prometheus.yml")
            .withNetworkAliases("prometheus")
            .withCreateContainerCmdModifier(cmd ->
            {
                cmd.withName("prometheus");
                cmd.withHostConfig(new HostConfig().withNetworkMode("host")); // Used when container is within host network (container can see localhost)
            });
    }
}
