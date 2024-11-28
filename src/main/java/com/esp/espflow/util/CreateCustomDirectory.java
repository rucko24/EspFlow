package com.esp.espflow.util;

import com.vaadin.flow.component.upload.receivers.FileBuffer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

/**
 * @author rubn
 */
public interface CreateCustomDirectory {

    Logger log = Logger.getLogger(CreateCustomDirectory.class.getName());

    /**
     *
     * @param buffer the FileBuffer with inputStream
     * @param fixedDir, tmp dir or user.home
     * @param fileName the filename
     */
    default void createCustomDirectory(FileBuffer buffer, String fixedDir, String fileName) {
        final var flashesDir = Path.of(fixedDir);
        if (!Files.exists(flashesDir)) {
            try {
                Files.createDirectory(flashesDir);
            } catch (IOException ex) {
                log.info(() -> "Error when creating temporary directory " + flashesDir + " " + ex.getMessage());
            }
        }
        // Get information about the uploaded file
        final Path fileNameResult = flashesDir.resolve(Path.of(fileName));
        try (var input = new BufferedInputStream(buffer.getInputStream());
             var outPut = new BufferedOutputStream(Files.newOutputStream(fileNameResult))) {

            input.transferTo(outPut);

        } catch (IOException ex) {
            log.info("Error when writing flash to temporary directory " + flashesDir + " " + ex.getMessage());
        }
    }
}
