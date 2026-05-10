package com.acme.iamcafelab.profiles.domain.model.aggregates;

import com.acme.iamcafelab.iam.domain.model.aggregates.User;
import com.acme.iamcafelab.profiles.domain.model.commands.CreateProfileCommand;
import com.acme.iamcafelab.profiles.domain.model.valueobjects.EmailAddress;
import com.acme.iamcafelab.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;

@Entity
@Table(
        name = "profiles",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_profiles_user_id", columnNames = "user_id")
        }
)
public class Profile extends AuditableAbstractAggregateRoot<Profile> {

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "address", column = @Column(name = "email_address"))
    })
    private EmailAddress emailAddress;

    @Getter
    private String name;
    @Getter
    private String role;
    @Getter
    private String cafeteriaName;
    @Getter
    private String experience;
    @Getter
    private String profilePicture;
    @Getter
    private String paymentMethod;
    private boolean isFirstLogin;

    @Getter
    @Column(name = "subscription_plan")
    private String plan;
    private boolean hasPlan;

    @Getter
    @OneToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "user_id", referencedColumnName = "id", unique = true)
    private User user;

    public Profile() {
        this.emailAddress = new EmailAddress();
    }

    public Profile(
            String name,
            String email,
            String role,
            String cafeteriaName,
            String experience,
            String profilePicture,
            String paymentMethod,
            boolean isFirstLogin,
            String plan,
            boolean hasPlan
    ) {
        this.name = name;
        this.emailAddress = new EmailAddress(email);
        this.role = role;
        this.cafeteriaName = cafeteriaName;
        this.experience = experience;
        this.profilePicture = profilePicture;
        this.paymentMethod = paymentMethod;
        this.isFirstLogin = isFirstLogin;
        this.plan = plan;
        this.hasPlan = hasPlan;
    }

    public Profile(CreateProfileCommand command) {
        this(
                command.name(),
                command.email(),
                command.role(),
                command.cafeteriaName(),
                command.experience(),
                command.profilePicture(),
                command.paymentMethod(),
                Boolean.TRUE.equals(command.isFirstLogin()),
                command.plan(),
                Boolean.TRUE.equals(command.hasPlan())
        );
    }

    public String getEmailAddress() {
        return emailAddress.address();
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateEmailAddress(String email) {
        this.emailAddress = new EmailAddress(email);
    }

    public void updateRole(String role) {
        this.role = role;
    }

    public void updateCafeteriaName(String cafeteriaName) {
        this.cafeteriaName = cafeteriaName;
    }

    public void updateExperience(String experience) {
        this.experience = experience;
    }

    public void updateProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public void updatePaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void updatePlan(String plan) {
        this.plan = plan;
    }

    public void updateFirstLoginStatus(boolean isFirstLogin) {
        this.isFirstLogin = isFirstLogin;
    }

    public void updateHasPlanStatus(boolean hasPlan) {
        this.hasPlan = hasPlan;
    }

    public void linkUser(User user) {
        this.user = user;
    }

    public Long getIamUserId() {
        return user != null ? user.getId() : null;
    }

    public boolean isFirstLogin() {
        return isFirstLogin;
    }

    public boolean hasPlan() {
        return hasPlan;
    }
}

// deploy again