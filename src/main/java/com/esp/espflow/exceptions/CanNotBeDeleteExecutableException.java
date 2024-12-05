package com.esp.espflow.exceptions;

/**
 *
 * @author rubn
 */
@SuppressWarnings("unused")
public class CanNotBeDeleteExecutableException extends RuntimeException {

    public CanNotBeDeleteExecutableException(final String error) {
        super(error);
    }
}
