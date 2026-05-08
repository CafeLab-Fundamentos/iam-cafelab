package com.acme.iamcafelab.iam.domain.model.aggregates;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void createUserWithEmailPasswordAndRoleTest() {
        var user = new User(
                "ADRIAN@TEST.COM",
                "$2a$10$samplePasswordHash",
                "barista"
        );

        assertEquals("adrian@test.com", user.getEmail());
        assertEquals("$2a$10$samplePasswordHash", user.getPassword());
        assertEquals("barista", user.getRole());
    }

    @Test
    void normalizeEmailWhenCreatingUserTest() {
        var user = new User(
                "  OWNER@CAFELAB.COM  ",
                "$2a$10$samplePasswordHash",
                "owner"
        );

        assertEquals("owner@cafelab.com", user.getEmail());
    }

    @Test
    void throwExceptionWhenEmailIsBlankTest() {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                new User("   ", "$2a$10$samplePasswordHash", "barista")
        );

        assertEquals("Email cannot be null or blank", exception.getMessage());
    }

    @Test
    void throwExceptionWhenPasswordHashIsBlankTest() {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                new User("adrian@test.com", "   ", "barista")
        );

        assertEquals("Password hash cannot be null or blank", exception.getMessage());
    }

    @Test
    void updateEmailAndNormalizeValueTest() {
        var user = new User(
                "adrian@test.com",
                "$2a$10$samplePasswordHash",
                "barista"
        );

        user.updateEmail("  NEW.EMAIL@TEST.COM  ");

        assertEquals("new.email@test.com", user.getEmail());
    }

    @Test
    void updatePasswordHashTest() {
        var user = new User(
                "adrian@test.com",
                "$2a$10$oldHash",
                "barista"
        );

        user.updatePassword("$2a$10$newHash");

        assertEquals("$2a$10$newHash", user.getPassword());
    }

    @Test
    void updateRoleTest() {
        var user = new User(
                "adrian@test.com",
                "$2a$10$samplePasswordHash",
                "barista"
        );

        user.updateRole("owner");

        assertEquals("owner", user.getRole());
    }
}