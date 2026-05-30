package com.acme.iamcafelab.functional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.acme.iamcafelab.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import com.acme.iamcafelab.profiles.infrastructure.persistence.jpa.repositories.ProfileRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class IamProfilesFunctionalTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void createProfileAndSignInEndToEndTest() throws Exception {
        String email = "functional-" + UUID.randomUUID() + "@test.com";

        mockMvc.perform(post("/api/v1/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Adrian Functional",
                                  "email": "%s",
                                  "password": "123456",
                                  "role": "barista",
                                  "cafeteriaName": "CafeLab",
                                  "experience": "2 years",
                                  "profilePicture": "profile.png",
                                  "paymentMethod": "Visa",
                                  "isFirstLogin": true,
                                  "plan": "basic",
                                  "hasPlan": true
                                }
                                """.formatted(email)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Adrian Functional"))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.role").value("barista"))
                .andExpect(jsonPath("$.cafeteriaName").value("CafeLab"));

        assertTrue(profileRepository.findByNormalizedEmail(email).isPresent());
        assertTrue(userRepository.findByEmailIgnoreCase(email).isPresent());
        long profileId = profileRepository.findByNormalizedEmail(email).orElseThrow().getId();

        var signInResult = mockMvc.perform(post("/api/v1/authentication/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "%s",
                                  "password": "123456"
                                }
                                """.formatted(email)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.role").value("barista"))
                .andExpect(jsonPath("$.token").exists())
                .andReturn();

        String token = objectMapper.readTree(signInResult.getResponse().getContentAsString()).get("token").asText();
        var claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(
                        "test-only-secret-0123456789abcdef0123456789abcdef".getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertEquals(email, claims.getSubject());
        assertNotNull(claims.get("userId"));
        assertEquals(profileId, claims.get("userId", Number.class).longValue());
    }

    @Test
    void createProfileAndFindItByEmailEndToEndTest() throws Exception {
        String email = "find-profile-" + UUID.randomUUID() + "@test.com";

        mockMvc.perform(post("/api/v1/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Profile Finder",
                                  "email": "%s",
                                  "password": "123456",
                                  "role": "owner",
                                  "cafeteriaName": "Finder Coffee",
                                  "experience": "4 years",
                                  "profilePicture": "finder.png",
                                  "paymentMethod": "Mastercard",
                                  "isFirstLogin": true,
                                  "plan": "premium",
                                  "hasPlan": true
                                }
                                """.formatted(email)))
                .andExpect(status().isCreated());

        String token = signInAndExtractToken(email, "123456");

        mockMvc.perform(get("/api/v1/profiles")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken(token))
                        .param("email", email.toUpperCase()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Profile Finder"))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.role").value("owner"))
                .andExpect(jsonPath("$.cafeteriaName").value("Finder Coffee"));
    }

    @Test
    void updateProfileEmailAndSignInWithNewEmailEndToEndTest() throws Exception {
        String oldEmail = "old-email-" + UUID.randomUUID() + "@test.com";
        String newEmail = "new-email-" + UUID.randomUUID() + "@test.com";

        mockMvc.perform(post("/api/v1/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Email Update User",
                                  "email": "%s",
                                  "password": "123456",
                                  "role": "barista",
                                  "cafeteriaName": "CafeLab",
                                  "experience": "1 year",
                                  "profilePicture": "old.png",
                                  "paymentMethod": "Visa",
                                  "isFirstLogin": true,
                                  "plan": "basic",
                                  "hasPlan": true
                                }
                                """.formatted(oldEmail)))
                .andExpect(status().isCreated());

        var profile = profileRepository.findByNormalizedEmail(oldEmail).orElseThrow();
        Long profileId = profile.getId();
        String token = signInAndExtractToken(oldEmail, "123456");

        mockMvc.perform(patch("/api/v1/profiles/{userId}", profileId)
                        .header(HttpHeaders.AUTHORIZATION, bearerToken(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Email Updated User",
                                  "email": "%s",
                                  "cafeteriaName": "Updated Cafe",
                                  "experience": "2 years",
                                  "paymentMethod": "Mastercard",
                                  "isFirstLogin": false,
                                  "plan": "premium",
                                  "hasPlan": true
                                }
                                """.formatted(newEmail)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Email Updated User"))
                .andExpect(jsonPath("$.email").value(newEmail))
                .andExpect(jsonPath("$.cafeteriaName").value("Updated Cafe"));

        assertTrue(profileRepository.findByNormalizedEmail(newEmail).isPresent());
        assertTrue(userRepository.findByEmailIgnoreCase(newEmail).isPresent());

        mockMvc.perform(post("/api/v1/authentication/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "%s",
                                  "password": "123456"
                                }
                                """.formatted(newEmail)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(newEmail))
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void returnNotFoundForMissingProfileEndToEndTest() throws Exception {
        String email = "missing-profile-" + UUID.randomUUID() + "@test.com";

        mockMvc.perform(post("/api/v1/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Missing Profile User",
                                  "email": "%s",
                                  "password": "123456",
                                  "role": "barista",
                                  "cafeteriaName": "CafeLab",
                                  "experience": "1 year",
                                  "profilePicture": "missing.png",
                                  "paymentMethod": "Visa",
                                  "isFirstLogin": true,
                                  "plan": "basic",
                                  "hasPlan": true
                                }
                                """.formatted(email)))
                .andExpect(status().isCreated());

        String token = signInAndExtractToken(email, "123456");

        mockMvc.perform(get("/api/v1/profiles/{userId}", 999999L)
                        .header(HttpHeaders.AUTHORIZATION, bearerToken(token)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Perfil no encontrado"));
    }

    private String signInAndExtractToken(String email, String password) throws Exception {
        String responseBody = mockMvc.perform(post("/api/v1/authentication/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "%s",
                                  "password": "%s"
                                }
                                """.formatted(email, password)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(responseBody).get("token").asText();
    }

    private String bearerToken(String token) {
        return "Bearer " + token;
    }
}
