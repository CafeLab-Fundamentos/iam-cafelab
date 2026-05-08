package com.acme.iamcafelab.iam.interfaces.rest;

import com.acme.iamcafelab.iam.domain.exceptions.SignInFailedException;
import com.acme.iamcafelab.iam.domain.exceptions.SignUpFailedException;
import com.acme.iamcafelab.iam.domain.model.commands.SignInCommand;
import com.acme.iamcafelab.iam.domain.services.UserCommandService;
import com.acme.iamcafelab.iam.interfaces.rest.resources.AuthenticatedUserResource;
import com.acme.iamcafelab.iam.interfaces.rest.resources.SignInResource;
import com.acme.iamcafelab.iam.interfaces.rest.resources.SignUpResource;
import com.acme.iamcafelab.iam.interfaces.rest.transform.AuthenticatedUserResourceFromEntityAssembler;
import com.acme.iamcafelab.iam.interfaces.rest.transform.SignInCommandFromResourceAssembler;
import com.acme.iamcafelab.iam.interfaces.rest.transform.SignUpCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/authentication", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Authentication", description = "IAM authentication endpoints")
public class AuthenticationController {

    private final UserCommandService userCommandService;

    public AuthenticationController(UserCommandService userCommandService) {
        this.userCommandService = userCommandService;
    }

    @PostMapping("/sign-in")
    @Operation(
            summary = "Sign-in",
            description = "Authenticate user with email and password.",
            security = {@SecurityRequirement(name = "")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication successful."),
            @ApiResponse(responseCode = "404", description = "Invalid credentials.")
    })
    public ResponseEntity<AuthenticatedUserResource> signIn(@RequestBody SignInResource signInResource) {
        var signInCommand = SignInCommandFromResourceAssembler.toCommandFromResource(signInResource);
        var authenticatedUser = userCommandService.handle(signInCommand);

        if (authenticatedUser.isEmpty()) {
            throw new SignInFailedException();
        }

        var authenticatedUserResource =
                AuthenticatedUserResourceFromEntityAssembler.toResourceFromEntity(
                        authenticatedUser.get().getLeft(),
                        authenticatedUser.get().getRight()
                );

        return ResponseEntity.ok(authenticatedUserResource);
    }

    @PostMapping("/sign-up")
    @Operation(
            summary = "Sign-up",
            description = "Create an IAM user and return an authentication token.",
            security = {@SecurityRequirement(name = "")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created and authenticated."),
            @ApiResponse(responseCode = "400", description = "Invalid request.")
    })
    public ResponseEntity<AuthenticatedUserResource> signUp(@RequestBody SignUpResource signUpResource) {
        var signUpCommand = SignUpCommandFromResourceAssembler.toCommandFromResource(signUpResource);
        var user = userCommandService.handle(signUpCommand);

        if (user.isEmpty()) {
            throw new SignUpFailedException();
        }

        var signInCommand = new SignInCommand(
                signUpResource.email(),
                signUpResource.password()
        );

        var authenticatedUser = userCommandService.handle(signInCommand);

        if (authenticatedUser.isEmpty()) {
            throw new SignUpFailedException();
        }

        var resource =
                AuthenticatedUserResourceFromEntityAssembler.toResourceFromEntity(
                        authenticatedUser.get().getLeft(),
                        authenticatedUser.get().getRight()
                );

        return new ResponseEntity<>(resource, HttpStatus.CREATED);
    }
}