package com.esp.espflow.util;

import com.vaadin.flow.server.streams.TransferContext;
import com.vaadin.flow.server.streams.TransferProgressAwareHandler;
import com.vaadin.flow.server.streams.TransferUtil;
import com.vaadin.flow.server.streams.UploadEvent;
import com.vaadin.flow.server.streams.UploadHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author rubn
 */
@Log4j2
@RequiredArgsConstructor
public class FlashUploadHandler extends TransferProgressAwareHandler<UploadEvent, FlashUploadHandler> implements UploadHandler {

    private final String fixedDir;

    @Override
    public void handleUploadRequest(UploadEvent event) {
        final Path targetDir = Path.of(fixedDir);
        final Path safeFilePath = this.validateAndPreparePath(targetDir, event.getFileName());

        this.transferFileAsync(event, safeFilePath)
                .subscribeOn(Schedulers.boundedElastic())
                .doOnError(error -> {
                    log.error(() -> "Upload failed: " + error.getMessage());
                    event.getUI().access(() -> ConfirmDialogBuilder.showWarningUI("Upload failed: " + error.getMessage(), event.getUI()));
                })
                .doOnSuccess(bytesTransferred -> {
                    log.info(() -> String.format("File uploaded: %s (%d bytes)",
                            safeFilePath.getFileName(), bytesTransferred));
                    event.getUI().access(() -> ConfirmDialogBuilder.showInformationUI("Upload completed: " + safeFilePath.getFileName(), event.getUI()));
                })
                .subscribe();
    }

    private Mono<Long> transferFileAsync(UploadEvent event, Path targetPath) {
        return Mono.fromCallable(() -> {
            try (final InputStream inputStream = event.getInputStream();
                 final BufferedOutputStream outputStream = new BufferedOutputStream(Files.newOutputStream(targetPath))) {

                return TransferUtil.transfer(inputStream, outputStream, getTransferContext(event), getListeners());

            } catch (IOException error) {
                this.notifyError(event, error);
                throw new UncheckedIOException("File transfer failed", error);
            }
        });
    }

    private Path validateAndPreparePath(Path targetDir, String fileName) {
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("File name cannot be empty");
        }

        if (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
            throw new SecurityException("Invalid characters in file name: " + fileName);
        }

        if (!Files.exists(targetDir)) {
            try {
                Files.createDirectories(targetDir);
            } catch (IOException ex) {
                throw new UncheckedIOException("Failed to create directory", ex);
            }
        }

        Path normalizedTarget = targetDir.toAbsolutePath().normalize();
        Path resolvedPath = normalizedTarget.resolve(fileName).normalize();

        if (!resolvedPath.startsWith(normalizedTarget)) {
            throw new SecurityException("Path traversal attempt detected");
        }

        return resolvedPath;
    }

    @Override
    protected TransferContext getTransferContext(UploadEvent event) {
        return new TransferContext(
                event.getRequest(),
                event.getResponse(),
                event.getSession(),
                event.getFileName(),
                event.getOwningElement(),
                event.getFileSize());
    }
}