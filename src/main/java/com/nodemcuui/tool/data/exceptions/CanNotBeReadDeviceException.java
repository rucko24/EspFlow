package com.nodemcuui.tool.data.exceptions;

/**
 * Can read device with esptool
 *
 * @author rubn
 */
public class CanNotBeReadDeviceException extends Exception {

    public CanNotBeReadDeviceException(final String error) {
        super(error);
    }
}
