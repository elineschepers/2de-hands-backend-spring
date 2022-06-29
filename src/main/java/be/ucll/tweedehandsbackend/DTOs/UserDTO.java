/**
 * Copyright (c) 2022
 * <p>
 * long description for the file
 *
 * @summary short description for the file
 * @author Kevin Coorens <kevin.coorens@student.be>
 * <p>
 * Created at     : 03/03/2022 12:25
 */

package be.ucll.tweedehandsbackend.DTOs;

import be.ucll.tweedehandsbackend.Models.Role;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class UserDTO {

    @JsonProperty("id")
    private UUID uuid;

    private String firstName;

    private String lastName;

    private String email;

    private String schoolNumber;

    private String phoneNumber;

    private LocalDateTime schoolNumberVerifiedAt;

    @JsonManagedReference
    private Set<RoleDTO> roles = new HashSet<>();

    public UserDTO() {}

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

    public Set<RoleDTO> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleDTO> roles) {
        this.roles = roles;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
