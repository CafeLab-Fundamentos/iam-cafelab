package com.acme.iamcafelab.iam.application.internal.commandservices;

import com.acme.iamcafelab.iam.application.internal.outboundservices.hashing.HashingService;
import com.acme.iamcafelab.iam.application.internal.outboundservices.tokens.TokenService;
import com.acme.iamcafelab.iam.domain.model.aggregates.User;
import com.acme.iamcafelab.iam.domain.model.commands.SignInCommand;
import com.acme.iamcafelab.iam.domain.model.commands.SignUpCommand;
import com.acme.iamcafelab.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserCommandServiceImplTest {

    private UserRepository userRepository;
    private HashingService hashingService;
    private TokenService tokenService;
    private UserCommandServiceImpl service;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        hashingService = mock(HashingService.class);
        tokenService = mock(TokenService.class);
        service = new UserCommandServiceImpl(userRepository, hashingService, tokenService);
    }

    @Test
    void signUpCreatesUserWithHashedPasswordTest() {
        var command = new SignUpCommand("ADRIAN@TEST.COM", "123456", "barista");

        when(userRepository.existsByEmail("adrian@test.com")).thenReturn(false);
        when(hashingService.encode("123456")).thenReturn("$2a$10$hashedPassword");

        var savedUser = new User("adrian@test.com", "$2a$10$hashedPassword", "barista");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        var result = service.handle(command);

        assertTrue(result.isPresent());
        assertEquals("adrian@test.com", result.get().getEmail());
        assertEquals("$2a$10$hashedPassword", result.get().getPassword());
        assertEquals("barista", result.get().getRole());

        var captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        assertEquals("adrian@test.com", captor.getValue().getEmail());
        assertEquals("$2a$10$hashedPassword", captor.getValue().getPassword());
    }

    @Test
    void signUpThrowsExceptionWhenEmailAlreadyExistsTest() {
        var command = new SignUpCommand("adrian@test.com", "123456", "barista");

        when(userRepository.existsByEmail("adrian@test.com")).thenReturn(true);

        var exception = assertThrows(RuntimeException.class, () -> service.handle(command));

        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void signInReturnsUserAndTokenWhenCredentialsAreValidTest() {
        var command = new SignInCommand("ADRIAN@TEST.COM", "123456");
        var user = new User("adrian@test.com", "$2a$10$hashedPassword", "barista");

        when(userRepository.findByEmail("adrian@test.com")).thenReturn(Optional.of(user));
        when(hashingService.matches("123456", "$2a$10$hashedPassword")).thenReturn(true);
        when(tokenService.generateToken("adrian@test.com")).thenReturn("jwt-token");

        var result = service.handle(command);

        assertTrue(result.isPresent());
        assertEquals(user, result.get().getLeft());
        assertEquals("jwt-token", result.get().getRight());
    }

    @Test
    void signInThrowsExceptionWhenUserDoesNotExistTest() {
        var command = new SignInCommand("missing@test.com", "123456");

        when(userRepository.findByEmail("missing@test.com")).thenReturn(Optional.empty());
        when(userRepository.findByEmailIgnoreCase("missing@test.com")).thenReturn(Optional.empty());

        var exception = assertThrows(RuntimeException.class, () -> service.handle(command));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void signInThrowsExceptionWhenPasswordIsInvalidTest() {
        var command = new SignInCommand("adrian@test.com", "wrong-password");
        var user = new User("adrian@test.com", "$2a$10$hashedPassword", "barista");

        when(userRepository.findByEmail("adrian@test.com")).thenReturn(Optional.of(user));
        when(hashingService.matches("wrong-password", "$2a$10$hashedPassword")).thenReturn(false);

        var exception = assertThrows(RuntimeException.class, () -> service.handle(command));

        assertEquals("Invalid password", exception.getMessage());
        verify(tokenService, never()).generateToken(anyString());
    }
}