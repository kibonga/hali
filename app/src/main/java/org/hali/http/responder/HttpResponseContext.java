package org.hali.http.responder;

public record HttpResponseContext(String url, String method,
                                  String token, byte[] data) {
}
