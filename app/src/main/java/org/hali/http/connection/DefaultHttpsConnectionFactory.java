package org.hali.http.connection;

import org.springframework.stereotype.Component;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Component
public class DefaultHttpsConnectionFactory implements ConnectionFactory {

    @Override
    public HttpsURLConnection createConnection(String url) throws IOException, URISyntaxException {
        return (HttpsURLConnection) new URI(url).toURL().openConnection();
    }
}
