package org.hali;

import com.github.dockerjava.api.model.HostConfig;
import com.github.sparsick.testcontainers.gitserver.plain.GitServerContainer;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.hali.util.ResourceLoader;
import org.opensearch.testcontainers.OpenSearchContainer;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@UtilityClass
@Slf4j
public class ContainerFactory {

    private static final String PUBLIC_KEY_PATH = "keys/id_client.pub";

    public GitServerContainer createGitServer(final String gitServerDirPath) throws IOException {

        final String publicKey = ResourceLoader.readResourcesAsString(ContainerFactory.class, PUBLIC_KEY_PATH);

        final Path tmpFile = Files.createTempFile("authorized_keys", ".tmp");
        Files.writeString(tmpFile, publicKey);

        return new GitServerContainer(DockerImageName.parse("rockstorm/git-server:2.38"))
            .withNetwork(Network.SHARED)
            .withNetworkAliases("gitserver")
            .withCreateContainerCmdModifier(cmd -> cmd.withName("gitserver"))
            .withExposedPorts(ContainerInfoConsts.GIT_SERVER_PORT)
            .withFileSystemBind(
                gitServerDirPath,
                ContainerInfoConsts.GIT_SERVER_GIT_SRV,
                BindMode.READ_WRITE
            )
            .withFileSystemBind(
                tmpFile.toAbsolutePath().toString(),
                "/home/git/.ssh/authorized_keys",
                BindMode.READ_WRITE
            )
            .withLogConsumer(new Slf4jLogConsumer(log).withPrefix("git-server"));
    }

    public GenericContainer<?> createWiremockServer(String hostMappingsPath, String containerMappingsPath) {
        return new GenericContainer<>(DockerImageName.parse(ContainerInfoConsts.WIREMOCK_IMAGE))
            .withNetwork(Network.SHARED)
            .withNetworkAliases("wiremock")
            .withCreateContainerCmdModifier(cmd -> cmd.withName("wiremock"))
            .withExposedPorts(ContainerInfoConsts.WIREMOCK_PORT)
            .withFileSystemBind(
                hostMappingsPath,
                containerMappingsPath,
                BindMode.READ_WRITE
            )
            .withLogConsumer(new Slf4jLogConsumer(log).withPrefix("wiremock"));
    }

    public OpenSearchContainer<?> createOpenSearchContainer() {
        return new OpenSearchContainer<>(DockerImageName.parse(ContainerInfoConsts.OPENSEARCH_IMAGE))
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
