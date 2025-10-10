package org.hali.git;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.ssh.jsch.JschConfigSessionFactory;
import org.eclipse.jgit.transport.ssh.jsch.OpenSshConfig;
import org.eclipse.jgit.util.FS;
import org.hali.HaliApplication;
import org.hali.exception.CloneRepositoryException;
import org.hali.resource.ResourceLoaderUtil;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.URISyntaxException;
import java.util.function.Function;

@Service
@Slf4j
public class JGitRepositoryCloner implements GitRepositoryCloner {

    @Override
    public void clone(String url, File workingDir) throws CloneRepositoryException {
        final SshSessionFactory sshSessionFactory = createSshSessionFactory();

        final var transportConfigCallback = transportConfigCallbackFunction.apply(sshSessionFactory);

        try (final Git result = Git.cloneRepository()
            .setURI(url)
            .setDirectory(workingDir)
            .setTransportConfigCallback(transportConfigCallback)
            .call()
        ) {
            log.info("Repository cloned successfully: {}", result.getRepository().toString());
        } catch (GitAPIException e) {
            log.error("Error cloning repository", e);
        }
    }

    private SshSessionFactory createSshSessionFactory() {
        return new JschConfigSessionFactory() {
            @Override
            protected void configure(OpenSshConfig.Host hc, Session session) {
                session.setConfig("StrictHostKeyChecking", "no");
            }

            @Override
            protected JSch createDefaultJSch(FS fs) throws JSchException {
                final JSch defaultJSch = super.createDefaultJSch(fs);
                defaultJSch.removeAllIdentity();

                final String privateKey;
                try {
                    privateKey = new File(ResourceLoaderUtil.getResource(HaliApplication.class, "keys/id_client").toURI()).getAbsolutePath();
                } catch (URISyntaxException e) {
                    log.error("Error getting id_client", e);
                    throw new RuntimeException(e);
                }

                defaultJSch.addIdentity(privateKey);

                return defaultJSch;
            }
        };
    }

    private static final Function<SshSessionFactory, TransportConfigCallback> transportConfigCallbackFunction = sshSessionFactory -> transportConfig -> {
        if (transportConfig instanceof SshTransport sshTransport) {
            sshTransport.setSshSessionFactory(sshSessionFactory);
        }
    };
}
