package org.hali;

public final class ContainerInfoConsts {
    private ContainerInfoConsts() {
    }

    public static final int WIREMOCK_PORT = 8080;
    public static final int WIREMOCK_PORT_HTTPS = 8443;
    public static final int GIT_SERVER_PORT = 22;

    public static final String WIREMOCK_IMAGE = "wiremock/wiremock:latest";
    public static final String OPENSEARCH_IMAGE = "opensearchproject/opensearch:2.19.3";
    public static final String GIT_SERVER_GIT_SRV = "/srv/git";

    //    public static final String CONTAINER_KEYS_PATH = "/home/git/.ssh/authorized_keys";

    public static final String CONTAINER_AUTHORIZED_KEYS_PATH = "/home/git/.ssh/authorized_keys";
    public static final String LOCALHOST = "localhost";
    public static final Integer GIT_SERVER_DEFAULT_PORT = 9418;

    public static final String WEBHOOK_HANDLER_PIPELINE = "http://localhost:8080/webhook/handler/pipeline";

    public static final String DATA_INTEGRATION_WEBHOOK_PULL_REQUEST_JSON = "data/integration_webhook_pull_request.json";
    public static final String DATA_INTEGRATION_WEBHOOK_PUSH_JSON = "data/integration_webhook_push.json";

    public static String getWebhookHandlerPipelineUrl(Integer port) {
        return String.format("http://%s:%d/webhook/handler/pipeline", LOCALHOST, port);
    }
}
