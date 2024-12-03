package com.esp.espflow.service;

import com.esp.espflow.entity.dto.EsptoolExecutableDto;
import com.esp.espflow.enums.GetOsName;
import com.esp.espflow.service.respository.impl.EsptoolExecutableServiceImpl;
import com.esp.espflow.util.IMakeExecutable;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import static com.esp.espflow.util.EspFlowConstants.ESPTOOL_BUNDLE_DIR;
import static com.esp.espflow.util.EspFlowConstants.JAVA_IO_TEMPORAL_DIR_OS;

/**
 * @author rubn
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class EsptoolPathService implements IMakeExecutable {

    private final EsptoolExecutableServiceImpl esptoolExecutableServiceImpl;
    private String esptoolPath = StringUtils.EMPTY;

    /**
     * Use a bundle esptool.py
     *
     * @return A {@link String} with esptool bundle path
     */
    public String esptoolPath() {

        this.esptoolExecutableServiceImpl.findByIsSelectedToTrue()
                .ifPresentOrElse(esptoolBundleDto -> {
                    if (esptoolBundleDto.isBundled()) {
                        this.esptoolPath = this.bundlePath();
                        log.info("Loaded esptoolPath() esptool.py bundled {}", esptoolPath);
                    } else {
                        this.esptoolPath = esptoolBundleDto.absolutePathEsptool();
                        log.info("Loaded esptoolPath() custom esptool.py from {}", esptoolBundleDto.absolutePathEsptool());
                    }
                }, () -> {
                    this.esptoolPath = this.bundlePath();
                    log.info("Entity is not present, Loaded esptool.py bundle {}", esptoolPath);
                });

        return esptoolPath;
    }

    /**
     *
     * @param esptoolExecutableDto bundled or custom executable esptool
     *
     * @return A {@link String} with esptool bundle path
     *
     */
    public String esptoolPath(EsptoolExecutableDto esptoolExecutableDto) {

        final String absolutePath = esptoolExecutableDto.absolutePathEsptool();
        final boolean isbundled = esptoolExecutableDto.isBundled();
        final String esptoolVersion = esptoolExecutableDto.esptoolVersion();

        this.esptoolExecutableServiceImpl.findByAbsolutePathEsptoolAndIsBundleAndVersion(absolutePath, isbundled, esptoolVersion)
                .ifPresentOrElse(esptoolBundleDto -> {
                    if (esptoolBundleDto.isBundled()) {
                        this.esptoolPath = this.bundlePath();
                        log.info("Loaded esptool.py bundled {}", esptoolPath);
                    } else {
                        this.esptoolPath = esptoolBundleDto.absolutePathEsptool();
                        log.info("Loaded custom esptool.py from {}", esptoolBundleDto.absolutePathEsptool());
                    }
                }, () -> {
                    this.esptoolPath = this.bundlePath();
                    log.info("Entity is not present, Loaded esptool.py bundle {}", esptoolPath);
                });

        return esptoolPath;
    }

    private String bundlePath() {
        this.esptoolPath = JAVA_IO_TEMPORAL_DIR_OS.concat(ESPTOOL_BUNDLE_DIR);
        switch (GetOsName.getOsName()) {
            case WINDOWS -> {
                return esptoolPath + "esptool-winx64/esptool.exe";
            }
            case LINUX -> {
                return esptoolPath + "esptool-linux-amd64/esptool";
            }
            case MAC -> {
                return "esptool.py";
            }
            case FREEBSD -> {
                //It must be installed from the FreeBSD ports.
                return "esptool.py";
            }
            default -> {
                log.info("SO not found! {}");
                return StringUtils.EMPTY;
            }
        }
    }

}
