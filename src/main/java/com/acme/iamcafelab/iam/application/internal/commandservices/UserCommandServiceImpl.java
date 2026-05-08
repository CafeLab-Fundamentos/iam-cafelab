package com.acme.iamcafelab.iam.application.internal.commandservices;

import com.acme.iamcafelab.iam.application.internal.outboundservices.hashing.HashingService;
import com.acme.iamcafelab.iam.application.internal.outboundservices.tokens.TokenService;
import com.acme.iamcafelab.iam.domain.model.aggregates.User;
import com.acme.iamcafelab.iam.domain.model.commands.SignInCommand;
import com.acme.iamcafelab.iam.domain.model.commands.SignUpCommand;
import com.acme.iamcafelab.iam.domain.services.UserCommandService;
import com.acme.iamcafelab.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Optional;

@Service
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepository;
    private final HashingService hashingService;
    private final TokenService tokenService;

    public UserCommandServiceImpl(
            UserRepository userRepository,
            HashingService hashingService,
            TokenService tokenService
    ) {
        this.userRepository = userRepository;
        this.hashingService = hashingService;
        this.tokenService = tokenService;
    }

    @Override
    public Optional<ImmutablePair<User, String>> handle(SignInCommand command) {
        String normalizedEmail = normalizeEmail(command.email());

        var user = userRepository
                .findByEmail(normalizedEmail)
                .or(() -> userRepository.findByEmailIgnoreCase(normalizedEmail));

        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        if (!hashingService.matches(command.password(), user.get().getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        var token = tokenService.generateToken(user.get().getEmail());

        return Optional.of(ImmutablePair.of(user.get(), token));
    }

    @Override
    @Transactional
    public Optional<User> handle(SignUpCommand command) {
        String normalizedEmail = normalizeEmail(command.email());

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new RuntimeException("Email already exists");
        }

        var hashedPassword = hashingService.encode(command.password());
        var user = new User(normalizedEmail, hashedPassword, command.role());

        var savedUser = userRepository.save(user);

        return Optional.of(savedUser);
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }

        return email.trim().toLowerCase(Locale.ROOT);
    }
}