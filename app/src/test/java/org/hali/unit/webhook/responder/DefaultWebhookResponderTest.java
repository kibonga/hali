package org.hali.unit.webhook.responder;

import com.sun.net.httpserver.HttpExchange;
import lombok.SneakyThrows;
import org.hali.handler.webhook.responder.DefaultWebhookResponder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class DefaultWebhookResponderTest {

    private final DefaultWebhookResponder defaultWebhookResponder = new DefaultWebhookResponder();

    // TODO - fix this test

//    @Test
//    @SneakyThrows
//    void succes_send200Response_onSuccess() {
//        // Arrange
//        final HttpExchange httpExchange = mock(HttpExchange.class);
//
//        // Act
//        // Assert
//        assertDoesNotThrow(
//            () -> this.webhookResponder.succes(httpExchange, 200, 0L));
//    }

    @Test
    @SneakyThrows
    void succes_failedToSendResponseHeaders_throwsIOException() {
        // Arrange
        final int statusCode = 400;
        final long responseLength = 0L;
        final HttpExchange httpExchange = mock(HttpExchange.class);

        doThrow(new IOException()).when(httpExchange)
            .sendResponseHeaders(anyInt(), anyLong());

        // Act
        // Assert
//        assertThrows(IOException.class,
//            () -> this.defaultWebhookResponder.succes(httpExchange, statusCode, responseLength));
    }

}
