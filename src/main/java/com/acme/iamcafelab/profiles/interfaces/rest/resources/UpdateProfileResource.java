package com.acme.iamcafelab.profiles.interfaces.rest.resources;

public record UpdateProfileResource(
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