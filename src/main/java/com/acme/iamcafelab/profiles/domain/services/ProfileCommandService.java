package com.acme.iamcafelab.profiles.domain.services;

import com.acme.iamcafelab.profiles.domain.model.aggregates.Profile;
import com.acme.iamcafelab.profiles.domain.model.commands.CreateProfileCommand;
import com.acme.iamcafelab.profiles.domain.model.commands.UpdateProfileCommand;

import java.util.Optional;

public interface ProfileCommandService {

    Optional<Profile> handle(CreateProfileCommand command);

    Optional<Profile> handle(UpdateProfileCommand command);
}