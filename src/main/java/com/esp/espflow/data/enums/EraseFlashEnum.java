package com.esp.espflow.data.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author rubn
 */
@Getter
@RequiredArgsConstructor
public enum EraseFlashEnum {

    NO("no"),
    YES_WIPES_ALL_DATA("yes, wipes all data");

    private final String value;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(value);
        return sb.toString();
    }
}
