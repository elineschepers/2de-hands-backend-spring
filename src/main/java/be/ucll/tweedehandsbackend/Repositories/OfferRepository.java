package be.ucll.tweedehandsbackend.Repositories;

import be.ucll.tweedehandsbackend.Models.Course;
import be.ucll.tweedehandsbackend.Models.Offer;
import be.ucll.tweedehandsbackend.Models.User;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface OfferRepository extends JpaRepository<Offer,Long>, JpaSpecificationExecutor {

    @Query(value = "SELECT offer_id FROM course c inner join offer_courses co on c.id = co.course_id WHERE c.uuid IN ?1", nativeQuery = true)
    List<String> getOfferIdsForCoursesUuids(List<UUID> courses);

    List<Offer> findAllByTitle(String title);
    List<Offer> findAllByPrice(double price);
    List<Offer> findAllBySold(Boolean is_sold);

    Offer findOfferById(Long id);

    Offer findOfferByUuid(UUID id);

    List<Offer> findAllBySoldOrderByTitle(boolean sold);

    @Query(value = "SELECT * FROM offer WHERE sold = false AND id IN (SELECT offer_id FROM course c inner join offer_courses co on c.id = co.course_id WHERE c.uuid IN ?1) ORDER BY title", nativeQuery = true)
    List<Offer> findOffersByCourses(List<UUID> courses);

    @Query(value = "SELECT * FROM offer WHERE sold = false AND user_id = ?1 ORDER BY title", nativeQuery = true)
    List<Offer> findOffersByUserId(Long userId);

    @Query(value = "SELECT * FROM offer WHERE sold = false AND LOWER(description) LIKE LOWER(?1) OR LOWER(title) LIKE LOWER(?1) OR LOWER(isbn) LIKE LOWER(?1) ORDER BY title", nativeQuery = true)
    List<Offer> findOffersByQuery(String query);

    @Query(value = "SELECT * FROM offer WHERE sold = false AND user_id = ?1 AND (LOWER(description) LIKE LOWER(?2) OR LOWER(title) LIKE LOWER(?2) OR LOWER(isbn) LIKE LOWER(?2)) ORDER BY title", nativeQuery = true)
    List<Offer> findOffersByUserIdAndQuery(Long id, String q);

    @Query(value = "SELECT * FROM offer WHERE sold = false AND id IN (SELECT offer_id FROM course c inner join offer_courses co on c.id = co.course_id WHERE c.uuid IN ?1) AND (LOWER(description) LIKE LOWER(?2) OR LOWER(title) LIKE LOWER(?2) OR LOWER(isbn) LIKE LOWER(?2)) ORDER BY title", nativeQuery = true)
    List<Offer> findOffersByCoursesAndQuery(List<UUID> courses, String q);

    @Query(value = "SELECT * FROM offer WHERE sold = false AND id IN (SELECT offer_id FROM course c inner join offer_courses co on c.id = co.course_id WHERE c.uuid IN ?2) AND user_id = ?1 ORDER BY title", nativeQuery = true)
    List<Offer> findOffersByUserIdAndCourses(Long id, List<UUID> courses);

    @Query(value = "SELECT * FROM offer WHERE sold = false AND id IN (SELECT offer_id FROM course c inner join offer_courses co on c.id = co.course_id WHERE c.uuid IN ?2) AND user_id = ?1 AND (LOWER(description) LIKE LOWER(?3) OR LOWER(title) LIKE LOWER(?3) OR LOWER(isbn) LIKE LOWER(?3)) ORDER BY title", nativeQuery = true)
    List<Offer> findOffersByUserIdAndCoursesAndQuery(Long id, List<UUID> courses, String q);


}
