/**
 * Copyright (c) 2022
 * <p>
 * long description for the file
 *
 * @summary short description for the file
 * @author Kevin Coorens <kevin.coorens@student.be>
 * <p>
 * Created at     : 10/03/2022 18:08
 */

package be.ucll.tweedehandsbackend.Models.Requests;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class OauthComplete {

    @NotBlank(message = "email.not.empty")
    @Email(message = "email.not.valid")
    private String email;

    @NotBlank(message = "password.not.empty")
    private String passwordPlain;

    @NotBlank(message = "password.confirmation.not.empty")
    private String passwordConfirmation;

    public OauthComplete() {}

    public OauthComplete(String email, String passwordPlain, String passwordConfirmation) {
        this.setEmail(email);
        this.setPasswordPlain(passwordPlain);
        this.setPasswordConfirmation(passwordConfirmation);
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
