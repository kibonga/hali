package org.hali.http.extractor;

import com.sun.net.httpserver.Headers;

public interface HeaderExtractor {

    String extract(Headers headers, String name);
}
