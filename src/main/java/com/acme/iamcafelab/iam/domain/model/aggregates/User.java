package com.acme.iamcafelab.iam.domain.model.aggregates;

import com.acme.iamcafelab.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Locale;

@Entity
@Getter
@Setter
public class User extends AuditableAbstractAggregateRoot<User> {

    @NotBlank
    @Size(max = 100)
    @Column(name = "email_address", unique = true, nullable = false)
    private String email;

    @NotBlank
    @Size(max = 120)
    @Column(name = "password_hash", nullable = false)
    private String password;

    @Column(name = "role")
    private String role;

    public User() {
        super();
    }

    public User(String email, String passwordHash) {
        this();

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }

        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("Password hash cannot be null or blank");
        }

        this.email = normalizeEmail(email);
        this.password = passwordHash;
    }

    public User(String email, String passwordHash, String role) {
        this(email, passwordHash);
        this.role = role;
    }

    public User updateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }

        this.email = normalizeEmail(email);
        return this;
    }

    public User updatePassword(String passwordHash) {
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("Password hash cannot be null or blank");
        }

        this.password = passwordHash;
        return this;
    }

    public User updateRole(String role) {
        this.role = role;
        return this;
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}