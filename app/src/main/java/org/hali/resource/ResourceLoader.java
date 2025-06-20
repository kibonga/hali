package org.hali.resource;

import java.io.InputStream;

public interface ResourceLoader {
    InputStream getInputStream(String path);
}
