package com.samseen.bookify.core.handler;

import com.samseen.bookify.core.exception.ClientErrorException;
import com.samseen.bookify.core.response.ErrorResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.ZonedDateTime;
import java.util.TimeZone;

@RestControllerAdvice
@Slf4j
public class WebExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleException(BadCredentialsException e, WebRequest request) {
        return handle(HttpStatus.UNAUTHORIZED, e, request);
    }

    public ResponseEntity<?> handleException(ClientErrorException e, WebRequest request) {
        return handle(HttpStatus.SERVICE_UNAVAILABLE, e, request);
    }

    public ResponseEntity<?> handle(HttpStatus httpStatus, Exception e, WebRequest request) {
        ZonedDateTime timestamp = ZonedDateTime.now(TimeZone.getTimeZone("GMT+1").toZoneId());
        ErrorResult build = ErrorResult.builder()
                .timestamp(timestamp.toString())
                .status(String.valueOf(httpStatus.value()))
                .message("")
                .error(e.getMessage())
                .path(getPath(request))
                .build();
        return ResponseEntity.status(httpStatus).body(build);
    }

    private String getPath(WebRequest request) {
        return request.getDescription(false).substring("uri=".length());
    }
}
