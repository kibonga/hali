package org.hali;

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

import java.io.IOException;
import java.nio.file.Files;

@Slf4j
public class JGitCloneTesting {


    private String privateKey;

    public static void main(String[] args) throws GitAPIException {

        final SshSessionFactory sessionFactory = new JschConfigSessionFactory() {
            @Override
            protected void configure(OpenSshConfig.Host hc, Session session) {
                session.setConfig("StrictHostKeyChecking", "no");
            }

            @Override
            protected JSch createDefaultJSch(FS fs) throws JSchException {
                final JSch defaultJSch = super.createDefaultJSch(fs);
                defaultJSch.removeAllIdentity();

                final String privateKeyPath = getClass().getClassLoader().getResource("keys/id_client").toString();
                defaultJSch.addIdentity(privateKeyPath);

                return defaultJSch;
            }
        };

        final TransportConfigCallback transportCb = transport -> {
            if (transport instanceof SshTransport sshTransport) {
                sshTransport.setSshSessionFactory(sessionFactory);
            }
        };

        int port = 22;
        final String cloneUrl = "ssh://git@localhost:" + port  + "/srv/git/" + "kibonga-upstream-repo.git";

        try (Git result = Git.cloneRepository()
            .setURI(cloneUrl)
            .setDirectory(Files.createTempDirectory("git-clone-test").toFile())
            .setTransportConfigCallback(transportCb)
            .call()
        ) {
            result.getRepository();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
