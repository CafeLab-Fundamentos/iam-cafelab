package com.acme.iamcafelab.iam.interfaces.rest.resources;

public record SignUpResource(
        String email,
        String password,
        String role
) {

    public SignUpResource(String email, String password) {
        this(email, password, null);
    }
}