/**
 * Copyright (c) 2022
 * <p>
 * long description for the file
 *
 * @summary short description for the file
 * @author Kevin Coorens <kevin.coorens@student.be>
 * <p>
 * Created at     : 04/03/2022 23:42
 */

package be.ucll.tweedehandsbackend.Enums;

public enum ERole {
    ROLE_USER("User", "Gebruiker"),
    ROLE_MODERATOR("Moderator", "Moderator"),
    ROLE_ADMIN("Admin", "Administrator");

    public final String en;

    public final String nl;

    private ERole(String en, String nl) {
        this.en = en;
        this.nl = nl;
    }
}
