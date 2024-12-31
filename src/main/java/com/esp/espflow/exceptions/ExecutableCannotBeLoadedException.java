package com.esp.espflow.exceptions;

/**
 *
 * @author rubn
 */
public class ExecutableCannotBeLoadedException extends RuntimeException {

    public ExecutableCannotBeLoadedException(final String error) {
        super(error);
    }
}
