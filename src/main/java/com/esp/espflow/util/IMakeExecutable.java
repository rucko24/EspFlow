package com.esp.espflow.util;

import com.esp.espflow.enums.GetOsName;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;
import java.util.logging.Logger;

import static com.esp.espflow.util.EspFlowConstants.SET_CHMOD_X;

/**
 * @author rubn
 */
public interface IMakeExecutable {

    Logger log = Logger.getLogger(IMakeExecutable.class.getName());

    default boolean makeExecutable(String esptoolPath) {
        boolean isExecutable = false;
        if (GetOsName.getOsName() == GetOsName.LINUX) {
            if (this.setPosixFilePermissions(Path.of(esptoolPath))) {
                log.info("esptool is executable: " + esptoolPath);
                isExecutable = true;
            } else {
                log.info("Error when setting permissions in the esptool executable " + esptoolPath);
                isExecutable = false;
            }
        }
        return isExecutable;
    }

    /**
     * Set run permissions if it is a linux or macOS executable.
     *
     * @param esptoolPath contains the path where the esptool executable is located
     */
    private boolean setPosixFilePermissions(final Path esptoolPath) {
        try {
            Set<PosixFilePermission> permissions = PosixFilePermissions.fromString(SET_CHMOD_X);
            Files.setPosixFilePermissions(esptoolPath, permissions);
            return Files.isExecutable(esptoolPath.toAbsolutePath());
        } catch (IOException ex) {
            return false;
        }
    }

}
