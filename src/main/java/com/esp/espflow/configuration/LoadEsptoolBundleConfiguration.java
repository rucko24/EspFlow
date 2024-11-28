package com.esp.espflow.configuration;

import com.esp.espflow.entity.dto.EsptoolExecutableDto;
import com.esp.espflow.service.EsptoolPathService;
import com.esp.espflow.service.EsptoolService;
import com.esp.espflow.service.respository.impl.EsptoolExecutableServiceImpl;
import com.esp.espflow.util.GetOsName;
import com.esp.espflow.util.MakeExecutable;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static com.esp.espflow.util.EspFlowConstants.ESPTOOL;
import static com.esp.espflow.util.EspFlowConstants.ESPTOOL_BUNDLE_DIR;
import static com.esp.espflow.util.EspFlowConstants.JAVA_IO_TEMPORAL_DIR_OS;
import static com.esp.espflow.util.EspFlowConstants.META_INF_RESOURCES_ESPTOOL_BUNDLE;
import static com.esp.espflow.util.EspFlowConstants.SLASH;

/**
 *
 * <p>
 * Setting to move the esptool executable depending on the operating system to the temporary directory
 * to run it from there.
 * </p>
 *
 * <p>This setting always fires when the application starts</p>
 *
 * <p>Example in linux this directory is the one that will be created:</p>
 *
 * <ul>
 *   <li>
 *        <strong>/tmp/esptool-bundle-dir/esptool-linux-amd64 </strong>
 *   </li>
 * </ul>
 *
 * <p>
 *     For FreeBSD you must install the <strong>esptool.py from the <strong>ports</strong>.
 * </p>
 *
 * @author rubn
 */
@Log4j2
@Configuration
public class LoadEsptoolBundleConfiguration implements MakeExecutable {

    /**
     * Move the esptool executable to the system's temporary directory in runtime
     *
     * @return A {@link CommandLineRunner}
     */
    @Bean
    public CommandLineRunner moveEsptoolBundleToTempDir(final EsptoolService esptoolService,
                                                        final EsptoolExecutableServiceImpl esptoolExecutableService) {
        return commands -> {
            switch (GetOsName.getOsName()) {
                case WINDOWS -> this.moveBundleToTempDirectory("esptool-winx64/esptool.exe");
                case LINUX -> {
                    final String outFileName = this.moveBundleToTempDirectory("esptool-linux-amd64/esptool");
                    esptoolService.showEsptoolVersion()
                            .subscribe(esptoolVersion -> {
                                final EsptoolExecutableDto esptoolExecutableDto = EsptoolExecutableDto.builder()
                                        .name(ESPTOOL)
                                        .esptoolVersion(esptoolVersion)
                                        .absolutePathEsptool(outFileName)
                                        .isBundle(true)
                                        .isSelected(true)
                                        .build();
                                esptoolExecutableService.save(esptoolExecutableDto);
                            });
                }
                //case MAC -> this.moveBundleToTempDirectory("esptool-macosx64/esptool.py");
                default -> {
                    //Do nothing
                }
            }
        };
    }

    /**
     * Moves the executable from the resources directory to the temporary directory
     *
     * @param bundleFileName the name of the executable, depending on the operating system
     */
    private String moveBundleToTempDirectory(final String bundleFileName) {

        final String tempDir = JAVA_IO_TEMPORAL_DIR_OS
                .concat(ESPTOOL_BUNDLE_DIR)
                .concat(bundleFileName.split(SLASH)[0]);

        final Path pathTempDir = Path.of(tempDir);

        if (!Files.exists(pathTempDir)) {
            try {
                Files.createDirectories(pathTempDir);
                log.info("Directory created successfully {}", pathTempDir.toString());
            } catch (IOException ex) {
                log.info("Error when creating the directory {}", ex.getMessage());
            }
        }

        var esptoolFileNameOutput = Path.of(META_INF_RESOURCES_ESPTOOL_BUNDLE + bundleFileName).getFileName().toString();
        var outPathFileName = Path.of(tempDir + File.separator + esptoolFileNameOutput);

        if (GetOsName.getOsName() == GetOsName.LINUX) {
            final var pathResourceAsStream = META_INF_RESOURCES_ESPTOOL_BUNDLE + bundleFileName;
            this.processResourceAsStream(pathResourceAsStream, outPathFileName);
            this.makeExecutable(outPathFileName);
        }

        return outPathFileName.toString();

    }

    /**
     * Reads and writes the resource and writes it to the temporary directory
     *
     * @param pathResourceAsStream is the input where the esptool is
     * @param outPathFileName      is the path where the esptool will be stored
     */
    private void processResourceAsStream(final String pathResourceAsStream, final Path outPathFileName) {
        try (var inputStream = EsptoolPathService.class.getResourceAsStream(pathResourceAsStream);
             var bufferedOutputStream = new BufferedOutputStream(Files.newOutputStream(outPathFileName))) {

            Objects.requireNonNull(inputStream, "inputStream is null");
            inputStream.transferTo(bufferedOutputStream);

        } catch (IOException ex) {
            log.info("Error by copying the esptool executable to the temporary directory {}", ex.getMessage());
        }
    }

}
