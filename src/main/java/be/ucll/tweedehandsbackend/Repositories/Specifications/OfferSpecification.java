/**
 * Copyright (c) 2022
 * <p>
 * long description for the file
 *
 * @summary short description for the file
 * @author Kevin Coorens <kevin.coorens@student.be>
 * <p>
 * Created at     : 08/03/2022 22:43
 */

package be.ucll.tweedehandsbackend.Repositories.Specifications;

import be.ucll.tweedehandsbackend.Models.Offer;
import be.ucll.tweedehandsbackend.Models.User;
import org.springframework.data.jpa.domain.Specification;
import javax.persistence.criteria.*;
import java.util.*;

public class OfferSpecification {

    public static Specification<Offer> findByCriteria(final Map<String, String[]> map, User user, List<String> courseIds, boolean sold) {

        return (root, query, cb) -> {

            boolean coursesSet = map.containsKey("courses");
            boolean ownSet = map.containsKey("owned");
            boolean querySet = map.containsKey("q");
            boolean conditionSet = map.containsKey("condition");

            List<Predicate> predicates = new ArrayList<>();

            // If only own offers is enbabled match user ID
            if (ownSet && user != null) {
                predicates.add(cb.equal(root.get("user"), user));
            }
            // If a query is set search title, description and isbn for that query
            if (querySet) {
                String q = "%" + map.get("q")[0] + "%";

                if(map.containsKey("f")) {
                    String field = map.get("f")[0];
                    if(field.equals("title")) {
                        predicates.add(cb.like(cb.lower(root.get("title")), q.toLowerCase()));
                    } else if(field.equals("isbn")) {
                        predicates.add(cb.like(cb.lower(root.get("isbn")), q.toLowerCase()));
                    } else if(field.equals("description")) {
                        predicates.add(cb.like(cb.lower(root.get("description")), q.toLowerCase()));
                    }
                } else {
                    Predicate title = cb.like(cb.lower(root.get("title")), q.toLowerCase());
                    Predicate desc = cb.like(cb.lower(root.get("description")), q.toLowerCase());
                    Predicate isbn = cb.like(cb.lower(root.get("isbn")), q.toLowerCase());
                    predicates.add(cb.or(title, desc, isbn));
                }
            }
            // If any courses are set as a filter fetch all offers related to those courses and add in query
            if (coursesSet) {
                predicates.add(root.get("id").in(courseIds));
            }

            // If any product conditions are set, add them to the predicates
            if(conditionSet) {
                String condition = map.get("condition")[0];
                if (condition.equals("new")) {
                    predicates.add(cb.equal(root.get("condition"), "NEW"));
                } else if (condition.equals("as_good_as_new")) {
                    predicates.add(cb.equal(root.get("condition"), "AS_GOOD_AS_NEW"));
                } else if (condition.equals("used")) {
                    predicates.add(cb.equal(root.get("condition"), "USED"));
                }
            }

            // Get sold or non sold offers depending on the bool
            predicates.add(cb.equal(root.get("sold"), sold));

            // Sort by
            if (map.containsKey("sort")) {
                String sort = map.get("sort")[0];
                if (sort.equals("price_up")) {
                    query.orderBy(cb.asc(root.get("price")));
                } else if (sort.equals("price_down")) {
                    query.orderBy(cb.desc(root.get("price")));
                } else if (sort.equals("new_first")) {
                    query.orderBy(cb.desc(root.get("id")));
                } else if (sort.equals("old_first")) {
                    query.orderBy(cb.asc(root.get("id")));
                } else {
                    query.orderBy(cb.asc(root.get("title")));
                }
            } else {
                query.orderBy(cb.asc(root.get("title")));
            }


            return cb.and(predicates.toArray(new Predicate[] {}));
        };
    }
}
