package com.acme.iamcafelab.profiles.application.internal.queryservices;

import com.acme.iamcafelab.profiles.domain.model.aggregates.Profile;
import com.acme.iamcafelab.profiles.domain.model.queries.GetAllProfilesQuery;
import com.acme.iamcafelab.profiles.domain.model.queries.GetProfileByEmailQuery;
import com.acme.iamcafelab.profiles.domain.model.queries.GetProfileByIamUserIdQuery;
import com.acme.iamcafelab.profiles.domain.model.queries.GetProfileByIdQuery;
import com.acme.iamcafelab.profiles.domain.model.valueobjects.EmailAddress;
import com.acme.iamcafelab.profiles.infrastructure.persistence.jpa.repositories.ProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProfileQueryServiceImplTest {

    private ProfileRepository profileRepository;
    private ProfileQueryServiceImpl service;

    @BeforeEach
    void setUp() {
        profileRepository = mock(ProfileRepository.class);
        service = new ProfileQueryServiceImpl(profileRepository);
    }

    @Test
    void getProfileByIdTest() {
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

        when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));

        var result = service.handle(new GetProfileByIdQuery(1L));

        assertTrue(result.isPresent());
        assertEquals("Adrian", result.get().getName());
    }

    @Test
    void getProfileByEmailNormalizesEmailTest() {
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

        when(profileRepository.findByNormalizedEmail("adrian@test.com")).thenReturn(Optional.of(profile));

        var result = service.handle(new GetProfileByEmailQuery(new EmailAddress("  ADRIAN@TEST.COM  ")));

        assertTrue(result.isPresent());
        assertEquals("adrian@test.com", result.get().getEmailAddress());
        verify(profileRepository).findByNormalizedEmail("adrian@test.com");
    }

    @Test
    void getProfileByEmailReturnsEmptyWhenEmailIsNullTest() {
        var result = service.handle(new GetProfileByEmailQuery(new EmailAddress(null)));

        assertTrue(result.isEmpty());
        verify(profileRepository, never()).findByNormalizedEmail(anyString());
    }

    @Test
    void getProfileByIamUserIdTest() {
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

        when(profileRepository.findByIamUserId(10L)).thenReturn(Optional.of(profile));

        var result = service.handle(new GetProfileByIamUserIdQuery(10L));

        assertTrue(result.isPresent());
        assertEquals("Adrian", result.get().getName());
    }

    @Test
    void getProfileByIamUserIdReturnsEmptyWhenUserIdIsNullTest() {
        var result = service.handle(new GetProfileByIamUserIdQuery(null));

        assertTrue(result.isEmpty());
        verify(profileRepository, never()).findByIamUserId(any());
    }

    @Test
    void getAllProfilesTest() {
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

        when(profileRepository.findAll()).thenReturn(List.of(profileOne, profileTwo));

        var result = service.handle(new GetAllProfilesQuery());

        assertEquals(2, result.size());
        assertEquals("Adrian", result.get(0).getName());
        assertEquals("Batman", result.get(1).getName());
    }
}