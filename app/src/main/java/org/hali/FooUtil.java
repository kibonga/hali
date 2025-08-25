package org.hali;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.experimental.UtilityClass;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.ssh.jsch.JschConfigSessionFactory;
import org.eclipse.jgit.transport.ssh.jsch.OpenSshConfig;
import org.eclipse.jgit.util.FS;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;

@UtilityClass
public class FooUtil {

    public static void foo(String cloneUrl) {

        final SshSessionFactory sessionFactory = new JschConfigSessionFactory() {
            @Override
            protected void configure(OpenSshConfig.Host hc, Session session) {
                session.setConfig("StrictHostKeyChecking", "no");
            }

            @Override
            protected JSch createDefaultJSch(FS fs) throws JSchException {
                final JSch defaultJSch = super.createDefaultJSch(fs);
                defaultJSch.removeAllIdentity();

                final String privateKeyPath;
                try {
                    privateKeyPath = new File(getClass().getClassLoader().getResource("keys/id_client").toURI()).getAbsolutePath();
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
                defaultJSch.addIdentity(privateKeyPath);

                return defaultJSch;
            }
        };

        final TransportConfigCallback transportCb = transport -> {
            if (transport instanceof SshTransport sshTransport) {
                sshTransport.setSshSessionFactory(sessionFactory);
            }
        };

        try (Git result = Git.cloneRepository()
            .setURI(cloneUrl)
            .setDirectory(Files.createTempDirectory("git-clone-").toFile())
            .setTransportConfigCallback(transportCb)
            .call()
        ) {
            result.getRepository();
        } catch (IOException | GitAPIException e) {
            throw new RuntimeException(e);
        }
    }
}
