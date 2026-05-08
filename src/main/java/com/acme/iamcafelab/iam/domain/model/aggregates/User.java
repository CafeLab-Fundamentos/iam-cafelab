package com.acme.iamcafelab.iam.domain.model.aggregates;

import com.acme.iamcafelab.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class User extends AuditableAbstractAggregateRoot<User> {

    @NotBlank
    @Size(max = 100)
    @Column(name = "username", unique = true, nullable = false)
    private String email;

    @NotBlank
    @Size(max = 120)
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role", nullable = true)
    private String role;

    public User() {
        super();
    }

    public User(String email, String password) {
        this();

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or blank");
        }

        this.email = email.trim().toLowerCase();
        this.password = password;
    }

    public User(String email, String password, String role) {
        this(email, password);
        this.role = role;
    }

    public User updateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }

        this.email = email.trim().toLowerCase();
        return this;
    }

    public User updatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or blank");
        }

        this.password = password;
        return this;
    }

    public User updateRole(String role) {
        this.role = role;
        return this;
    }
}