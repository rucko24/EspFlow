package com.esp.espflow.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author rubn
 */
@Getter
@RequiredArgsConstructor
public enum FlashMode {

    QUAD_IO("QIO"),
    Q_OUT("QOUT"),
    DUAL_IO("DIO"),
    DUAL_OUTPUT("DOUT");

    private final String flashMode;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(flashMode);
        return sb.toString();
    }
}