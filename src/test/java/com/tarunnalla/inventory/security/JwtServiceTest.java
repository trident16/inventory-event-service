package com.tarunnalla.inventory.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private static final String SECRET = "5rXDMbL2pUSlvDYoNyHCTiOX3sEMu4RmicNT/LnI+o3QfVDGNVWPnv8a9NMZEF0L";
    private static final String OTHER_SECRET = "Zm9vYmFyYmF6cXV4Zm9vYmFyYmF6cXV4Zm9vYmFyYmF6cXV4Zm9vYmFyYmF6cXV4";

    private final JwtService jwtService = new JwtService(new JwtProperties(SECRET, Duration.ofHours(1)));

    @Test
    void tokenRoundTripKeepsUsernameAndRole() {
        String token = jwtService.generateToken("tarun", Role.ADMIN);

        JwtService.TokenClaims claims = jwtService.parse(token);

        assertThat(claims.username()).isEqualTo("tarun");
        assertThat(claims.role()).isEqualTo(Role.ADMIN);
    }

    @Test
    void tokenSignedWithDifferentKeyIsRejected() {
        JwtService otherService = new JwtService(new JwtProperties(OTHER_SECRET, Duration.ofHours(1)));
        String foreignToken = otherService.generateToken("tarun", Role.ADMIN);

        assertThatThrownBy(() -> jwtService.parse(foreignToken))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void expiredTokenIsRejected() {
        JwtService shortLived = new JwtService(new JwtProperties(SECRET, Duration.ofSeconds(-1)));
        String expired = shortLived.generateToken("tarun", Role.USER);

        assertThatThrownBy(() -> jwtService.parse(expired))
                .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    void tamperedTokenIsRejected() {
        String token = jwtService.generateToken("tarun", Role.USER);
        String tampered = token.substring(0, token.length() - 2) + (token.endsWith("A") ? "BB" : "AA");

        assertThatThrownBy(() -> jwtService.parse(tampered))
                .isInstanceOf(JwtException.class);
    }
}
