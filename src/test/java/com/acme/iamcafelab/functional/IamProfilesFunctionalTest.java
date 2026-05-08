package com.acme.iamcafelab.functional;

import com.acme.iamcafelab.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import com.acme.iamcafelab.profiles.infrastructure.persistence.jpa.repositories.ProfileRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

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

        mockMvc.perform(post("/api/v1/authentication/sign-in")
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
                .andExpect(jsonPath("$.token").exists());
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

        mockMvc.perform(get("/api/v1/profiles")
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

        mockMvc.perform(patch("/api/v1/profiles/{userId}", profileId)
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
        mockMvc.perform(get("/api/v1/profiles/{userId}", 999999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Perfil no encontrado"));
    }
}