package com.nodemcuui.tool.data.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.EnumSet;
import java.util.Locale;

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

    final String osName;

    public static GetOsName getOsInfo() {
        final String currentOs = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
        return EnumSet.allOf(GetOsName.class)
                .stream()
                .filter(osEnum -> currentOs.contains(osEnum.getOsName()))
                .findFirst()
                .orElse(OTHER);
    }

}