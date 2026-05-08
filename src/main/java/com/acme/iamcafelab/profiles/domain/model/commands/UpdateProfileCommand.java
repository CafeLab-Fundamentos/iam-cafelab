package com.acme.iamcafelab.profiles.domain.model.commands;

public record UpdateProfileCommand(
        Long userId,
        String name,
        String email,
        String cafeteriaName,
        String experience,
        String paymentMethod,
        Boolean isFirstLogin,
        String plan,
        Boolean hasPlan
) {
}