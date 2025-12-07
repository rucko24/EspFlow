package com.esp.espflow.util;

import com.vaadin.flow.component.upload.Upload;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author rubn
 */
@Log4j2
@RequiredArgsConstructor
public class FileUploadHandler extends TransferProgressAwareHandler<UploadEvent, FileUploadHandler> implements UploadHandler {

    private final String fixedDir;
    private final Upload upload;

    @Override
    public void handleUploadRequest(UploadEvent event) {
        Mono.just(Path.of(fixedDir))
                .flatMap(this.validateAndPreparePath(event))
                .flatMap(this.transferFile(event))
                .doOnError(this.onErrorUploadFailed(event))
                .doOnTerminate(() -> log.info("Transfer file completed"))
                .subscribe();
    }

    private Function<Path, Mono<? extends Path>> validateAndPreparePath(UploadEvent event) {
        return targetDir -> this.validateAndPreparePath(targetDir, event.getFileName())
                .doOnError(error -> {
                    log.error("ValidateAndPreparePath failed: {}", error.getMessage());
                    event.getUI().access(() -> {
                        ConfirmDialogBuilder.showWarningUI("ValidateAndPreparePath failed: " + error.getMessage(), event.getUI());
                        this.upload.clearFileList(); //renable the upload button
                        //this.getElement().setPropertyJson("files", Json.createArray());
                    });
                });
    }

    private Mono<Path> validateAndPreparePath(Path targetDir, String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return Mono.error(new IllegalArgumentException("File name cannot be empty"));
        }

        if (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
            return Mono.error(new SecurityException("Invalid characters in file name: " + fileName));
        }

        if (!Files.exists(targetDir)) {
            try {
                Files.createDirectories(targetDir);
            } catch (IOException ex) {
                return Mono.error(new IOException("Failed to create directory ", ex));
            }
        }

        final Path normalizedTarget = targetDir.toAbsolutePath().normalize();
        final Path resolvedPath = normalizedTarget.resolve(fileName).normalize();

        if (!resolvedPath.startsWith(normalizedTarget)) {
            return Mono.error(new SecurityException("Path traversal attempt detected"));
        }

        return Mono.just(resolvedPath);
    }

    private Function<Path, Mono<? extends Mono<Long>>> transferFile(UploadEvent event) {
        return safeFilePath -> Mono.fromCallable(() -> this.transferFile(event, safeFilePath))
                .subscribeOn(Schedulers.boundedElastic())
                .map(Function.identity());
    }

    private Mono<Long> transferFile(UploadEvent event, Path targetPath) {
        try (final InputStream inputStream = event.getInputStream();
             final BufferedOutputStream outputStream = new BufferedOutputStream(Files.newOutputStream(targetPath))) {

            return Mono.just(TransferUtil.transfer(inputStream, outputStream, this.getTransferContext(event), super.getListeners()));

        } catch (IOException error) {
            this.notifyError(event, error);
            return Mono.error(new IOException("File transfer failed: ", error));
        }
    }

    private Consumer<Throwable> onErrorUploadFailed(UploadEvent event) {
        return error -> {
            log.error("Upload failed: {}", error.getMessage());
            event.getUI().access(() -> {
                ConfirmDialogBuilder.showWarningUI("Upload failed: " + error.getMessage(), event.getUI());
                this.upload.clearFileList(); //renable the upload button
            });
        };
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