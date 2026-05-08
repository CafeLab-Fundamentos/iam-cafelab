package com.acme.iamcafelab.profiles.application.acl;

import com.acme.iamcafelab.profiles.domain.model.commands.CreateProfileCommand;
import com.acme.iamcafelab.profiles.domain.model.queries.GetProfileByEmailQuery;
import com.acme.iamcafelab.profiles.domain.model.valueobjects.EmailAddress;
import com.acme.iamcafelab.profiles.domain.services.ProfileCommandService;
import com.acme.iamcafelab.profiles.domain.services.ProfileQueryService;
import com.acme.iamcafelab.profiles.interfaces.acl.ProfilesContextFacade;
import org.springframework.stereotype.Service;

@Service
public class ProfilesContextFacadeImpl implements ProfilesContextFacade {

    private final ProfileCommandService profileCommandService;
    private final ProfileQueryService profileQueryService;

    public ProfilesContextFacadeImpl(
            ProfileCommandService profileCommandService,
            ProfileQueryService profileQueryService
    ) {
        this.profileCommandService = profileCommandService;
        this.profileQueryService = profileQueryService;
    }

    @Override
    public Long createProfile(
            String name,
            String email,
            String password,
            String role,
            String cafeteriaName,
            String experience,
            String profilePicture,
            String paymentMethod,
            boolean isFirstLogin,
            String plan,
            boolean hasPlan
    ) {
        var createProfileCommand = new CreateProfileCommand(
                name,
                email,
                password,
                role,
                cafeteriaName,
                experience,
                profilePicture,
                paymentMethod,
                isFirstLogin,
                plan,
                hasPlan
        );

        var profile = profileCommandService.handle(createProfileCommand);

        return profile.isEmpty() ? 0L : profile.get().getId();
    }

    @Override
    public Long fetchUserIdByEmail(String email) {
        var getProfileByEmailQuery = new GetProfileByEmailQuery(new EmailAddress(email));
        var profile = profileQueryService.handle(getProfileByEmailQuery);

        return profile.isEmpty() ? 0L : profile.get().getId();
    }
}