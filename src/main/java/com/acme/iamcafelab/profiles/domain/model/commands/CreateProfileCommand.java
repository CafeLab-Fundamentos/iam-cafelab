package com.acme.iamcafelab.profiles.domain.model.commands;

public record CreateProfileCommand(
        String name,
        String email,
        String password,
        String role,
        String cafeteriaName,
        String experience,
        String profilePicture,
        String paymentMethod,
        Boolean isFirstLogin,
        String plan,
        Boolean hasPlan
) {
}