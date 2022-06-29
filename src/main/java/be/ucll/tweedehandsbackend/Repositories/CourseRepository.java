package be.ucll.tweedehandsbackend.Repositories;

import be.ucll.tweedehandsbackend.Models.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CourseRepository extends JpaRepository<Course, Long> {

    Course findCourseById(long id);

    Course findCourseByUuid(UUID courseUuid);

    List<Course> findCoursesByUuidIn(List<UUID> coursesUuid);
}
