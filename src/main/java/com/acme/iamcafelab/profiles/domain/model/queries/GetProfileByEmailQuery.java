package com.acme.iamcafelab.profiles.domain.model.queries;

import com.acme.iamcafelab.profiles.domain.model.valueobjects.EmailAddress;

public record GetProfileByEmailQuery(EmailAddress emailAddress) {
}