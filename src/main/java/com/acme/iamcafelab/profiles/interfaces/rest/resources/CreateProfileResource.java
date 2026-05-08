package com.acme.iamcafelab.profiles.interfaces.rest.resources;

public record CreateProfileResource(
        String name,
        String email,
        String password,
        String role,
        String cafeteriaName,
        String experience,
        String profilePicture,
        String paymentMethod,
        boolean isFirstLogin,
        String plan,
        boolean hasPlan
) {

    public CreateProfileResource {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }

        if (role == null || role.isBlank()) {
            throw new IllegalArgumentException("Role is required");
        }

        if (!role.equalsIgnoreCase("barista") && !role.equalsIgnoreCase("owner")) {
            throw new IllegalArgumentException("Role must be either 'barista' or 'owner'");
        }
    }
}