package com.esp.espflow.exceptions;

/**
 * command not found exception
 *
 * @author rubn
 */
public class CommandNotFoundException extends Exception {

    public CommandNotFoundException(final String error) {
        super(error);
    }
}
