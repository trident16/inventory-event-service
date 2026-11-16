package com.tarunnalla.inventory.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * @param secret     base64-encoded HMAC key, at least 256 bits
 * @param expiration how long an issued token stays valid
 */
@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(String secret, Duration expiration) {
}
