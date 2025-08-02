package org.hali.common.fs;

import java.nio.file.Path;

public interface DirectoryService {
    void create(Path path);

    void remove(Path path);

    boolean exists(Path path);
}
