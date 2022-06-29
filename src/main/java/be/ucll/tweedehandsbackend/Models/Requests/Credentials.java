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

import javax.validation.constraints.NotBlank;

public class Credentials {

    @NotBlank(message = "schoolnumber.not.empty")
    private String schoolnumber;

    @NotBlank(message = "password.not.empty")
    private String password;

    public Credentials() {}

    public String getSchoolnumber() {
        return schoolnumber;
    }

    public void setSchoolnumber(String schoolnumber) {
        this.schoolnumber = schoolnumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
