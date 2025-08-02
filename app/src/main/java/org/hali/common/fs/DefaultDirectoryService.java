package org.hali.common.fs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

@Component
@Slf4j
public class DefaultDirectoryService implements DirectoryService {
    @Override
    public void create(Path path) {
        log.info("Creating directory on path: [{}]", path);

        if (exists(path)) {
            log.info("Directory: [{}] already exists, skipping...", path);

            return;
        }

        if (new File(path.toString()).mkdirs()) {
            log.info("Successfully created directory on path: [{}]", path);
        } else {
            log.error(
                "Failed to create directory on path: {}.",
                path
            );
        }
    }

    @Override
    public void remove(Path path) {
        log.info("Trying to remove directory from path: [{}]", path);

        if (!exists(path)) {
            log.info(
                "Directory does not exist at path: [{}]. Skipping removal.",
                path);

            return;
        }

        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file,
                                                 BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir,
                                                          IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });

            log.info("Successfully removed directory from path: [{}]", path);
        } catch (IOException e) {
            log.error(
                "Failed to remove directory from path: {}. Reason: {}",
                path,
                e.getMessage()
            );
        }
    }

    @Override
    public boolean exists(Path path) {
        return Files.exists(path);
    }
}
