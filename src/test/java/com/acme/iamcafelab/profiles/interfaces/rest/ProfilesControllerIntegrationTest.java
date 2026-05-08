package com.acme.iamcafelab.profiles.interfaces.rest;

import com.acme.iamcafelab.profiles.domain.model.aggregates.Profile;
import com.acme.iamcafelab.profiles.domain.model.commands.CreateProfileCommand;
import com.acme.iamcafelab.profiles.domain.model.commands.UpdateProfileCommand;
import com.acme.iamcafelab.profiles.domain.model.queries.GetAllProfilesQuery;
import com.acme.iamcafelab.profiles.domain.model.queries.GetProfileByEmailQuery;
import com.acme.iamcafelab.profiles.domain.model.queries.GetProfileByIdQuery;
import com.acme.iamcafelab.profiles.domain.services.ProfileCommandService;
import com.acme.iamcafelab.profiles.domain.services.ProfileQueryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class ProfilesControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProfileCommandService profileCommandService;

    @MockBean
    private ProfileQueryService profileQueryService;

    @Test
    void createProfileSuccessfullyTest() throws Exception {
        var profile = new Profile(
                "Adrian",
                "adrian@test.com",
                "barista",
                "CafeLab",
                "2 years",
                "profile.png",
                "Visa",
                true,
                "basic",
                true
        );

        when(profileCommandService.handle(any(CreateProfileCommand.class)))
                .thenReturn(Optional.of(profile));

        mockMvc.perform(post("/api/v1/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Adrian",
                                  "email": "adrian@test.com",
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
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Adrian"))
                .andExpect(jsonPath("$.email").value("adrian@test.com"))
                .andExpect(jsonPath("$.role").value("barista"))
                .andExpect(jsonPath("$.cafeteriaName").value("CafeLab"));
    }

    @Test
    void getProfileByIdSuccessfullyTest() throws Exception {
        var profile = new Profile(
                "Adrian",
                "adrian@test.com",
                "barista",
                "CafeLab",
                "2 years",
                "profile.png",
                "Visa",
                true,
                "basic",
                true
        );

        when(profileQueryService.handle(any(GetProfileByIdQuery.class)))
                .thenReturn(Optional.of(profile));

        mockMvc.perform(get("/api/v1/profiles/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Adrian"))
                .andExpect(jsonPath("$.email").value("adrian@test.com"));
    }

    @Test
    void getProfileByEmailSuccessfullyTest() throws Exception {
        var profile = new Profile(
                "Batman",
                "batman@test.com",
                "owner",
                "La Baticueva",
                "3 years",
                "batman.png",
                "Mastercard",
                true,
                "premium",
                true
        );

        when(profileQueryService.handle(any(GetProfileByEmailQuery.class)))
                .thenReturn(Optional.of(profile));

        mockMvc.perform(get("/api/v1/profiles")
                        .param("email", "batman@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Batman"))
                .andExpect(jsonPath("$.email").value("batman@test.com"))
                .andExpect(jsonPath("$.role").value("owner"));
    }

    @Test
    void getAllProfilesSuccessfullyTest() throws Exception {
        var profileOne = new Profile(
                "Adrian",
                "adrian@test.com",
                "barista",
                "CafeLab",
                "2 years",
                "profile.png",
                "Visa",
                true,
                "basic",
                true
        );

        var profileTwo = new Profile(
                "Batman",
                "batman@test.com",
                "owner",
                "La Baticueva",
                "3 years",
                "batman.png",
                "Mastercard",
                true,
                "premium",
                true
        );

        when(profileQueryService.handle(any(GetAllProfilesQuery.class)))
                .thenReturn(List.of(profileOne, profileTwo));

        mockMvc.perform(get("/api/v1/profiles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Adrian"))
                .andExpect(jsonPath("$[1].name").value("Batman"));
    }

    @Test
    void updateProfileSuccessfullyTest() throws Exception {
        var updatedProfile = new Profile(
                "Bruce Wayne",
                "bruce@test.com",
                "owner",
                "Wayne Coffee",
                "5 years",
                "bruce.png",
                "Mastercard",
                false,
                "premium",
                true
        );

        when(profileCommandService.handle(any(UpdateProfileCommand.class)))
                .thenReturn(Optional.of(updatedProfile));

        mockMvc.perform(patch("/api/v1/profiles/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Bruce Wayne",
                                  "email": "bruce@test.com",
                                  "cafeteriaName": "Wayne Coffee",
                                  "experience": "5 years",
                                  "paymentMethod": "Mastercard",
                                  "isFirstLogin": false,
                                  "plan": "premium",
                                  "hasPlan": true
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Bruce Wayne"))
                .andExpect(jsonPath("$.email").value("bruce@test.com"))
                .andExpect(jsonPath("$.cafeteriaName").value("Wayne Coffee"));
    }

    @Test
    void returnNotFoundWhenProfileDoesNotExistTest() throws Exception {
        when(profileQueryService.handle(any(GetProfileByIdQuery.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/profiles/{userId}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Perfil no encontrado"));
    }

    @Test
    void returnBadRequestWhenCreateProfileResourceIsInvalidTest() throws Exception {
        mockMvc.perform(post("/api/v1/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "name": "",
                              "email": "invalid@test.com",
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
                            """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("No se pudo leer el JSON: Name is required"));
    }
}