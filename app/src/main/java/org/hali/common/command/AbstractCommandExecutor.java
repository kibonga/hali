package org.hali.common.command;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

@Slf4j
public abstract class AbstractCommandExecutor implements CommandExecutor {
    @SneakyThrows
    @Override
    public int runCommand(List<String> command, File workingDir) throws IOException, InterruptedException {
        final ProcessBuilder processBuilder = new ProcessBuilder(command)
            .directory(workingDir)
            .redirectErrorStream(true);

        final Process process = processBuilder.start();

        try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            bufferedReader.lines().forEach(line -> {
                try {
                    handleOutput(line);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        return process.waitFor();
    }

    protected abstract void handleOutput(String line) throws IOException;
}
