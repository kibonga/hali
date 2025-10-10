package org.hali.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
@UtilityClass
public class ResourceLoader {

    public static String readResourcesAsString(Class<?> root, String resourcePath) {
        final String fullResourcePath = root.getPackageName().replace('.', '/') + "/" + resourcePath;
        try (final InputStream stream = root.getClassLoader().getResourceAsStream(fullResourcePath)) {
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Error while loading resource from path: " + fullResourcePath, e);
            throw new RuntimeException(e);
        }
    }
}
