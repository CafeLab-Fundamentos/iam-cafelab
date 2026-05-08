package com.acme.iamcafelab.iam.interfaces.rest.transform;

import com.acme.iamcafelab.iam.domain.model.aggregates.User;
import com.acme.iamcafelab.iam.interfaces.rest.resources.AuthenticatedUserResource;

public class AuthenticatedUserResourceFromEntityAssembler {

    public static AuthenticatedUserResource toResourceFromEntity(User user, String token) {
        return new AuthenticatedUserResource(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                token
        );
    }
}