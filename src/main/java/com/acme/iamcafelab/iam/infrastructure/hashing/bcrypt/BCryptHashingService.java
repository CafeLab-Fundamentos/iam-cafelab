package com.acme.iamcafelab.iam.infrastructure.hashing.bcrypt;

import com.acme.iamcafelab.iam.application.internal.outboundservices.hashing.HashingService;
import org.springframework.security.crypto.password.PasswordEncoder;

public interface BCryptHashingService extends HashingService, PasswordEncoder {
}