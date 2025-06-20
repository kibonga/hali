package org.hali.http.server;

public final class HttpServerConsts {

    private HttpServerConsts() {
    }

    public static String getDefaultHost() {
        return System.getenv().getOrDefault("HOST", "0.0.0.0");
    }

    public static int getDefaultPort() {
        return Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
    }

}
