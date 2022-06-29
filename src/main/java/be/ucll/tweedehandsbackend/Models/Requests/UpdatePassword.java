/**
 * Copyright (c) 2022
 * <p>
 * long description for the file
 *
 * @summary short description for the file
 * @author Kevin Coorens <kevin.coorens@student.be>
 * <p>
 * Created at     : 04/03/2022 15:00
 */

package be.ucll.tweedehandsbackend.Models.Requests;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class UpdatePassword {

    @NotBlank(message = "email.not.empty")
    @Email(message = "email.not.valid")
    private String email;

    @NotBlank(message = "password.not.empty")
    private String passwordPlain;

    @NotBlank(message = "password.confirmation.not.empty")
    private String passwordConfirmation;

    @NotBlank(message = "password.old.not.empty")
    private String oldPassword;

    public UpdatePassword() {}

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
}
