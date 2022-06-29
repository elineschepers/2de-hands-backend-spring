/**
 * Copyright (c) 2022
 * <p>
 * long description for the file
 *
 * @summary short description for the file
 * @author Kevin Coorens <kevin.coorens@student.be>
 * <p>
 * Created at     : 02/03/2022 13:16
 */

package be.ucll.tweedehandsbackend.Controllers;

import be.ucll.tweedehandsbackend.DTOs.ProgramDTO;
import be.ucll.tweedehandsbackend.Models.Program;
import be.ucll.tweedehandsbackend.Responses.ResponseHandler;
import be.ucll.tweedehandsbackend.Services.ProgramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/programs")
public class ProgramController {

    @Autowired
    ProgramService service;

    @PostMapping("/")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<Object> store(@Valid @RequestBody Program program) {
        service.add(program);
        return ResponseHandler.generateResponse(service.getAll(), ProgramDTO.class);
    }

    @GetMapping("/")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<Object> index() {
        return ResponseHandler.generateResponse(service.getAll(), ProgramDTO.class);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<Object> show(@PathVariable UUID id) {
        return ResponseHandler.generateResponse(service.getByUuid(id), ProgramDTO.class);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<Object> update(@PathVariable UUID id, @RequestBody Program program) {
        return ResponseHandler.generateResponse(service.update(id, program), ProgramDTO.class);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<Object> delete(@PathVariable UUID id) {
        service.deleteByUuid(id);
        return ResponseHandler.generateResponse("OK");
    }


}
