package com.acme.iamcafelab.iam.interfaces.rest.transform;

import com.acme.iamcafelab.iam.domain.model.commands.SignInCommand;
import com.acme.iamcafelab.iam.interfaces.rest.resources.SignInResource;

public class SignInCommandFromResourceAssembler {

    public static SignInCommand toCommandFromResource(SignInResource signInResource) {
        return new SignInCommand(
                signInResource.email(),
                signInResource.password()
        );
    }
}