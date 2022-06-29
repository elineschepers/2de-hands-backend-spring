package be.ucll.tweedehandsbackend.Models;

import be.ucll.tweedehandsbackend.Enums.ERole;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

import javax.persistence.*;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "users")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    private UUID uuid = UUID.randomUUID();

    @NotBlank(message ="firstName.is.missing")
    private String firstName;

    @NotBlank(message ="lastName.is.missing")
    private String lastName;

    @Column(unique = true)
    @Email(message = "email.not.valid")
    @NotBlank(message ="email.is.missing")
    private String email;

    private String phoneNumber;

    private String schoolEmail;

    private LocalDateTime emailVerifiedAt;

    private String password;

    @Transient
    @NotBlank(message = "password.is.missing")
    private String passwordPlain;

    @Transient
    @NotBlank(message = "password.confirmation.is.missing")
    private String passwordConfirmation;

    @Column(unique = true)
    @NotBlank(message = "schoolnumber.is.missing")
    private String schoolNumber;

    private LocalDateTime schoolNumberVerifiedAt;

    @Transient
    private boolean schoolNumberVerified;

    private String locale;

    private LocalDateTime latestDataExport;

    private LocalDateTime bannedAt;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    @Transient
    private String hCaptchaResponse;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(	name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @Transient
    private List<ERole> eRoles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Offer> offers;

    public User() {}

    /**
     * User created through OAuth
     * @param schoolNumber
     * @param lastName
     * @param firstName
     * @param schoolEmail
     */
    public User(String schoolNumber, String lastName, String firstName, String schoolEmail) {
        this.setSchoolNumber(schoolNumber);
        this.setSchoolNumberVerifiedAt(LocalDateTime.now());
        this.setLastName(lastName);
        this.setFirstName(firstName);
        this.setSchoolEmail(schoolEmail);
        this.setEmail(schoolEmail);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSchoolEmail() {
        return schoolEmail;
    }

    public void setSchoolEmail(String schoolEmail) {
        this.schoolEmail = schoolEmail;
    }

    public LocalDateTime getEmailVerifiedAt() {
        return emailVerifiedAt;
    }

    public void setEmailVerifiedAt(LocalDateTime emailVerifiedAt) {
        this.emailVerifiedAt = emailVerifiedAt;
    }

    public boolean isSchoolNumberVerified() {
        return schoolNumberVerified;
    }

    public void setSchoolNumberVerified(boolean schoolNumberVerified) {
        this.schoolNumberVerified = schoolNumberVerified;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        Argon2PasswordEncoder encoder = new Argon2PasswordEncoder();
        this.password = encoder.encode(password);
    }

    public String getPasswordPlain() {
        return passwordPlain;
    }

    public void setPasswordPlain(String passwordPlain) {
        this.passwordPlain = passwordPlain;
    }

    public String getPasswordConfirmation() {
        return passwordConfirmation;
    }

    public void setPasswordConfirmation(String passwordConfirmation) {
        this.passwordConfirmation = passwordConfirmation;
    }

    public String getSchoolNumber() {
        return schoolNumber;
    }

    public void setSchoolNumber(String schoolNumber) {
        this.schoolNumber = schoolNumber;
    }

    public LocalDateTime getSchoolNumberVerifiedAt() {
        return schoolNumberVerifiedAt;
    }

    public void setSchoolNumberVerifiedAt(LocalDateTime schoolNumberVerifiedAt) {
        this.schoolNumberVerifiedAt = schoolNumberVerifiedAt;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public LocalDateTime getLatestDataExport() {
        return latestDataExport;
    }

    public void setLatestDataExport(LocalDateTime latestDataExport) {
        this.latestDataExport = latestDataExport;
    }

    public LocalDateTime getBannedAt() {
        return bannedAt;
    }

    public void setBannedAt(LocalDateTime bannedAt) {
        this.bannedAt = bannedAt;
    }

    public boolean isBanned() {
        return bannedAt != null;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Offer> getOffers() {
        return offers;
    }

    public void setOffers(List<Offer> offers) {
        this.offers = offers;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public void deleteRole(Role role) {
        this.roles.remove(role);
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String gethCaptchaResponse() {
        return hCaptchaResponse;
    }

    public void sethCaptchaResponse(String hCaptchaResponse) {
        this.hCaptchaResponse = hCaptchaResponse;
    }

    public List<ERole> geteRoles() {
        return eRoles;
    }

    public void seteRoles(List<ERole> eRoles) {
        this.eRoles = eRoles;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", uuid=" + uuid +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", schoolNumber='" + schoolNumber + '\'' +
                '}';
    }
}
