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
import be.ucll.tweedehandsbackend.Repositories.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class CourseService {

    @Autowired
    private CourseRepository repository;

    @Autowired
    private ProgramService programService;

    /**
     * Adds a give course to the database
     *
     * @param course the course to add
     * @return
     */
    public Course add(Course course) {
        for (UUID programUuid : course.getProgramsUuid()) {
            Program program = programService.getByUuid(programUuid);
            course.addProgram(program);
        }
        return repository.save(course);
    }

    /**
     * Get a course by its internal id
     *
     * @param id the id of the course to fetch
     * @return the requested course
     * @throws RecordNotFoundException if the course id does not exist
     */
    public Course get(long id) throws RecordNotFoundException {
        Course course = repository.findCourseById(id);
        if (course == null) {
            throw new RecordNotFoundException("course.notFound");
        }
        return course;
    }

    /**
     * Get a course by its uuid
     *
     * @param courseUuid the uuid of the course to fetch
     * @return the requested course
     * @throws RecordNotFoundException if the course uuid does not exist
     */
    public Course getByUuid(UUID courseUuid) {
        Course course = repository.findCourseByUuid(courseUuid);
        if (course == null) {
            throw new RecordNotFoundException("course.notFound");
        }
        return course;
    }

    /**
     * Get a list course by a list of its uuids
     *
     * @param coursesUuid the list of uuids of the courses to fetch
     * @return the requested courses
     */
    public List<Course> getByUuidIn(List<UUID> coursesUuid) {
        return repository.findCoursesByUuidIn(coursesUuid);
    }

    /**
     * Converts a string array of UUIDs to a List of UUIDs
     *
     * @param coursesUuid the array of course UUIDs
     * @return the converted list
     */
    public List<UUID> getByUuidInArray(String[] coursesUuid) {
        List<UUID> uuids = new ArrayList<>();
        for(String uuid : coursesUuid) {
            uuids.add(UUID.fromString(uuid));
        }
        return uuids;
    }

    /**
     * Returns all Courses from the database
     *
     * @return a list of courses
     */
    public List<Course> getAll() {
        return new ArrayList<>(repository.findAll());
    }

    /**
     * Delete a given course (by id) from the database and
     * detaches all programs attached to it.
     *
     * @param id the id of the course
     * @throws RecordNotFoundException if no course exists with the given id
     */
    public void delete(Long id) throws RecordNotFoundException {
        Course course = this.get(id);
        // Detach programs from this Course
        for (Program program : course.getPrograms()) {
            program.deleteCourse(course);
        }
        course.setPrograms(new ArrayList<>());
        repository.delete(course);
    }

    /**
     * Delete a given course (by UUID) from the database
     * @param uuid the UUID of the course
     */
    public void deleteByUuid(UUID uuid) {
        delete(getByUuid(uuid).getId());
    }

    /**
     * Update given course (by UUID) to the database
     * and set all fields not associated to the update form
     *
     * @param uuid the uuid of the course
     * @param course the updated course object
     * @return the saved course
     */
    public Course update(UUID uuid, Course course) {
        Course oldCourse = getByUuid(uuid);
        course.setUuid(uuid);
        course.setId(oldCourse.getId());
        for (UUID programUuid : course.getProgramsUuid()) {
            course.addProgram(programService.getByUuid(programUuid));
        }
        return repository.save(course);
    }
}
