package com.esp.espflow.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * - https://docs.espressif.com/projects/esptool/en/latest/esp32/esptool/basic-commands.html#erasing-flash-before-write
 *
 * @author rubn
 */
@Getter
@RequiredArgsConstructor
public enum EraseFlashEnum {

    NO(""),
    YES_WIPES_ALL_DATA("--erase-all");

    private final String value;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(value);
        return sb.toString();
    }

    /**
     *
     * @param eraseFlashEnum
     *
     * @return A {@link String}
     */
    public static String getDescriptionForEraseFlash(EraseFlashEnum eraseFlashEnum) {
        switch (eraseFlashEnum) {
            case NO -> {
                return "no";
            }
            case YES_WIPES_ALL_DATA -> {
                return "yes, wipes all data";
            }
            default -> {
                return "";
            }
        }
    }

}
