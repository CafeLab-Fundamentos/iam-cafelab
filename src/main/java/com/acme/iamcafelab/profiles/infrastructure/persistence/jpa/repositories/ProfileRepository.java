package com.acme.iamcafelab.profiles.infrastructure.persistence.jpa.repositories;

import com.acme.iamcafelab.profiles.domain.model.aggregates.Profile;
import com.acme.iamcafelab.profiles.domain.model.valueobjects.EmailAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByEmailAddress(EmailAddress emailAddress);

    @Query("SELECT p FROM Profile p WHERE LOWER(TRIM(p.emailAddress.address)) = :email")
    Optional<Profile> findByNormalizedEmail(@Param("email") String normalizedEmail);

    Optional<Profile> findByIamUserId(Long iamUserId);

    List<Profile> findByIamUserIdIsNull();

    boolean existsByEmailAddress(EmailAddress emailAddress);
}