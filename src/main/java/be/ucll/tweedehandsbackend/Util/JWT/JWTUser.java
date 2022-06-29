/**
 * Copyright (c) 2022
 * <p>
 * long description for the file
 *
 * @summary short description for the file
 * @author Kevin Coorens <kevin.coorens@student.be>
 * <p>
 * Created at     : 04/03/2022 23:41
 */

package be.ucll.tweedehandsbackend.Util.JWT;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import be.ucll.tweedehandsbackend.Models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class JWTUser implements UserDetails {
    private static final long serialVersionUID = 1L;
    private UUID id;
    private String firstName;
    private String lastName;
    private String schoolNumber;
    private String email;
    private boolean oauthOnly;
    private boolean verified;
    @JsonIgnore
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    public JWTUser(UUID id, String firstName, String lastName, String schoolNumber, String email, String password, boolean verified,
                   Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.schoolNumber = schoolNumber;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
        this.verified = verified;
        if (password == null) {
            this.oauthOnly = true;
        } else {
            this.oauthOnly = false;
        }
    }
    public static JWTUser build(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());
        return new JWTUser(
                user.getUuid(),
                user.getFirstName(),
                user.getLastName(),
                user.getSchoolNumber(),
                user.getEmail(),
                user.getPassword(),
                user.getSchoolNumberVerifiedAt()!=null,
                authorities);
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
    public UUID getId() {
        return id;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {return lastName;}
    public String getSchoolNumber() {return schoolNumber;}
    public String getEmail() {return email;}
    public boolean isOauthOnly() {
        return oauthOnly;
    }
    public boolean isVerified() {
        return verified;
    }

    @Override
    public String getPassword() {
        return password;
    }
    @Override
    public String getUsername() {
        return schoolNumber;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        JWTUser user = (JWTUser) o;
        return Objects.equals(id, user.id);
    }
}
