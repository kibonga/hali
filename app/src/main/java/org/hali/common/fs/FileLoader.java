package org.hali.common.fs;

import java.io.IOException;
import java.nio.file.Path;

public interface FileLoader {
    String load(Path path) throws IOException;
}
