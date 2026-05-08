package com.acme.iamcafelab.profiles.application.internal.commandservices;

import com.acme.iamcafelab.profiles.domain.model.aggregates.Profile;
import com.acme.iamcafelab.profiles.domain.model.commands.CreateProfileCommand;
import com.acme.iamcafelab.profiles.domain.model.commands.UpdateProfileCommand;
import com.acme.iamcafelab.profiles.domain.model.events.ProfileCreatedEvent;
import com.acme.iamcafelab.profiles.domain.model.valueobjects.EmailAddress;
import com.acme.iamcafelab.profiles.infrastructure.persistence.jpa.repositories.ProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProfileCommandServiceImplTest {

    private ProfileRepository profileRepository;
    private ApplicationEventPublisher eventPublisher;
    private ProfileCommandServiceImpl service;

    @BeforeEach
    void setUp() {
        profileRepository = mock(ProfileRepository.class);
        eventPublisher = mock(ApplicationEventPublisher.class);
        service = new ProfileCommandServiceImpl(profileRepository, eventPublisher);
    }

    @Test
    void createProfileSuccessfullyTest() {
        var command = new CreateProfileCommand(
                "Adrian",
                "adrian@test.com",
                "123456",
                "barista",
                "CafeLab",
                "2 years",
                "profile.png",
                "Visa",
                true,
                "basic",
                true
        );

        when(profileRepository.existsByEmailAddress(new EmailAddress("adrian@test.com"))).thenReturn(false);
        when(profileRepository.save(any(Profile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = service.handle(command);

        assertTrue(result.isPresent());
        assertEquals("Adrian", result.get().getName());
        assertEquals("adrian@test.com", result.get().getEmailAddress());
        assertEquals("barista", result.get().getRole());

        verify(profileRepository).save(any(Profile.class));

        var eventCaptor = ArgumentCaptor.forClass(ProfileCreatedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());

        assertEquals("adrian@test.com", eventCaptor.getValue().email());
        assertEquals("123456", eventCaptor.getValue().password());
        assertEquals("barista", eventCaptor.getValue().role());
    }

    @Test
    void createProfileThrowsExceptionWhenEmailAlreadyExistsTest() {
        var command = new CreateProfileCommand(
                "Adrian",
                "adrian@test.com",
                "123456",
                "barista",
                "CafeLab",
                "2 years",
                "profile.png",
                "Visa",
                true,
                "basic",
                true
        );

        when(profileRepository.existsByEmailAddress(new EmailAddress("adrian@test.com"))).thenReturn(true);

        var exception = assertThrows(IllegalArgumentException.class, () -> service.handle(command));

        assertEquals("Profile with email address already exists", exception.getMessage());
        verify(profileRepository, never()).save(any(Profile.class));
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void updateProfileSuccessfullyTest() {
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

        var command = new UpdateProfileCommand(
                1L,
                "Batman",
                "batman@test.com",
                "La Baticueva",
                "3 years",
                "Mastercard",
                false,
                "premium",
                false
        );

        when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));
        when(profileRepository.save(any(Profile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = service.handle(command);

        assertTrue(result.isPresent());
        assertEquals("Batman", result.get().getName());
        assertEquals("batman@test.com", result.get().getEmailAddress());
        assertEquals("La Baticueva", result.get().getCafeteriaName());
        assertEquals("3 years", result.get().getExperience());
        assertEquals("Mastercard", result.get().getPaymentMethod());
        assertEquals("premium", result.get().getPlan());
        assertFalse(result.get().isFirstLogin());
        assertFalse(result.get().hasPlan());

        verify(profileRepository).save(profile);
    }

    @Test
    void updateProfileReturnsEmptyWhenProfileDoesNotExistTest() {
        var command = new UpdateProfileCommand(
                99L,
                "Batman",
                "batman@test.com",
                "La Baticueva",
                "3 years",
                "Mastercard",
                false,
                "premium",
                false
        );

        when(profileRepository.findById(99L)).thenReturn(Optional.empty());

        var result = service.handle(command);

        assertTrue(result.isEmpty());
        verify(profileRepository, never()).save(any(Profile.class));
    }

    @Test
    void updateProfileOnlyChangesProvidedFieldsTest() {
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

        var command = new UpdateProfileCommand(
                1L,
                "Nuevo Nombre",
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));
        when(profileRepository.save(any(Profile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = service.handle(command);

        assertTrue(result.isPresent());
        assertEquals("Nuevo Nombre", result.get().getName());
        assertEquals("adrian@test.com", result.get().getEmailAddress());
        assertEquals("CafeLab", result.get().getCafeteriaName());
        assertEquals("2 years", result.get().getExperience());
        assertEquals("Visa", result.get().getPaymentMethod());
        assertEquals("basic", result.get().getPlan());
    }
}