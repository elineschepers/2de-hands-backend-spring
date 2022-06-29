package be.ucll.tweedehandsbackend.Services;

import be.ucll.tweedehandsbackend.Exceptions.RecordNotFoundException;
import be.ucll.tweedehandsbackend.Models.Course;
import be.ucll.tweedehandsbackend.Models.Offer;
import be.ucll.tweedehandsbackend.Models.User;
import be.ucll.tweedehandsbackend.Repositories.OfferRepository;
import be.ucll.tweedehandsbackend.Repositories.Specifications.OfferSpecification;
import be.ucll.tweedehandsbackend.Util.SecureUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.security.auth.login.LoginException;
import java.util.*;

@Service
public class OfferService {

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    /**
     * Add a given offer to the database
     *
     * @param offer the Offer object to add
     * @return the saved Offer object
     */
    public Offer add(Offer offer){
        User user = userService.getBySchoolNumber(SecureUser.getSchoolNumber());
        offer.setUser(user);

        if(offer.getCoursesUuid() != null) {
            List<Course> courses = courseService.getByUuidIn(offer.getCoursesUuid());
            offer.setCourses(courses);
        }

        return offerRepository.save(offer);
    }

    /**
     * Get a given Offer (by id) from the database
     *
     * @param id the id of the requested Offer
     * @return the requested Offer object
     * @throws RecordNotFoundException if the id is not present in the database
     */
    public Offer get(Long id)throws RecordNotFoundException{
        Offer offer = offerRepository.findOfferById(id);
        if(offer == null){
            throw new RecordNotFoundException("offer.not.found");
        }
        return offer;
    }

    /**
     * Get a given Offer (by UUID) from the database
     *
     * @param uuid the UUID of the requested Offer
     * @return the requested Offer object
     * @throws RecordNotFoundException if the UUID is not present in the database
     */
    public Offer getByUuid(UUID uuid) {
        Offer offer = offerRepository.findOfferByUuid(uuid);
        if (offer == null) {
            throw new RecordNotFoundException("offer.notFound");
        }
        return offer;
    }

    /**
     * Get a given Offer (by UUID as String) from the database
     *
     * @param uuid the UUID (as String) of the requested Offer
     * @return the requested Offer object
     * @throws RecordNotFoundException if the UUID is not present in the database
     */
    public Offer getByUuid(String uuid) {
        try {
            return getByUuid(UUID.fromString(uuid));
        } catch (IllegalArgumentException e) {
            throw new RecordNotFoundException("offer.notFound");
        }
    }

    /**
     * Get all Offers (not sold) from the database. Checks the request
     * Parameters map to see if any filters are set. If so apply filters
     * to result.
     *
     * @param map the Request parameters map.
     * @param sold filter by sold offers (true/false)
     * @return a List of all Offer objects taking into account the set filters
     */
    public List<Offer> getAll(Map<String, String[]> map, boolean sold){
        List<String> courseIds = new ArrayList<>();
        User user = null;

        if (!SecureUser.getSchoolNumber().equals("anonymousUser")) {
            user = userService.getBySchoolNumber(SecureUser.getSchoolNumber());
        }
        if (map.containsKey("courses")) {
            courseIds = getOfferIdsForCourses(map.get("courses"));
        }

        return offerRepository.findAll(OfferSpecification.findByCriteria(map, user, courseIds, sold));
    }

    /**
     * Update a given Offer object to the database and fill in
     * all the fields not present in the update form.
     *
     * @param uuid the UUID of the Offer to update
     * @param offer the updated Offer object
     * @return the saved Offer object
     */
    public Offer update(UUID uuid, Offer offer) throws LoginException {
        if(!SecureUser.hasAccesToOffer(getByUuid(uuid))) {
            throw new LoginException("no_access");
        }
        List<Course> courses = courseService.getByUuidIn(offer.getCoursesUuid());

        Offer offerOld = getByUuid(uuid);
        if (offerOld.isSold())
            return offerOld;
        offer.setId(offerOld.getId());
        offer.setUuid(uuid);
        offer.setUser(offerOld.getUser());
        offer.setCourses(courses);

        return offerRepository.save(offer);
    }

    /**
     * Delete a given Offer (by id) from the database
     *
     * @param id the id of the Offer to delete
     */
    public void delete(Long id) {
        offerRepository.delete(get(id));
    }

    /**
     * Set a given Offer (by UUID) as sold in the database
     * (Set it as Sold)
     *
     * @param uuid the UUID of the offer to update
     */
    public void setSold(UUID uuid) throws LoginException {
        if(!SecureUser.hasAccesToOffer(getByUuid(uuid))) {
            throw new LoginException("no_access");
        }
        Offer offer = getByUuid(uuid);
        offer.setSold(true);
        offerRepository.save(offer);
    }

    /**
     * Delete a given Offer (by UUID) from the database
     *
     * @param uuid the UUID of the offer to delete
     */
    public void deleteByUuid(UUID uuid) throws LoginException {
        Offer offer = getByUuid(uuid);
        if(!SecureUser.hasAccesToOffer(offer)) {
            throw new LoginException("no_access");
        }
        // Detach courses from this Offer
        for (Course course : offer.getCourses()) {
            course.deleteOffer(offer);
        }

        offer.setCourses(new ArrayList<>());
        offerRepository.delete(offer);
    }

    public List<String> getOfferIdsForCourses(String[] courses) {
        return offerRepository.getOfferIdsForCoursesUuids(courseService.getByUuidInArray(courses));
    }
}
