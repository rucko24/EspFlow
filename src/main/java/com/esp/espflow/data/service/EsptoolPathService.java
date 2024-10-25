package com.esp.espflow.data.service;

import com.esp.espflow.data.util.GetOsName;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import static com.esp.espflow.data.util.EspFlowConstants.ESPTOOL_BUNDLE_DIR;
import static com.esp.espflow.data.util.EspFlowConstants.JAVA_IO_TEMPORAL_DIR_OS;

/**
 * @author rubn
 */
@Log4j2
@Service
public class EsptoolPathService {

    /**
     * Use a bundle esptool.py
     *
     * @return A {@link String} with esptool bundle path
     */
    public String esptoolPath() {
        final String tmpDir = JAVA_IO_TEMPORAL_DIR_OS.concat(ESPTOOL_BUNDLE_DIR);
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
                //It must be installed from the FreeBSD ports.
                return "esptool.py";
            }
            default -> {
                log.debug("SO not found! {}");
                return StringUtils.EMPTY;
            }
        }
    }

}
