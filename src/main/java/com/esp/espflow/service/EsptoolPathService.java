package com.esp.espflow.service;

import com.esp.espflow.dto.EsptoolExecutableDto;
import com.esp.espflow.enums.GetOsName;
import com.esp.espflow.service.respository.impl.EsptoolExecutableService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import static com.esp.espflow.util.EspFlowConstants.ENTITY_IS_NOT_PRESENT_LOADED_BUNDLE_ESPTOOL_PY_FROM;
import static com.esp.espflow.util.EspFlowConstants.ESPTOOL_BUNDLE_DIR;
import static com.esp.espflow.util.EspFlowConstants.JAVA_IO_TEMPORAL_DIR_OS;
import static com.esp.espflow.util.EspFlowConstants.LOADED_BUNDLED_ESPTOOL_PY_FROM;
import static com.esp.espflow.util.EspFlowConstants.LOADED_CUSTOM_ESPTOOL_PY_FROM;

/**
 * <p>The esptool path is searched whether bundle or custom</p>
 *
 * <p>If there is no version registered in db, we load the default bundle path</p>
 *
 * @author rubn
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class EsptoolPathService {

    private final EsptoolExecutableService esptoolExecutableService;
    private String esptoolPath = StringUtils.EMPTY;

    /**
     * <p>It is used to locate the esptool path, either custom or bundle, if it is marked as bundle to true or does not
     * exist in db we will use the bundle, otherwise the custom version.
     * </p>
     *
     * @return A {@link String} with esptool path, bundle or custom
     */
    public String esptoolPath() {

        this.esptoolExecutableService.findByIsSelectedToTrue()
                .ifPresentOrElse(this::isBundleOrGetAbsolutePath, this::loadedBundleEsptoolPy);

        return esptoolPath;
    }

    /**
     *
     * <p>It is used to mark the executable of the <strong>esptool.py</strong>, through these three fields, to locate more easily this executable,
     * and then save the state in db,
     * <ul>
     *     <li>
     *         Used in {@link com.esp.espflow.views.settings.SettingsEsptoolHomePathContent#initListeners()}
     *     </li>
     * </u>
     * </p>
     *
     * @param esptoolExecutableDto bundled or custom executable esptool
     *
     * @return A {@link String} with esptool path, bundle or custom
     *
     */
    public String esptoolPath(final EsptoolExecutableDto esptoolExecutableDto) {

        final String absolutePath = esptoolExecutableDto.absolutePathEsptool();
        final boolean isbundled = esptoolExecutableDto.isBundled();
        final String esptoolVersion = esptoolExecutableDto.esptoolVersion();

        this.esptoolExecutableService.findByAbsolutePathEsptoolAndIsBundleAndVersion(absolutePath, isbundled, esptoolVersion)
                .ifPresentOrElse(this::isBundleOrGetAbsolutePath, this::loadedBundleEsptoolPy);

        return esptoolPath;
    }

    private void isBundleOrGetAbsolutePath(EsptoolExecutableDto esptoolBundleDto) {
        if (esptoolBundleDto.isBundled()) {
            this.esptoolPath = this.bundlePath();
            log.debug(LOADED_BUNDLED_ESPTOOL_PY_FROM, esptoolPath);
        } else {
            this.esptoolPath = esptoolBundleDto.absolutePathEsptool();
            log.debug(LOADED_CUSTOM_ESPTOOL_PY_FROM, esptoolBundleDto.absolutePathEsptool());
        }
    }

    private void loadedBundleEsptoolPy() {
        this.esptoolPath = this.bundlePath();
        log.debug(ENTITY_IS_NOT_PRESENT_LOADED_BUNDLE_ESPTOOL_PY_FROM, esptoolPath);
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
