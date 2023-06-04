package com.samseen.bookify.security.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RefreshToken implements Serializable {
    private Long merchantId;
    private String email;
    private String token;
    private Instant expireAt;
}
