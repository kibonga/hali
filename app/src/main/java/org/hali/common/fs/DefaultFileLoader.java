package org.hali.common.fs;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class DefaultFileLoader implements FileLoader {

    @Override
    public String load(Path path) throws IOException {
        return Files.readString(path);
    }
}
