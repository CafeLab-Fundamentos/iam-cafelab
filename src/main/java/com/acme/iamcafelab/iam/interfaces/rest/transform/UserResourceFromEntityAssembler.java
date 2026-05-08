package com.acme.iamcafelab.iam.interfaces.rest.transform;

import com.acme.iamcafelab.iam.domain.model.aggregates.User;
import com.acme.iamcafelab.iam.interfaces.rest.resources.UserResource;

public class UserResourceFromEntityAssembler {

    public static UserResource toResourceFromEntity(User user) {
        return new UserResource(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}