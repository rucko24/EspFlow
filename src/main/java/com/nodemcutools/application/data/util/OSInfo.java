package com.nodemcutools.application.data.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Locale;

@Getter
@RequiredArgsConstructor
public enum OSInfo {

    WINDOWS("window"),
    LINUX("linux"),
    FREEBSD("freebsd"),
    MAC("mac os"),
    OTHER("other");

    private final String osName;
    public static Boolean osName(final OSInfo osName) {
        final String os = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
        if (os.contains(osName.getOsName())) {
            return true;
        }
        return false;
    }
    public static Boolean isWindows() {
        return OSInfo.osName(OSInfo.WINDOWS);
    }

    public static Boolean isMac() {
        return OSInfo.osName(OSInfo.MAC);
    }

    public static Boolean isLinux() {
        return OSInfo.osName(OSInfo.LINUX);
    }

    public static Boolean isFreeBSD() {
        return OSInfo.osName(OSInfo.FREEBSD);
    }

}