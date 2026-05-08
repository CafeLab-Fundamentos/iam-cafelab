package com.acme.iamcafelab.profiles.domain.model.aggregates;

import com.acme.iamcafelab.profiles.domain.model.commands.CreateProfileCommand;
import com.acme.iamcafelab.profiles.domain.model.valueobjects.EmailAddress;
import com.acme.iamcafelab.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;

@Entity
public class Profile extends AuditableAbstractAggregateRoot<Profile> {

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "address", column = @Column(name = "email_address"))
    })
    private EmailAddress emailAddress;

    private String name;
    private String password;
    private String role;
    private String cafeteriaName;
    private String experience;
    private String profilePicture;
    private String paymentMethod;
    private boolean isFirstLogin;
    private String plan;
    private boolean hasPlan;

    /**
     * FK opcional hacia users.id.
     * En la tabla se mantiene como user_id para conservar compatibilidad con tu base anterior.
     */
    @Column(name = "user_id")
    private Long iamUserId;

    public Profile() {
        this.emailAddress = new EmailAddress();
    }

    public Profile(
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
        this.name = name;
        this.emailAddress = new EmailAddress(email);
        this.password = password;
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
                command.password(),
                command.role(),
                command.cafeteriaName(),
                command.experience(),
                command.profilePicture(),
                command.paymentMethod(),
                command.isFirstLogin(),
                command.plan(),
                command.hasPlan()
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

    public void updatePassword(String password) {
        this.password = password;
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

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public String getCafeteriaName() {
        return cafeteriaName;
    }

    public String getExperience() {
        return experience;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public boolean isFirstLogin() {
        return isFirstLogin;
    }

    public String getPlan() {
        return plan;
    }

    public boolean hasPlan() {
        return hasPlan;
    }

    public Long getIamUserId() {
        return iamUserId;
    }

    public void setIamUserId(Long iamUserId) {
        this.iamUserId = iamUserId;
    }
}