package com.acme.iamcafelab.profiles.domain.model.valueobjects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class EmailAddressTest {

    @Test
    void createEmailAddressTest() {
        var emailAddress = new EmailAddress("adrian@test.com");

        assertEquals("adrian@test.com", emailAddress.address());
    }

    @Test
    void createEmptyEmailAddressTest() {
        var emailAddress = new EmailAddress();

        assertNull(emailAddress.address());
    }
}