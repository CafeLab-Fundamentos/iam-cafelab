package com.acme.iamcafelab.iam.domain.model.commands;

public record SignUpCommand(String email, String password, String role) {

    public SignUpCommand(String email, String password) {
        this(email, password, null);
    }
}