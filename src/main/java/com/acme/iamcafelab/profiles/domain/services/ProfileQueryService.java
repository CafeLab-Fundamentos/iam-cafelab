package com.acme.iamcafelab.profiles.domain.services;

import com.acme.iamcafelab.profiles.domain.model.aggregates.Profile;
import com.acme.iamcafelab.profiles.domain.model.queries.GetAllProfilesQuery;
import com.acme.iamcafelab.profiles.domain.model.queries.GetProfileByEmailQuery;
import com.acme.iamcafelab.profiles.domain.model.queries.GetProfileByIamUserIdQuery;
import com.acme.iamcafelab.profiles.domain.model.queries.GetProfileByIdQuery;

import java.util.List;
import java.util.Optional;

public interface ProfileQueryService {

    Optional<Profile> handle(GetProfileByIdQuery query);

    Optional<Profile> handle(GetProfileByEmailQuery query);

    Optional<Profile> handle(GetProfileByIamUserIdQuery query);

    List<Profile> handle(GetAllProfilesQuery query);
}