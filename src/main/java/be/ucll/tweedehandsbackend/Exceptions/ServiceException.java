/**
 * Copyright (c) 2022
 * <p>
 * long description for the file
 *
 * @summary short description for the file
 * @author Kevin Coorens <kevin.coorens@student.be>
 * <p>
 * Created at     : 02/03/2022 12:43
 */

package be.ucll.tweedehandsbackend.Exceptions;

public class ServiceException extends Throwable {
    public ServiceException(String message) {
        super(message);
    }
}
