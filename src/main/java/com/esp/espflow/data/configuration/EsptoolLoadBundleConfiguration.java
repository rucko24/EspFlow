package com.esp.espflow.data.configuration;

import com.esp.espflow.data.util.EsptoolPath;
import com.esp.espflow.data.util.GetOsName;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Objects;
import java.util.Set;

import static com.esp.espflow.data.util.EspFlowConstants.*;

/**
 * <p>
 *  Setting to move the esptool executable depending on the operating system to the temporary directory
 *   to run it from there.
 * </p>
 *
 * <p>Example in linux this directory is the one that will be created:</p>
 *
 * <ul>
 *
 *   <li>
 *        <strong>/tmp/esptool-bundle-dir/esptool-linux-amd64 </strong>
 *   </li>
 * </ul>
 *
 * <p>
 *     Para FreeBSD se debe instalar el <strong>esptool.py desde los ports </strong>
 * </p>
 *
 *
 * @author rubn
 */
@Log4j2
@Configuration
public class EsptoolLoadBundleConfiguration {

    /**
     * Move the esptool executable to the system's temporary directory in runtime
     *
     * @return A {@link CommandLineRunner}
     */
    @Bean
    public CommandLineRunner moveEsptoolBundleToTempDir() {
        return commands -> {
            switch (GetOsName.getOsName()) {
                case WINDOWS -> this.moveBundleToTempDirectory("esptool-winx64/esptool.exe");
                case LINUX -> this.moveBundleToTempDirectory("esptool-linux-amd64/esptool");
                case MAC -> this.moveBundleToTempDirectory("esptool-macosx64/esptool");
                default -> {
                    //Do nothing
                }
            }
        };
    }

    /**
     * Moves the executable from the resources directory to the temporary directory
     *
     * @param bundleFileName the name of the executable, depending on the operating systemº
     */
    private void moveBundleToTempDirectory(final String bundleFileName) {

        final String tempDir = JAVA_IO_TEMPORAL_DIR_OS
                .concat(ESPTOOL_BUNDLE_DIR)
                .concat(bundleFileName.split(SLASH)[0]);

        final Path pathTempDir = Path.of(tempDir);

        if (!Files.exists(pathTempDir)) {
            try {
                Files.createDirectories(pathTempDir);
                log.debug("Directory created successfully {}", pathTempDir.toString());
            } catch (IOException ex) {
                log.error("Error when creating the directory {}", ex.getMessage());
            }
        }

        var esptoolFileNameOutput = Path.of(META_INF_RESOURCES_ESPTOOL_BUNDLE + bundleFileName).getFileName().toString();
        var outPathFileName = Path.of(tempDir + File.separator + esptoolFileNameOutput);

        final var pathResourceAsStream = META_INF_RESOURCES_ESPTOOL_BUNDLE + bundleFileName;

        this.processResourceAsStream(pathResourceAsStream, outPathFileName);

        final var os = GetOsName.getOsName();

        if (os == GetOsName.LINUX || os == GetOsName.MAC) {
            this.makeTheBundleExecutable(outPathFileName);
        }

    }

    /**
     * Reads and writes the resource and writes it to the temporary directory
     *
     * @param pathResourceAsStream is the input where the esptool is
     * @param outPathFileName is the path where the esptool will be stored
     */
    private void processResourceAsStream(final String pathResourceAsStream, final Path outPathFileName) {
        try (var inputStream = EsptoolPath.class.getResourceAsStream(pathResourceAsStream);
             var bufferedOutputStream = new BufferedOutputStream(Files.newOutputStream(outPathFileName))) {

            Objects.requireNonNull(inputStream, "inputStream is null");
            inputStream.transferTo(bufferedOutputStream);

        } catch (IOException ex) {
            log.error("Error by copying the esptool executable to the temporary directory {}", ex.getMessage());
        }
    }

    /**
     * Set run permissions if it is a linux or macOS executable.
     *
     * @param esptoolPath contains the path where the esptool executable is located
     */
    private void makeTheBundleExecutable(final Path esptoolPath) {
        try {
            Set<PosixFilePermission> permissions = PosixFilePermissions.fromString(SET_CHMOD_X);
            Files.setPosixFilePermissions(esptoolPath, permissions);

            if (Files.isExecutable(esptoolPath.toAbsolutePath())) {
                log.debug("esptool bundle is executable");
            } else {
                log.error("Error when setting permissions in the esptool executable {}", esptoolPath);
            }

        } catch (IOException ex) {
            log.error("Error makeTheBundleExecutable() {}", ex.getMessage());
        }
    }

}
