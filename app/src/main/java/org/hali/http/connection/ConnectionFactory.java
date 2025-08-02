package org.hali.http.connection;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.URISyntaxException;

public interface ConnectionFactory {
    HttpsURLConnection createConnection(String url) throws IOException, URISyntaxException;
}
