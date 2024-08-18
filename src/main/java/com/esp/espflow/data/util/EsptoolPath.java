package com.esp.espflow.data.util;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import static com.esp.espflow.data.util.EspFlowConstants.ESPTOOL_BUNDLE_DIR;

/**
 * @author rubn
 */
@Log4j2
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class EsptoolPath {

    /**
     * Use a bundle esptool.py
     *
     * @return A {@link String} with esptool bundle path
     */
    public static String esptoolPath() {
        final String tmpDir = System.getProperty("java.io.tmpdir").concat(ESPTOOL_BUNDLE_DIR);
        switch (GetOsName.getOsName()) {
            case WINDOWS -> {
                return tmpDir + "esptool-winx64/esptool.exe";
            }
            case LINUX -> {
                return tmpDir + "esptool-linux-amd64/esptool";
            }
            case MAC -> {
                return tmpDir + "esptool-macos/esptool";
            }
            case FREEBSD -> {
                //No es obtenido desde el tmpdir sino desde la instalacion normal
                return "esptool.py";
            }
            default -> {
                log.debug("SO not found! {}");
                return StringUtils.EMPTY;
            }
        }
    }

}
