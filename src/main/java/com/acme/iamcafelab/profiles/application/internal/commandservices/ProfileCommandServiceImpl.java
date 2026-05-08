package com.acme.iamcafelab.profiles.application.internal.commandservices;

import com.acme.iamcafelab.profiles.domain.model.aggregates.Profile;
import com.acme.iamcafelab.profiles.domain.model.commands.CreateProfileCommand;
import com.acme.iamcafelab.profiles.domain.model.commands.UpdateProfileCommand;
import com.acme.iamcafelab.profiles.domain.model.events.ProfileCreatedEvent;
import com.acme.iamcafelab.profiles.domain.model.valueobjects.EmailAddress;
import com.acme.iamcafelab.profiles.domain.services.ProfileCommandService;
import com.acme.iamcafelab.profiles.infrastructure.persistence.jpa.repositories.ProfileRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProfileCommandServiceImpl implements ProfileCommandService {

    private final ProfileRepository profileRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ProfileCommandServiceImpl(
            ProfileRepository profileRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        this.profileRepository = profileRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Optional<Profile> handle(CreateProfileCommand command) {
        var emailAddress = new EmailAddress(command.email());

        if (profileRepository.existsByEmailAddress(emailAddress)) {
            throw new IllegalArgumentException("Profile with email address already exists");
        }

        var profile = new Profile(
                command.name(),
                command.email(),
                command.password(),
                command.role(),
                command.cafeteriaName(),
                command.experience(),
                command.profilePicture(),
                command.paymentMethod(),
                Boolean.TRUE.equals(command.isFirstLogin()),
                command.plan(),
                Boolean.TRUE.equals(command.hasPlan())
        );

        profileRepository.save(profile);

        eventPublisher.publishEvent(
                new ProfileCreatedEvent(command.email(), command.password(), command.role())
        );

        return Optional.of(profile);
    }

    @Override
    public Optional<Profile> handle(UpdateProfileCommand command) {
        var profile = profileRepository.findById(command.userId());

        if (profile.isEmpty()) {
            return Optional.empty();
        }

        var updatedProfile = profile.get();

        if (command.name() != null) {
            updatedProfile.updateName(command.name());
        }

        if (command.email() != null) {
            updatedProfile.updateEmailAddress(command.email());
        }

        if (command.cafeteriaName() != null) {
            updatedProfile.updateCafeteriaName(command.cafeteriaName());
        }

        if (command.experience() != null) {
            updatedProfile.updateExperience(command.experience());
        }

        if (command.paymentMethod() != null) {
            updatedProfile.updatePaymentMethod(command.paymentMethod());
        }

        if (command.isFirstLogin() != null) {
            updatedProfile.updateFirstLoginStatus(command.isFirstLogin());
        }

        if (command.plan() != null) {
            updatedProfile.updatePlan(command.plan());
        }

        if (command.hasPlan() != null) {
            updatedProfile.updateHasPlanStatus(command.hasPlan());
        }

        profileRepository.save(updatedProfile);

        return Optional.of(updatedProfile);
    }
}