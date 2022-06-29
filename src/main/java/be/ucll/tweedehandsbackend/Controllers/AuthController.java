/**
 * Copyright (c) 2022
 * <p>
 * long description for the file
 *
 * @summary short description for the file
 * @author Kevin Coorens <kevin.coorens@student.be>
 * <p>
 * Created at     : 04/03/2022 12:02
 */

package be.ucll.tweedehandsbackend.Controllers;


import be.ucll.tweedehandsbackend.Models.Requests.Credentials;
import be.ucll.tweedehandsbackend.Responses.ResponseHandler;
import be.ucll.tweedehandsbackend.Responses.JwtResponse;
import be.ucll.tweedehandsbackend.Util.JWT.JWTUser;
import be.ucll.tweedehandsbackend.Services.UserService;
import be.ucll.tweedehandsbackend.Util.JWT.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(HttpServletRequest request, HttpServletResponse response, @RequestBody Credentials credentials) {
        Cookie cookieSet = WebUtils.getCookie(request, "Auth");
        if(cookieSet != null && jwtUtils.validateJwtToken(cookieSet.getValue())) {
            return ResponseHandler.generateResponse("OK");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(credentials.getSchoolnumber(), credentials.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        JWTUser jwtUser = (JWTUser) authentication.getPrincipal();
        List<String> roles = jwtUser.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        Cookie cookie = new Cookie("Auth", jwt);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);

        return ResponseEntity.ok(new JwtResponse(jwt,
                jwtUser.getId(),
                jwtUser.getEmail(),
                jwtUser.getFirstName(),
                jwtUser.getLastName(),
                jwtUser.getSchoolNumber(),
                roles));


    }

    @GetMapping("/loginOauth")
    public RedirectView loginOauth() {
        // Redirect to the login page
        return new RedirectView("/api/oauth2/provider/ucll");
    }

    @PostMapping("/logout")
    public ResponseEntity<Object> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("Auth", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ResponseHandler.generateResponse("OK");
    }

    @GetMapping("/error")
    public String error(Model model) {
        model.addAttribute("error", "An error has occurred");
        return "error";
    }

    @GetMapping("/user")
    public Map<String, Object> user() {
        Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(user.getClass().equals(JWTUser.class)) {
            Map<String, Object> res = new HashMap<>();
            res.put("user", user);
            return res;
        }
        return null;
    }

}
