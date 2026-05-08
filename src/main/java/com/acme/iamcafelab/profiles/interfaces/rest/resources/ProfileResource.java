package com.acme.iamcafelab.profiles.interfaces.rest.resources;

public record ProfileResource(
        Long id,
        String name,
        String email,
        String role,
        String cafeteriaName,
        String experience,
        String profilePicture,
        String paymentMethod,
        String plan,
        boolean hasPlan
) {
}