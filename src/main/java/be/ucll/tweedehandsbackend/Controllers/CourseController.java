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

import be.ucll.tweedehandsbackend.DTOs.CourseDTO;
import be.ucll.tweedehandsbackend.Models.Course;
import be.ucll.tweedehandsbackend.Responses.ResponseHandler;
import be.ucll.tweedehandsbackend.Services.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseService service;

    @PostMapping("/")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<Object> store(@Valid @RequestBody Course course) {
        service.add(course);
        return ResponseHandler.generateResponse(service.getAll(), CourseDTO.class);
    }

    @GetMapping("/")
    public ResponseEntity<Object> index() {
        return ResponseHandler.generateResponse(service.getAll(), CourseDTO.class);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> show(@PathVariable UUID id) {
        return ResponseHandler.generateResponse(service.getByUuid(id), CourseDTO.class);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<Object> update(@PathVariable UUID id, @Valid @RequestBody Course course) {
        return ResponseHandler.generateResponse(service.update(id, course), CourseDTO.class);
    }

    @GetMapping("/search/{query}")
    public ResponseEntity<Object> show(@PathVariable String query) {
        System.out.println("Query: " + query);
        // TODO returns all
        return ResponseHandler.generateResponse(service.getAll(), CourseDTO.class);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<Object> delete(@PathVariable UUID id) {
        service.deleteByUuid(id);
        return ResponseHandler.generateResponse("OK");
    }

}
