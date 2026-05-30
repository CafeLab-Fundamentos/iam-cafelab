package com.acme.iamcafelab.iam.infrastructure.tokens.jwt.services;

import com.acme.iamcafelab.profiles.domain.model.aggregates.Profile;
import com.acme.iamcafelab.profiles.infrastructure.persistence.jpa.repositories.ProfileRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenServiceImplTest {

    private static final String SECRET =
            "local-dev-only-change-me-0123456789abcdef0123456789abcdef0123456789abcdef";

    @Mock
    private ProfileRepository profileRepository;

    private TokenServiceImpl tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new TokenServiceImpl(profileRepository);
        ReflectionTestUtils.setField(tokenService, "secret", SECRET);
        ReflectionTestUtils.setField(tokenService, "expirationDays", 7);
    }

    @Test
    void includesUserIdClaimWhenProfileExistsForEmailTest() {
        var profile = new Profile(
                "User", "user@test.com", "barista", "Cafe", "1y", "pic.png", "Visa", true, "basic", true);
        ReflectionTestUtils.setField(profile, "id", 15L);
        when(profileRepository.findByNormalizedEmail("user@test.com")).thenReturn(Optional.of(profile));

        String token = tokenService.generateToken("user@test.com");

        var claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertEquals("user@test.com", claims.getSubject());
        assertEquals(15L, claims.get("userId", Number.class).longValue());
    }

    @Test
    void omitsUserIdClaimWhenProfileDoesNotExistTest() {
        when(profileRepository.findByNormalizedEmail("user@test.com")).thenReturn(Optional.empty());

        String token = tokenService.generateToken("user@test.com");

        var claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertEquals("user@test.com", claims.getSubject());
        assertNull(claims.get("userId"));
    }
}
