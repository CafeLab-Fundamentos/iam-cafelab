package com.acme.iamcafelab.profiles.interfaces.rest.transform;

import com.acme.iamcafelab.profiles.domain.model.commands.CreateProfileCommand;
import com.acme.iamcafelab.profiles.interfaces.rest.resources.CreateProfileResource;

public class CreateProfileCommandFromResourceAssembler {

    public static CreateProfileCommand toCommandFromResource(CreateProfileResource resource) {
        return new CreateProfileCommand(
                resource.name(),
                resource.email(),
                resource.password(),
                resource.role(),
                resource.cafeteriaName(),
                resource.experience(),
                resource.profilePicture(),
                resource.paymentMethod(),
                resource.isFirstLogin(),
                resource.plan(),
                resource.hasPlan()
        );
    }
}