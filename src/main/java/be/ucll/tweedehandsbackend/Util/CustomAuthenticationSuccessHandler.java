/**
 * Copyright (c) 2022
 * <p>
 * long description for the file
 *
 * @summary short description for the file
 * @author Kevin Coorens <kevin.coorens@student.be>
 * <p>
 * Created at     : 06/03/2022 13:04
 */

package be.ucll.tweedehandsbackend.Util;

import be.ucll.tweedehandsbackend.Services.UserService;
import be.ucll.tweedehandsbackend.Util.JWT.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        AuthenticationSuccessHandler.super.onAuthenticationSuccess(request, response, chain, authentication);
    }

    /**
     * When a User is succesfully logged in through UCLL OAuth 2, this function is called.
     * This sets the needed cookie for the logged in User.
     *
     * @param request the incoming HttpServletRequest
     * @param response the outgoing HttpServletResponse
     * @param authentication the set Authentication
     * @throws IOException if the JWT token can't be generated
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String jwt = jwtUtils.generateJwtToken(authentication);

        Cookie cookie = new Cookie("Auth", jwt);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
        response.sendRedirect("/auth/success");
    }
}
