package com.samseen.bookify.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class RestResponseStatusException extends ResponseStatusException {
    public RestResponseStatusException(HttpStatus status) {
        super(status);
    }

    public RestResponseStatusException(HttpStatus status, String reason) {
        super(status, reason);
    }

    public RestResponseStatusException(HttpStatus status, String reason, Throwable cause) {
        super(status, reason, cause);
    }

    public RestResponseStatusException(int rawStatusCode, String reason, Throwable cause) {
        super(rawStatusCode, reason, cause);
    }
}
