/**
 * Copyright (c) 2022
 * <p>
 * long description for the file
 *
 * @summary short description for the file
 * @author Kevin Coorens <kevin.coorens@student.be>
 * <p>
 * Created at     : 04/03/2022 23:39
 */

package be.ucll.tweedehandsbackend.Util.JWT;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

import be.ucll.tweedehandsbackend.Exceptions.RecordNotFoundException;
import be.ucll.tweedehandsbackend.Models.User;
import be.ucll.tweedehandsbackend.Services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;

import javax.security.auth.login.LoginException;

@Component
public class JwtUtils {

    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${tweedehands.app.jwtSecret}")
    private String jwtSecret;

    @Value("${tweedehands.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    /**
     * Generates a JWT token for a given Authentication object.
     * Principal can either be a UCLL Oauth2 User or an internal
     * JWT user.
     *
     * @param authentication the given Authentication object
     * @return a JWT token for the given Authentication object
     */
    public String generateJwtToken(Authentication authentication) {
        String username;
        if (authentication.getPrincipal().getClass() == JWTUser.class) {
            JWTUser userPrincipal = (JWTUser) authentication.getPrincipal();
            username = userPrincipal.getUsername();
        } else {
            DefaultOAuth2User userPrincipal = (DefaultOAuth2User) authentication.getPrincipal();
            User user;
            try {
                user =  userService.getBySchoolNumber(userPrincipal.getName());
                user.setSchoolEmail((String) userPrincipal.getAttributes().get("personEmail"));
                userService.verifySchoolNumber(user);
            } catch (RecordNotFoundException e) {
                Map<String, Object> userMap = userPrincipal.getAttributes();
                try {
                    user = userService.add(new User((String)userMap.get("accountId"), (String)userMap.get("lastName"), (String)userMap.get("firstName"), (String)userMap.get("personEmail")));
                } catch (LoginException ex) {
                   return null;
                }
            }
            username = user.getSchoolNumber();
        }

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    /**
     * Returns the username set in a given JWT token
     *
     * @param token the JWT token
     * @return the embedded username
     */
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * Validates a given JWT token
     *
     * @param authToken the given JWT token
     * @return true if valid else false
     */
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}
