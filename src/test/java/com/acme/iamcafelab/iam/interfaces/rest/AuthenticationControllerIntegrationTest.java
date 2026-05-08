package com.acme.iamcafelab.iam.interfaces.rest;

import com.acme.iamcafelab.iam.domain.model.aggregates.User;
import com.acme.iamcafelab.iam.domain.model.commands.SignInCommand;
import com.acme.iamcafelab.iam.domain.model.commands.SignUpCommand;
import com.acme.iamcafelab.iam.domain.services.UserCommandService;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class AuthenticationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserCommandService userCommandService;

    @Test
    void signInSuccessfullyTest() throws Exception {
        var user = new User(
                "adrian@test.com",
                "$2a$10$hashedPassword",
                "barista"
        );

        when(userCommandService.handle(any(SignInCommand.class)))
                .thenReturn(Optional.of(ImmutablePair.of(user, "jwt-token")));

        mockMvc.perform(post("/api/v1/authentication/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "adrian@test.com",
                                  "password": "123456"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("adrian@test.com"))
                .andExpect(jsonPath("$.role").value("barista"))
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void signUpSuccessfullyTest() throws Exception {
        var createdUser = new User(
                "owner@test.com",
                "$2a$10$hashedPassword",
                "owner"
        );

        when(userCommandService.handle(any(SignUpCommand.class)))
                .thenReturn(Optional.of(createdUser));

        when(userCommandService.handle(any(SignInCommand.class)))
                .thenReturn(Optional.of(ImmutablePair.of(createdUser, "jwt-token")));

        mockMvc.perform(post("/api/v1/authentication/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "owner@test.com",
                                  "password": "123456",
                                  "role": "owner"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("owner@test.com"))
                .andExpect(jsonPath("$.role").value("owner"))
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void returnNotFoundWhenSignInFailsTest() throws Exception {
        when(userCommandService.handle(any(SignInCommand.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/api/v1/authentication/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "missing@test.com",
                                  "password": "wrong"
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Credenciales inválidas"));
    }

    @Test
    void returnBadRequestWhenSignUpFailsTest() throws Exception {
        when(userCommandService.handle(any(SignUpCommand.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/api/v1/authentication/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "duplicated@test.com",
                                  "password": "123456",
                                  "role": "barista"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("No se pudo registrar el usuario"));
    }
}