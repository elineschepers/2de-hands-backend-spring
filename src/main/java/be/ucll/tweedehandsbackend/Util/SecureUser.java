/**
 * Copyright (c) 2022
 * <p>
 * long description for the file
 *
 * @summary short description for the file
 * @author Kevin Coorens <kevin.coorens@student.be>
 * <p>
 * Created at     : 05/03/2022 15:15
 */

package be.ucll.tweedehandsbackend.Util;

import be.ucll.tweedehandsbackend.Models.Offer;
import be.ucll.tweedehandsbackend.Services.OfferService;
import be.ucll.tweedehandsbackend.Util.JWT.JWTUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public class SecureUser {

    @Autowired
    OfferService offerService;

    /**
     * Get the school number of the currently logged in user
     *
     * @return the school number of the logged in user
     */
    public static String getSchoolNumber() {
        Object foo = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(foo.getClass() == JWTUser.class) {
            JWTUser user = (JWTUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return user.getUsername();
        }
        return "anonymousUser";
    }

    /**
     * Get the UUID of the currently logged in user
     *
     * @return the UUID of the logged in user
     */
    public static UUID getUuid() {
        Object foo = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(foo.getClass() == JWTUser.class) {
            JWTUser user = (JWTUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return user.getId();
        }
        return null;
    }

    /**
     * Check if the currently logged in user is verified
     *
     * @return true if verified else false
     */
    public static boolean isVerified() {
        Object foo = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(foo.getClass() == JWTUser.class) {
            JWTUser user = (JWTUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return user.isVerified();
        }
        return false;
    }

    /**
     * Check if the currently logged in user has access to the given Offer
     *
     * @param offer the offer to check
     * @return true if verified else false
     */
    public static boolean hasAccesToOffer(Offer offer) {
        Object foo = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(foo.getClass() == JWTUser.class) {
            JWTUser user = (JWTUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if(user.getId().equals(offer.getUser().getUuid())) {
                return true;
            }
        }
        return false;
    }
}
