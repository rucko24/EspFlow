package com.esp.espflow.data.configuration;

import com.esp.espflow.data.util.EsptoolBundlePath;
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
import java.util.Set;

import static com.esp.espflow.data.util.EspFlowConstants.ESPTOOL_BUNDLE_DIR;
import static com.esp.espflow.data.util.EspFlowConstants.SET_CHMOD_X;
import static com.esp.espflow.data.util.EspFlowConstants.SLASH;

@Log4j2
@Configuration
public class EsptoolLoadBundleConfiguration {

    /**
     *
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
                case FREEBSD -> this.moveBundleToTempDirectory("esptool-freebsd-amd64/esptool");
                case MAC -> this.moveBundleToTempDirectory("esptool-macOsx64/esptool");
                default -> {
                    //Do nothing
                }
            }
        };
    }

    private void moveBundleToTempDirectory(final String bundleFileName) {

        final String tempDir = System.getProperty("java.io.tmpdir")
                .concat("/esptool-bundle-dir/")
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

        var esptoolFileNameOutput = Path.of(ESPTOOL_BUNDLE_DIR + bundleFileName).getFileName().toString();
        var outPathFileName = Path.of(tempDir + File.separator + esptoolFileNameOutput);

        try (var inputStream = EsptoolBundlePath.class.getResourceAsStream(ESPTOOL_BUNDLE_DIR + bundleFileName);
             var bufferedOutputStream = new BufferedOutputStream(Files.newOutputStream(outPathFileName))) {

            assert inputStream != null;
            inputStream.transferTo(bufferedOutputStream);

        } catch (IOException ex) {
            log.error("Error by copying the esptool executable to the temporary directory {}", ex.getMessage());
        }

        final var os = GetOsName.getOsName();

        if(os == GetOsName.LINUX || os == GetOsName.FREEBSD || os == GetOsName.MAC) {
            this.makeTheBundleExecutable(outPathFileName);
        }

    }

    private void makeTheBundleExecutable(final Path esptoolPath) {
        try {
            Set<PosixFilePermission> permissions = PosixFilePermissions.fromString(SET_CHMOD_X);
            Files.setPosixFilePermissions(esptoolPath, permissions);

            if(Files.isExecutable(esptoolPath.toAbsolutePath())) {
                log.debug("esptool bundle is executable");
            } else {
                log.error("Error when setting permissions in the esptool executable {}", esptoolPath);
            }

        } catch (IOException ex) {
            log.error("Error makeTheBundleExecutable() {}", ex.getMessage());
        }
    }

}
