package be.ucll.tweedehandsbackend.Controllers;

import be.ucll.tweedehandsbackend.DTOs.RoleDTO;
import be.ucll.tweedehandsbackend.DTOs.UserDTO;
import be.ucll.tweedehandsbackend.Models.Requests.Credentials;
import be.ucll.tweedehandsbackend.Models.Requests.OauthComplete;
import be.ucll.tweedehandsbackend.Models.Requests.UpdatePassword;
import be.ucll.tweedehandsbackend.Models.User;
import be.ucll.tweedehandsbackend.Responses.ResponseHandler;
import be.ucll.tweedehandsbackend.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.LoginException;
import javax.validation.Valid;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService service;

    @PostMapping("/")
    public ResponseEntity<Object> store(@Valid @RequestBody User user) throws LoginException {
        return ResponseHandler.generateResponse(service.addFromForm(user), UserDTO.class);
    }

    @PostMapping("/updatepassword/")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN') or hasRole('USER')")
    public  ResponseEntity<Object> updatePassword(@Valid @RequestBody UpdatePassword credentials) throws LoginException {
        return ResponseHandler.generateResponse(service.updatePassword(credentials), UserDTO.class);
    }

    @PostMapping("/oauthcomplete/")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN') or hasRole('USER')")
    public  ResponseEntity<Object> updatePassword(@Valid @RequestBody OauthComplete credentials) throws LoginException {
        return ResponseHandler.generateResponse(service.oauthComplete(credentials), UserDTO.class);
    }

    @GetMapping("/")
    public ResponseEntity<Object> index() {
        return ResponseHandler.generateResponse(service.getAll(), UserDTO.class);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> show(@PathVariable UUID id) {
        return ResponseHandler.generateResponse(service.getByUuid(id), UserDTO.class);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> update(@PathVariable UUID id, @Valid @RequestBody User user) {
        return ResponseHandler.generateResponse(service.update(id, user), UserDTO.class);
    }

    @GetMapping("/roles/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> getRoles() {
        return ResponseHandler.generateResponse(service.getRoles(), RoleDTO.class);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<Object> delete(@PathVariable UUID id) {
        service.deleteByUuid(id);
        return ResponseHandler.generateResponse("OK");
    }

}

