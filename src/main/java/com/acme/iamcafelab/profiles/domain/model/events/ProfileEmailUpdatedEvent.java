package com.acme.iamcafelab.profiles.domain.model.events;

public record ProfileEmailUpdatedEvent(
        Long iamUserId,
        String oldEmail,
        String newEmail
) {
}