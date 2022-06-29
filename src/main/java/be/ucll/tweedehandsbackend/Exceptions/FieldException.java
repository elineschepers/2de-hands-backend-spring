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

import org.springframework.validation.FieldError;

public class FieldException extends Throwable {
    private String field;

    public FieldException(String field, String message) {
        super(message);
        this.setField(field);
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getField() {
        return field;
    }

    public FieldError getError() {
        return new FieldError("", field, getMessage());
    }
}
