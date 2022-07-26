package com.nodemcutools.application.data.util;

/**
 * Esptool not found
 * @author rubn
 */
public class CommandNotFoundException extends Exception {

    public CommandNotFoundException(final String error) {
        super(error);
    }
}
