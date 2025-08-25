package org.hali.common.command;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface CommandExecutor {
    int runCommand(List<String> command, File workingDir) throws IOException, InterruptedException;
    void cleanUp() throws IOException;
}
