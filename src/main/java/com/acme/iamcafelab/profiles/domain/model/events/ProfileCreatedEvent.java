package com.acme.iamcafelab.profiles.domain.model.events;

public record ProfileCreatedEvent(String email, String password, String role) {
}