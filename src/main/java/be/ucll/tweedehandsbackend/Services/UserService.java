package be.ucll.tweedehandsbackend.Services;

import be.ucll.tweedehandsbackend.Enums.ERole;
import be.ucll.tweedehandsbackend.Exceptions.RecordNotFoundException;
import be.ucll.tweedehandsbackend.Models.Requests.Credentials;
import be.ucll.tweedehandsbackend.Models.Requests.OauthComplete;
import be.ucll.tweedehandsbackend.Models.Requests.UpdatePassword;
import be.ucll.tweedehandsbackend.Models.Role;
import be.ucll.tweedehandsbackend.Models.User;
import be.ucll.tweedehandsbackend.Repositories.RoleRepository;
import be.ucll.tweedehandsbackend.Repositories.UserRepository;
import be.ucll.tweedehandsbackend.Responses.HCaptchaResponse;
import be.ucll.tweedehandsbackend.Util.JWT.JWTUser;
import be.ucll.tweedehandsbackend.Util.SecureUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.security.auth.login.LoginException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private RoleRepository roleRepository;

    @Value("${hCaptcha.secret.key}")
    private String hCaptchaSecretKey;

    /**
     * Add a given User object to the database
     *
     * @param user the User object to add
     * @return the saved User
     * @throws LoginException if the schoolNumber or email already exists in the database
     */
    public User add(User user) throws LoginException {

        if(schoolNumberExists(user.getSchoolNumber())) {
            throw new LoginException("schoolnumber.exists");
        }
        if(user.getEmail() != null && emailExists(user.getEmail())) {
            throw new LoginException("email.exists");
        }
        if(user.isSchoolNumberVerified()) {
            user.setSchoolNumberVerifiedAt(LocalDateTime.now());
        }
        user.setSchoolNumber(user.getSchoolNumber().toLowerCase());
        grantRole(user, ERole.ROLE_USER);
        return repository.save(user);
    }

    /**
     * Add a User object from a form if the captcha is valid and the passwords match
     *
     * @param user the User object to add
     * @return the saved User
     * @throws LoginException if the passwords don't match
     */
    public User addFromForm(User user) throws LoginException {
        if(!verifyCaptcha(user.gethCaptchaResponse())) {
            throw new LoginException("captcha.invalid");
        }
        if(!user.getPasswordPlain().equals(user.getPasswordConfirmation()))
            throw new LoginException("passwords.no.match");
        user.setPassword(user.getPasswordPlain());
        return this.add(user);
    }

    /**
     * Verify validity of a captcha
     *
     * @param captcha captcha code from form
     * @return true if valid else false
     */
    private boolean verifyCaptcha(String captcha) {
        String sb = "response=" +
                captcha +
                "&secret=" +
                this.hCaptchaSecretKey;

        RestTemplate restTemplate = new RestTemplate();
        URI verifyUri = URI.create("https://hcaptcha.com/siteverify?" + sb);

        HCaptchaResponse captchaResponse = restTemplate.getForObject(verifyUri, HCaptchaResponse.class);
        if (captchaResponse == null)
            return false;
        return captchaResponse.isSuccess();
    }

    /**
     * Get a User (by id) from the database
     *
     * @param id the id of the requested User
     * @return the requested User object
     * @throws RecordNotFoundException if the id is not present in the database
     */
    public User get(Long id) throws RecordNotFoundException {
        Optional<User> user = repository.findById(id);

        if (user.isEmpty()) {
            throw new RecordNotFoundException("user.not.found");
        }
        return user.get();
    }

    /**
     * Get a User (by email) from the database
     *
     * @param email the email of the requested User
     * @return the requested User object
     * @throws RecordNotFoundException if the email is not present in the database
     */
    public User getByEmail(String email) {
        User user = repository.findUserByEmail(email);
        if (user == null) {
            throw new RecordNotFoundException("user.not.found");
        }

        return user;
    }

    /**
     * Get a User (by uuid) from the database
     *
     * @param uuid the uuid of the requested User
     * @return the requested User object
     * @throws RecordNotFoundException if the uuid is not present in the database
     */
    public User getByUuid(UUID uuid) {
        User user = repository.findUserByUuid(uuid);
        if (user == null) {
            throw new RecordNotFoundException("user.not.found");
        }
        return user;
    }

    /**
     * Get a User (by schoolNumber) from the database
     *
     * @param schoolNumber the schoolNumber of the requested User
     * @return the requested User object
     * @throws RecordNotFoundException if the schoolNumber is not present in the database
     */
    public User getBySchoolNumber(String schoolNumber) {
        User user = repository.findBySchoolNumber(schoolNumber);
        if (user == null) {
            throw new RecordNotFoundException("user.not.found");
        }
        return user;
    }

    /**
     * Get a User (by schoolNumber) from the database
     * override for Spring authentication
     *
     * @param username the username of the requested User
     * @return the requested User object
     * @throws UsernameNotFoundException if the username is not present in the database
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repository.findUserBySchoolNumber(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
        return JWTUser.build(user);
    }

    /**
     * Get all Users from the database
     *
     * @return a list of all User objects
     */
    public List<User> getAll() {
        return repository.findAll();
    }

    /**
     * Check if a school number exists in the database
     *
     * @param schoolNumber the school number to check for
     * @return true if exists else false
     */
    public boolean schoolNumberExists(String schoolNumber) {
        if(repository.findBySchoolNumber(schoolNumber)==null) {
            return false;
        }
        return true;
    }

    /**
     * Check if an email exists in the database
     *
     * @param email the email to check for
     * @return true if exists else false
     */
    public boolean emailExists(String email) {
        if(repository.findUserByEmail(email)==null) {
            return false;
        }
        return true;
    }

    /**
     * Update a given User object to the database end fill
     * in the fields not present in the update form.
     *
     * @param uuid the UUID of the User to update
     * @param user the updated USer object
     * @return the saved User object
     */
    public User update(UUID uuid, User user) {
        User userOld = getByUuid(uuid);
        userOld.setFirstName(user.getFirstName());
        userOld.setLastName(user.getLastName());
        userOld.setSchoolNumber(user.getSchoolNumber());
        userOld.setEmail(user.getEmail());
        userOld.setPhoneNumber(user.getPhoneNumber());

        userOld.setRoles(new HashSet<>());
        for (ERole role : user.geteRoles()) {
            grantRole(userOld, role);
        }

        if(user.isSchoolNumberVerified()) {
            if(userOld.getSchoolNumberVerifiedAt() == null) {
                userOld.setSchoolNumberVerifiedAt(LocalDateTime.now());
            }
        } else {
            userOld.setSchoolNumberVerifiedAt(null);
        }

        return repository.save(userOld);
    }

    /**
     * Verify the school number for a given User
     *
     * @param user the User object to verify
     */
    public void verifySchoolNumber(User user) {
        user.setSchoolNumberVerifiedAt(LocalDateTime.now());
        repository.save(user);
    }

    /**
     * Grant a given role to a given User object
     *
     * @param user the User object to grant the role to
     * @param eRole the Role to grant
     */
    public void grantRole(User user, ERole eRole) {
        Role role = roleRepository.findByName(eRole);
        if(role == null) {
            role = roleRepository.save(new Role(eRole));
        }
        user.addRole(role);
    }

    /**
     * Delete a given User (by id) from the database
     *
     * @param id the id of the User to delete
     */
    public void delete(Long id) {
        repository.delete(get(id));
    }

    /**
     * Delete a given User (by UUID) from the database
     *
     * @param uuid the id of the User to delete
     */
    public void deleteByUuid(UUID uuid) {
        delete(getByUuid(uuid).getId());
    }

    /**
     * Get all roles defined in this project.
     *
     * @return A list of Role objects
     */
    public List<Role> getRoles() {
        List<Role> roles = new ArrayList<>();
        for (ERole role : ERole.values()) {
            roles.add(new Role(role));
        }
        return roles;
    }

    /**
     * Update user password and email address
     *
     * @param credentials incoming request model with password confirmation and email
     * @return the saved user
     * @throws LoginException if the passwords don't match
     * @throws LoginException if the email is already taken
     */
    public User oauthComplete(OauthComplete credentials) throws LoginException {
        if(!credentials.getPasswordPlain().equals(credentials.getPasswordConfirmation()))
            throw new LoginException("passwords.no.match");
        User user = getBySchoolNumber(SecureUser.getSchoolNumber());
        if(credentials.getEmail() != null && emailExists(credentials.getEmail())) {
            User mailUser = getByEmail(credentials.getEmail());
            // If email is not that of current user throw error
            if(! mailUser.getSchoolNumber().equals(SecureUser.getSchoolNumber()))
                throw new LoginException("email.exists");
        }

        user.setPassword(credentials.getPasswordPlain());
        user.setEmail(credentials.getEmail());
        return repository.save(user);
    }

    /**
     * Update user password 
     *
     * @param credentials incoming request model with password confirmation and email
     * @return the saved user
     * @throws LoginException if the old password isnt correct
     */
    public User updatePassword(UpdatePassword credentials) throws LoginException {
        Argon2PasswordEncoder enc = new Argon2PasswordEncoder();

        User user = getBySchoolNumber(SecureUser.getSchoolNumber());
        if(! enc.matches(credentials.getOldPassword(), user.getPassword()))
            throw new LoginException("password_incorrect");
        return oauthComplete(new OauthComplete(user.getEmail(), credentials.getPasswordPlain(), credentials.getPasswordConfirmation()));
    }
}
