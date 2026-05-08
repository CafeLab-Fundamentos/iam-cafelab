package com.acme.iamcafelab.iam.interfaces.rest.resources;

public record AuthenticatedUserResource(
        Long id,
        String email,
        String role,
        String token
) {
}