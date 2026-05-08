package com.acme.iamcafelab.profiles.domain.model.aggregates;

import com.acme.iamcafelab.iam.domain.model.aggregates.User;
import com.acme.iamcafelab.profiles.domain.model.commands.CreateProfileCommand;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProfileTest {

    @Test
    void createProfileFromConstructorTest() {
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

        assertEquals("Adrian", profile.getName());
        assertEquals("adrian@test.com", profile.getEmailAddress());
        assertEquals("barista", profile.getRole());
        assertEquals("CafeLab", profile.getCafeteriaName());
        assertEquals("2 years", profile.getExperience());
        assertEquals("profile.png", profile.getProfilePicture());
        assertEquals("Visa", profile.getPaymentMethod());
        assertTrue(profile.isFirstLogin());
        assertEquals("basic", profile.getPlan());
        assertTrue(profile.hasPlan());
    }

    @Test
    void createProfileFromCommandTest() {
        var command = new CreateProfileCommand(
                "Bruno",
                "bruno@test.com",
                "123456",
                "owner",
                "La Cafeteria",
                "5 years",
                "photo.jpg",
                "Mastercard",
                true,
                "complete",
                true
        );

        var profile = new Profile(command);

        assertEquals("Bruno", profile.getName());
        assertEquals("bruno@test.com", profile.getEmailAddress());
        assertEquals("owner", profile.getRole());
        assertEquals("La Cafeteria", profile.getCafeteriaName());
        assertEquals("5 years", profile.getExperience());
        assertEquals("photo.jpg", profile.getProfilePicture());
        assertEquals("Mastercard", profile.getPaymentMethod());
        assertTrue(profile.isFirstLogin());
        assertEquals("complete", profile.getPlan());
        assertTrue(profile.hasPlan());
    }

    @Test
    void applyProfileUpdatesTest() {
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

        profile.updateName("Batman");
        profile.updateEmailAddress("batman@test.com");
        profile.updateRole("owner");
        profile.updateCafeteriaName("La Baticueva");
        profile.updateExperience("3 years");
        profile.updatePaymentMethod("Mastercard");
        profile.updatePlan("premium");
        profile.updateFirstLoginStatus(false);
        profile.updateHasPlanStatus(false);

        assertEquals("Batman", profile.getName());
        assertEquals("batman@test.com", profile.getEmailAddress());
        assertEquals("owner", profile.getRole());
        assertEquals("La Baticueva", profile.getCafeteriaName());
        assertEquals("3 years", profile.getExperience());
        assertEquals("Mastercard", profile.getPaymentMethod());
        assertEquals("premium", profile.getPlan());
        assertFalse(profile.isFirstLogin());
        assertFalse(profile.hasPlan());
    }

    @Test
    void linkUserToProfileTest() {
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

        var user = new User(
                "adrian@test.com",
                "$2a$10$samplePasswordHash",
                "barista"
        );

        profile.linkUser(user);

        assertEquals(user, profile.getUser());
    }

    @Test
    void returnNullIamUserIdWhenProfileHasNoUserLinkedTest() {
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

        assertNull(profile.getIamUserId());
    }
}