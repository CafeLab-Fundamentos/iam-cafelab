package com.acme.iamcafelab.iam.application.internal.eventhandlers;

import com.acme.iamcafelab.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import com.acme.iamcafelab.profiles.domain.model.events.ProfileEmailUpdatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Component
public class ProfileEmailUpdatedEventHandler {

    private final UserRepository userRepository;

    public ProfileEmailUpdatedEventHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @EventListener
    @Transactional
    public void on(ProfileEmailUpdatedEvent event) {
        if (event.newEmail() == null || event.newEmail().isBlank()) {
            return;
        }

        String normalizedNewEmail = event.newEmail().trim().toLowerCase(Locale.ROOT);

        var existingUserWithNewEmail = userRepository.findByEmailIgnoreCase(normalizedNewEmail);

        if (existingUserWithNewEmail.isPresent()
                && !existingUserWithNewEmail.get().getId().equals(event.iamUserId())) {
            throw new IllegalArgumentException("Email already exists");
        }

        var user = event.iamUserId() != null
                ? userRepository.findById(event.iamUserId())
                : userRepository.findByEmailIgnoreCase(event.oldEmail());

        user.ifPresent(existingUser -> {
            existingUser.updateEmail(normalizedNewEmail);
            userRepository.save(existingUser);
        });
    }
}