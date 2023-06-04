package com.samseen.bookify.security.service;

import com.samseen.bookify.security.config.SecurityConfig;
import com.samseen.bookify.security.model.RefreshToken;
import com.samseen.bookify.user.entity.User;
import io.fusionauth.jwt.JWTException;
import io.fusionauth.jwt.Signer;
import io.fusionauth.jwt.Verifier;
import io.fusionauth.jwt.domain.JWT;
import io.fusionauth.jwt.rsa.RSASigner;
import io.fusionauth.jwt.rsa.RSAVerifier;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class TokenService {

    private final SecurityConfig securityConfig;
    private final RedissonClient redissonClient;
    private final Signer signer;
    private final Verifier verifier;

    public TokenService(SecurityConfig securityConfig, RedissonClient redissonClient) {
        this.securityConfig = securityConfig;
        this.redissonClient = redissonClient;
        this.signer = RSASigner.newSHA512Signer(securityConfig.getRsa());
        this.verifier = RSAVerifier.newVerifier(securityConfig.getRsa());
    }

    public String issueAccessToken(User user) {
        return generateUserToken(
                user, user.getMerchantId()
        );
    }

    private String generateUserToken(User user, Long merchantId) {
        String id = merchantId == null ? "" : merchantId.toString();
        ZoneId zoneId = TimeZone.getTimeZone("GMT+1").toZoneId();
        String issuer = "www.bookify.samseen.com";
        ZonedDateTime issueAt = ZonedDateTime.now(zoneId);

        JWT jwt = new JWT()
                .setIssuer(issuer)
                .setIssuedAt(issueAt)
                .setExpiration(issueAt.plus(securityConfig.getExpirationInMinute(), ChronoUnit.MINUTES))
                .setUniqueId(id)
                .addClaim("role", user.getRole())
                .setSubject(user.getEmail());

        return JWT.getEncoder().encode(jwt, signer);
    }

    public String getUserNameFromJwtToken(String token) {
        return JWT.getDecoder().decode(token, verifier).subject;
    }

    public boolean isValid(String token) {

        if (StringUtils.isBlank(token)) {
            return false;
        }

        try {
            JWT.getDecoder().decode(token, verifier);
            return true;
        } catch (JWTException e) {
            log.error("Invalid token {}", e.getMessage());
        }
        return false;
    }

    public RefreshToken issueRefreshToken(Long merchantId, String email) {
        Instant expireAt = Instant.now().plusSeconds(securityConfig.getRefreshExpirationInMinute() * 60);
        RefreshToken refreshToken = new RefreshToken(merchantId, email, UUID.randomUUID().toString(), expireAt);
        getRefreshTokenCache().put(refreshToken.getToken(), refreshToken, securityConfig.getRefreshExpirationInMinute(), TimeUnit.MINUTES);
        return refreshToken;
    }

    public Optional<RefreshToken> getRefreshToken(String token) {
        return Optional.ofNullable(getRefreshTokenCache().get(token));
    }

    public RMapCache<String, RefreshToken> getRefreshTokenCache() {
        String baseKey = "com.samseen.bookify.refresh-token-map";
        return redissonClient.getMapCache(baseKey);
    }
}
