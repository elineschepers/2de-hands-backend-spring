/**
 * Copyright (c) 2022
 * <p>
 * long description for the file
 *
 * @summary short description for the file
 * @author Kevin Coorens <kevin.coorens@student.be>
 * <p>
 * Created at     : 02/03/2022 12:36
 */

package be.ucll.tweedehandsbackend.Services;

import be.ucll.tweedehandsbackend.Exceptions.RecordNotFoundException;
import be.ucll.tweedehandsbackend.Models.Course;
import be.ucll.tweedehandsbackend.Models.Program;
import be.ucll.tweedehandsbackend.Repositories.ProgramRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ProgramService {

    @Autowired
    private ProgramRepository repository;

    /**
     * Add a program Object to the database
     *
     * @param program the program to add
     * @return the saved Program object
     */
    public Program add(Program program) {
        return repository.save(program);
    }

    /**
     * Get a Program (by id) from the database
     *
     * @param id the id of the requested Program
     * @return the requested Program object
     * @throws RecordNotFoundException if no Program exists with the given id
     */
    public Program get(long id) throws RecordNotFoundException {
        Program program = repository.findProgramById(id);
        if (program == null) {
            throw new RecordNotFoundException("program.notFound");
        }
        return program;
    }

    /**
     * Get a Program (by UUID) from the database
     *
     * @param programUuid the UUID of the requested Program
     * @return the requested Program object
     */
    public Program getByUuid(UUID programUuid) {
        Program program = repository.findProgramByUuid(programUuid);
        if (program == null) {
            throw new RecordNotFoundException("program.notFound");
        }
        return program;
    }

    /**
     * Get all Programs from the database
     *
     * @return a List of Programs
     */
    public List<Program> getAll() {
        return repository.findAll();
    }

    /**
     * Update a given Program to the database and fill in
     * the fields not present in the update form.
     *
     * @param uuid the UUID of the Program to update
     * @param program the updated Program Object
     * @return the updated Program object
     */
    public Program update(UUID uuid, Program program) {
        Program programOld = getByUuid(uuid);
        program.setUuid(uuid);
        program.setId(programOld.getId());
        program.setCourses(programOld.getCourses());
        return repository.save(program);
    }

    /**
     * Delete a given Program (by id) from the database.
     * Detaches all courses attached to this program.
     *
     * @param id the id of the Program to delete
     */
    public void delete(Long id) {
        Program program = this.get(id);
        // Detach courses from this Program
        for (Course course : program.getCourses()) {
            course.deleteProgram(program);
        }

        program.setCourses(new ArrayList<>());
        repository.delete(program);
    }

    /**
     * Delete a given Program (by UUID) from the database
     *
     * @param uuid the uuid of the Program to delete
     */
    public void deleteByUuid(UUID uuid) {
        delete(this.getByUuid(uuid).getId());
    }

}
