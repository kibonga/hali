package org.hali.common.command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.List;

@Slf4j
@Service
public class CommandRunner implements CommandExecutor {
    @Override
    public int runCommand(List<String> command, File workingDir) throws IOException, InterruptedException {
        final ProcessBuilder processBuilder = new ProcessBuilder(command)
            .directory(workingDir)
            .redirectErrorStream(true);

        final Process process = processBuilder.start();

        // Constantly trying to empty the buffer in order not to reach deadlock due to input stream data overflowing the buffer
        try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            bufferedReader.lines().forEach(log::error);
        }

        return process.waitFor();
    }
}
