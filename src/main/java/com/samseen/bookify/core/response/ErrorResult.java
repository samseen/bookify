package com.samseen.bookify.core.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ErrorResult {
    private String timestamp;
    private String status;
    private String error;
    private String message;
    private String path;
}
