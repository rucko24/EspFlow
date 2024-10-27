package com.esp.espflow.exceptions;

/**
 * Esptool not found
 * @author rubn
 */
public class CommandNotFoundException extends Exception {

    public CommandNotFoundException(final String error) {
        super(error);
    }
}
