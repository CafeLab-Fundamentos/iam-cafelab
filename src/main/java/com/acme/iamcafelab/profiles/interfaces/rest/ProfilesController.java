package com.acme.iamcafelab.profiles.interfaces.rest;

import com.acme.iamcafelab.profiles.domain.exceptions.ProfileCreationFailedException;
import com.acme.iamcafelab.profiles.domain.exceptions.ProfileNotFoundException;
import com.acme.iamcafelab.profiles.domain.model.queries.GetAllProfilesQuery;
import com.acme.iamcafelab.profiles.domain.model.queries.GetProfileByEmailQuery;
import com.acme.iamcafelab.profiles.domain.model.queries.GetProfileByIdQuery;
import com.acme.iamcafelab.profiles.domain.model.valueobjects.EmailAddress;
import com.acme.iamcafelab.profiles.domain.services.ProfileCommandService;
import com.acme.iamcafelab.profiles.domain.services.ProfileQueryService;
import com.acme.iamcafelab.profiles.interfaces.rest.resources.CreateProfileResource;
import com.acme.iamcafelab.profiles.interfaces.rest.resources.ProfileResource;
import com.acme.iamcafelab.profiles.interfaces.rest.resources.UpdateProfileResource;
import com.acme.iamcafelab.profiles.interfaces.rest.transform.CreateProfileCommandFromResourceAssembler;
import com.acme.iamcafelab.profiles.interfaces.rest.transform.ProfileResourceFromEntityAssembler;
import com.acme.iamcafelab.profiles.interfaces.rest.transform.UpdateProfileCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/profiles", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Profiles", description = "Available Profile Endpoints")
public class ProfilesController {

    private final ProfileCommandService profileCommandService;
    private final ProfileQueryService profileQueryService;

    public ProfilesController(
            ProfileCommandService profileCommandService,
            ProfileQueryService profileQueryService
    ) {
        this.profileCommandService = profileCommandService;
        this.profileQueryService = profileQueryService;
    }

    @PostMapping
    @Operation(summary = "Create a new profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Profile created"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    public ResponseEntity<ProfileResource> createProfile(@RequestBody CreateProfileResource resource) {
        var createProfileCommand = CreateProfileCommandFromResourceAssembler.toCommandFromResource(resource);
        var profile = profileCommandService.handle(createProfileCommand);

        if (profile.isEmpty()) {
            throw new ProfileCreationFailedException();
        }

        var profileResource = ProfileResourceFromEntityAssembler.toResourceFromEntity(profile.get());

        return new ResponseEntity<>(profileResource, HttpStatus.CREATED);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get a profile by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile found"),
            @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    public ResponseEntity<ProfileResource> getProfileById(@PathVariable Long userId) {
        var getProfileByIdQuery = new GetProfileByIdQuery(userId);
        var profile = profileQueryService.handle(getProfileByIdQuery);

        if (profile.isEmpty()) {
            throw new ProfileNotFoundException(userId);
        }

        var profileResource = ProfileResourceFromEntityAssembler.toResourceFromEntity(profile.get());

        return ResponseEntity.ok(profileResource);
    }

    @GetMapping(params = "email")
    @Operation(summary = "Get profile by email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile found"),
            @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    public ResponseEntity<ProfileResource> getProfileByEmail(@RequestParam String email) {
        var getProfileByEmailQuery = new GetProfileByEmailQuery(new EmailAddress(email));
        var profile = profileQueryService.handle(getProfileByEmailQuery);

        if (profile.isEmpty()) {
            throw new ProfileNotFoundException(email);
        }

        var profileResource = ProfileResourceFromEntityAssembler.toResourceFromEntity(profile.get());

        return ResponseEntity.ok(profileResource);
    }

    @GetMapping
    @Operation(summary = "Get all profiles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profiles found")
    })
    public ResponseEntity<List<ProfileResource>> getAllProfiles() {
        var profiles = profileQueryService.handle(new GetAllProfilesQuery());

        var profileResources = profiles.stream()
                .map(ProfileResourceFromEntityAssembler::toResourceFromEntity)
                .toList();

        return ResponseEntity.ok(profileResources);
    }

    @PatchMapping("/{userId}")
    @Operation(summary = "Update profile", description = "Update an existing profile.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile updated successfully."),
            @ApiResponse(responseCode = "400", description = "Bad request."),
            @ApiResponse(responseCode = "404", description = "Profile not found.")
    })
    public ResponseEntity<ProfileResource> updateProfile(
            @PathVariable Long userId,
            @RequestBody UpdateProfileResource resource
    ) {
        var updateProfileCommand = UpdateProfileCommandFromResourceAssembler.toCommandFromResource(userId, resource);
        var updatedProfile = profileCommandService.handle(updateProfileCommand);

        if (updatedProfile.isEmpty()) {
            throw new ProfileNotFoundException(userId);
        }

        var profileResource = ProfileResourceFromEntityAssembler.toResourceFromEntity(updatedProfile.get());

        return ResponseEntity.ok(profileResource);
    }
}