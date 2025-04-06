package com.esp.espflow.enums;

import com.esp.espflow.util.EspFlowConstants;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.EnumSet;
import java.util.Locale;

import static com.esp.espflow.util.EspFlowConstants.BIN_BASH_C;
import static com.esp.espflow.util.EspFlowConstants.BIN_SH_C;
import static com.esp.espflow.util.EspFlowConstants.CMD_C;

/**
 * Get operating system in runtime
 * @author rubn
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
        return EspFlowConstants.OTHER;
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