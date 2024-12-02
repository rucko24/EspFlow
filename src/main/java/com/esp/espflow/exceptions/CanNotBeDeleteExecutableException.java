package com.esp.espflow.exceptions;

/**
 * Can not be read device exception
 *
 * @author rubn
 */
public class CanNotBeDeleteExecutableException extends RuntimeException {

    public CanNotBeDeleteExecutableException(final String error) {
        super(error);
    }
}
