package org.hali.resource;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Slf4j
@UtilityClass
public class ResourceLoaderUtil {

    public static String readResourcesAsString(Class<?> root, String name) {
        final String fullResourcePath = getFullResourcePath(root, name);
        try (final InputStream stream = root.getClassLoader().getResourceAsStream(fullResourcePath)) {
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Error while loading resource from path: " + fullResourcePath, e);
            throw new RuntimeException(e);
        }
    }

    public static URL getResource(Class<?> root, String name) {
        final String fullResourcePath = getFullResourcePath(root, name);
        return root.getClassLoader().getResource(fullResourcePath);
    }

    private static String getFullResourcePath(Class<?> root, String name) {
        return root.getPackageName().replace('.', '/') + "/" + name;
    }
}
