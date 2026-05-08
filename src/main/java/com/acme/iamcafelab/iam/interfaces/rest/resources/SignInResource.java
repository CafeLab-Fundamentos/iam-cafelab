package com.acme.iamcafelab.iam.interfaces.rest.resources;

public record SignInResource(
        String email,
        String password
) {
}