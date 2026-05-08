package com.acme.iamcafelab.iam.interfaces.rest.transform;

import com.acme.iamcafelab.iam.domain.model.commands.SignUpCommand;
import com.acme.iamcafelab.iam.interfaces.rest.resources.SignUpResource;

public class SignUpCommandFromResourceAssembler {

    public static SignUpCommand toCommandFromResource(SignUpResource resource) {
        return new SignUpCommand(
                resource.email(),
                resource.password(),
                resource.role()
        );
    }
}