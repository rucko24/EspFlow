package com.esp.espflow.data.exceptions;

/**
 * Can read devices
 *
 * @author rubn
 */
public class CanNotBeReadDeviceException extends Exception {

    public CanNotBeReadDeviceException(final String error) {
        super(error);
    }
}
