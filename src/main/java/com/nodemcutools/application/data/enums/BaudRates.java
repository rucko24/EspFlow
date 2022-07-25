package com.nodemcutools.application.data.enums;

import jssc.SerialPort;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author rubn
 */
@Getter
@RequiredArgsConstructor
public enum BaudRates {

    BAUD_RATE_9600(SerialPort.BAUDRATE_9600),
    BAUD_RATE_57600(SerialPort.BAUDRATE_57600),
    BAUD_RATE_74880(74880),
    BAUD_RATE_115200(SerialPort.BAUDRATE_115200),
    BAUD_RATE_230400(230400),
    BAUD_RATE_460800(460800),
    BAUD_RATE_921600(921600);

    private final int baudRate;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(baudRate);
        return sb.toString();
    }
}
