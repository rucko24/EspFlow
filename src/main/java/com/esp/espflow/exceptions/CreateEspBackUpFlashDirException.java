package com.esp.espflow.exceptions;

/**
 *
 * Error trying to create firmware backup directory
 *
 * @author rubn
 */
public class CreateEspBackUpFlashDirException extends Exception {

    public CreateEspBackUpFlashDirException(final String error) {
        super(error);
    }
}
