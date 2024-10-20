package com.esp.espflow.data.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.EnumSet;
import java.util.Locale;

import static com.esp.espflow.data.util.EspFlowConstants.*;

/**
 * Get operating system in runtime
 */
@Getter
@RequiredArgsConstructor
public enum GetOsName {

    WINDOWS("window"),
    LINUX("linux"),
    FREEBSD("freebsd"),
    MAC("mac os"),
    OTHER("other");

    final String name;

    /**
     *  Get the current shell Os name
     *
     * @return A {@link String} array
     */
    @SuppressWarnings("unused")
    public static String[] shellOsName() {
        GetOsName oS = GetOsName.getOsName();
        if (oS == GetOsName.WINDOWS) {
            return CMD_C;
        } else if (oS == GetOsName.LINUX) {
            return BIN_BASH_C;
        } else if (oS == GetOsName.FREEBSD) {
            return BIN_SH_C;
        } else if (oS == GetOsName.MAC) {
            return BIN_BASH_C;
        }
        return new String[]{GetOsName.OTHER.getName()};
    }

    /**
     *
     * Get the current OS name
     *
     * @return a {@link GetOsName}
     */
    public static GetOsName getOsName() {
        final String currentOs = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
        return EnumSet.allOf(GetOsName.class)
                .stream()
                .filter(osEnum -> currentOs.contains(osEnum.getName()))
                .findFirst()
                .orElse(OTHER);
    }

}