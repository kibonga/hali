package org.hali.git;

import lombok.extern.slf4j.Slf4j;
import org.hali.common.command.CommandExecutor;
import org.hali.security.ssh.SecuritySshProperties;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

@Service
@Slf4j
public class SshGitCommandExecutor extends AbstractGitCommandExecutor {

    private static final Function<String, String> SSH_COMMAND_OPTIONS_FUNCTION =
        pk -> "core.sshCommand=ssh -i " + pk + " -o StrictHostKeyChecking=no";

    private final SecuritySshProperties securitySshProperties;

    public SshGitCommandExecutor(CommandExecutor commandExecutor, SecuritySshProperties securitySshProperties) {
        super(commandExecutor);
        this.securitySshProperties = securitySshProperties;
    }

    @Override
    protected BiFunction<String, String, List<String>> getCloneCommandFunction() {
        try {
            final String privateKeyPath = Objects.requireNonNull(getClass().getClassLoader().getResource("keys/id_client")).toURI().getPath();
            final String sshCommandOptions = SSH_COMMAND_OPTIONS_FUNCTION.apply(privateKeyPath);

//            final String str = "git -c core.sshCommand='ssh -i /home/... -o StrictHostKeyChecking=no' clone --depth=1 --single-branch --branch=master --verbose ssh://git@localhost:"+ port + "/srv/git/kibonga-upstream-repo.git\")";

            return (branch, url) -> List.of("bash", "-c", "git", "-c", sshCommandOptions, "clone", "--depth=1", "--single-branch",
                "--branch=" + branch, "--verbose", url);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
