package com.acme.iamcafelab.profiles.interfaces.rest.transform;

import com.acme.iamcafelab.profiles.domain.model.aggregates.Profile;
import com.acme.iamcafelab.profiles.interfaces.rest.resources.ProfileResource;

public class ProfileResourceFromEntityAssembler {

    public static ProfileResource toResourceFromEntity(Profile entity) {
        return new ProfileResource(
                entity.getId(),
                entity.getName(),
                entity.getEmailAddress(),
                entity.getRole(),
                entity.getCafeteriaName(),
                entity.getExperience(),
                entity.getProfilePicture(),
                entity.getPaymentMethod(),
                entity.getPlan(),
                entity.hasPlan()
        );
    }
}