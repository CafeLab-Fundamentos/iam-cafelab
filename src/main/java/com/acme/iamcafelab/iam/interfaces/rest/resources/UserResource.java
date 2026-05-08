package com.acme.iamcafelab.iam.interfaces.rest.resources;

import java.util.Date;

public record UserResource(
        Long userId,
        String email,
        String role,
        Date createdAt,
        Date updatedAt
) {
}