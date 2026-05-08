package com.acme.iamcafelab.profiles.interfaces.acl;

public interface ProfilesContextFacade {

    Long createProfile(
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
    );

    Long fetchUserIdByEmail(String email);
}