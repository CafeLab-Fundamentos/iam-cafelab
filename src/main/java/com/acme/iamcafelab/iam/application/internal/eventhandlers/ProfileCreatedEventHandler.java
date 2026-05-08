package com.acme.iamcafelab.iam.application.internal.eventhandlers;

import com.acme.iamcafelab.iam.domain.model.commands.SignUpCommand;
import com.acme.iamcafelab.iam.domain.services.UserCommandService;
import com.acme.iamcafelab.profiles.domain.model.events.ProfileCreatedEvent;
import com.acme.iamcafelab.profiles.infrastructure.persistence.jpa.repositories.ProfileRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Component
public class ProfileCreatedEventHandler {

    private final UserCommandService userCommandService;
    private final ProfileRepository profileRepository;

    public ProfileCreatedEventHandler(
            UserCommandService userCommandService,
            ProfileRepository profileRepository
    ) {
        this.userCommandService = userCommandService;
        this.profileRepository = profileRepository;
    }

    @EventListener
    @Transactional
    public void on(ProfileCreatedEvent event) {
        var userOpt = userCommandService.handle(
                new SignUpCommand(event.email(), event.password(), event.role())
        );

        if (userOpt.isEmpty()) {
            return;
        }

        String normalizedEmail = event.email().trim().toLowerCase(Locale.ROOT);

        profileRepository
                .findByNormalizedEmail(normalizedEmail)
                .ifPresent(profile -> {
                    profile.setIamUserId(userOpt.get().getId());
                    profileRepository.save(profile);
                });
    }
}