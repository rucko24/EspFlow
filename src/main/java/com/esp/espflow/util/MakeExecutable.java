package com.esp.espflow.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

import static com.esp.espflow.util.EspFlowConstants.SET_CHMOD_X;

/**
 * @author rubn
 */
public interface MakeExecutable {

    /**
     * Set run permissions if it is a linux or macOS executable.
     *
     * @param esptoolPath contains the path where the esptool executable is located
     */
    default boolean makeExecutable(final Path esptoolPath) {
        try {
            Set<PosixFilePermission> permissions = PosixFilePermissions.fromString(SET_CHMOD_X);
            Files.setPosixFilePermissions(esptoolPath, permissions);

            if (Files.isExecutable(esptoolPath.toAbsolutePath())) {
                //log.info("esptool bundle is executable");
                return true;
            } else {
                //log.info("Error when setting permissions in the esptool executable {}", esptoolPath);
                return false;
            }
        } catch (IOException ex) {
            //log.info("Error makeTheBundleExecutable() {}", ex.getMessage());
            return false;
        }
    }

}