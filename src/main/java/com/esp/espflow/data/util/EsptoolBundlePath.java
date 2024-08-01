package com.esp.espflow.data.util;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Files;
import java.util.ResourceBundle;

import static com.esp.espflow.data.util.EspFlowConstants.ESPTOOL_BUNDLE;

/**
 * @author rubn
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class EsptoolBundlePath {

    /**
     *  Use a bundle esptool.py
     *
     * @return A {@link String} with esptool bundle path
     */
    public static String esptoolBundlePath() {
        switch (GetOsName.getOsName()) {
            case WINDOWS -> {
                return bundle("esptool-winx64/esptool.exe");
            }
            case LINUX -> {
                return "./esptool-bundle/esptool-linux64/esptool";
            }
            case MAC -> {
                return "./esptool-bundle/esptool-macOsx64/esptool";
            }
            case FREEBSD -> {
                return "./esptool-bundle/esptool-frebsdx64/esptool";
            }
            default -> {
                return StringUtils.EMPTY;
            }
        }
    }

    public static String bundle(final String fileName) {
        Thread.currentThread().getContextClassLoader().getResourceAsStream("a");
        ResourceBundle.getBundle("");
        return ESPTOOL_BUNDLE + fileName;
    }

}
