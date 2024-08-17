package com.esp.espflow.data.util;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * @author rubn
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class EsptoolBundlePath {

    /**
     * Use a bundle esptool.py
     *
     * @return A {@link String} with esptool bundle path
     */
    public static String esptoolBundlePath() {
        final String tmpDir = System.getProperty("java.io.tmpdir").concat("/esptool-bundle-dir/");
        switch (GetOsName.getOsName()) {
            case WINDOWS -> {
                return tmpDir + "esptool-winx64/esptool.exe";
            }
            case LINUX -> {
                return tmpDir + "esptool-linux-amd64/esptool";
            }
            case MAC -> {
                return tmpDir + "esptool-bundl/esptool-macOsx64/esptool";
            }
            case FREEBSD -> {
                return tmpDir + "esptool-bundle/esptool-freebsdx64/esptool";
            }
            default -> {
                return StringUtils.EMPTY;
            }
        }
    }

}
