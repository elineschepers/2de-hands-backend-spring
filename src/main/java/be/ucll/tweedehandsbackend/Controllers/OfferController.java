package be.ucll.tweedehandsbackend.Controllers;

import be.ucll.tweedehandsbackend.DTOs.OfferAuthDTO;
import be.ucll.tweedehandsbackend.DTOs.OfferDTO;
import be.ucll.tweedehandsbackend.Models.Offer;
import be.ucll.tweedehandsbackend.Responses.ResponseHandler;
import be.ucll.tweedehandsbackend.Services.OfferService;
import be.ucll.tweedehandsbackend.Util.SecureUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/offers")
public class OfferController {

    @Autowired
    private OfferService service;

    @PostMapping("/")
    public ResponseEntity<Object> store(@Valid @RequestBody Offer offer) {
        return ResponseHandler.generateResponse(service.add(offer), OfferDTO.class);
    }

    @GetMapping("/")
    public ResponseEntity<Object> index(HttpServletRequest request) {
        return ResponseHandler.generateResponse(service.getAll(request.getParameterMap(), false), OfferDTO.class);
    }

    @GetMapping("/sold/")
    public ResponseEntity<Object> indexSold(HttpServletRequest request) {
        return ResponseHandler.generateResponse(service.getAll(request.getParameterMap(), true), OfferDTO.class);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> show(HttpServletRequest request, @PathVariable UUID id) {
        if(SecureUser.isVerified()) {
            return ResponseHandler.generateResponse(service.getByUuid(id), OfferAuthDTO.class);
        }
        return ResponseHandler.generateResponse(service.getByUuid(id), OfferDTO.class);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable UUID id, @Valid @RequestBody Offer offer) throws LoginException {
        return ResponseHandler.generateResponse(service.update(id, offer), OfferDTO.class);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable UUID id) throws LoginException {
        service.deleteByUuid(id);
        return ResponseHandler.generateResponse("OK");
    }

    @PostMapping("/{id}/sold/")
    public ResponseEntity<Object> sold(@PathVariable UUID id) throws LoginException {
        service.setSold(id);
        return ResponseHandler.generateResponse("OK");
    }

}
