package com.esp.espflow.exceptions;

/**
 * @author rubn
 */
public class CanNotComputeSha256Exception extends RuntimeException{

    public CanNotComputeSha256Exception(final String error) {
        super(error);
    }
}
