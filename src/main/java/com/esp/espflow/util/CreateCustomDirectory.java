package com.esp.espflow.util;

import com.esp.espflow.exceptions.ExecutableCannotBeLoadedException;
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.util.FileCopyUtils;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CountDownLatch;
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
        final var targetDir = Path.of(fixedDir);
        if (!Files.exists(targetDir)) {
            try {
                Files.createDirectories(targetDir);
            } catch (IOException ex) {
                log.info(() -> "Error when creating directory " + targetDir + " " + ex.getMessage());
            }
        }
        final Path fileNameResult = targetDir.resolve(Path.of(fileName));
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        DataBufferUtils.readInputStream(buffer::getInputStream, DefaultDataBufferFactory.sharedInstance, FileCopyUtils.BUFFER_SIZE)
                .subscribeOn(Schedulers.boundedElastic())
                .doOnError(error -> log.info("doOnError: {}" + error.getMessage()))
                .onErrorResume(throwable ->
                        Mono.error(new RuntimeException("Error when writing flash to temporary directory " + targetDir + " " + throwable.getMessage())))
                .as(dataBuffer -> DataBufferUtils.write(dataBuffer, fileNameResult, StandardOpenOption.CREATE)
                        .doOnError(error -> log.info("doOnError: {}" + error.getMessage())))
                .doOnTerminate(() -> {
                    log.info("Writed executable successfully on " + fileNameResult);
                    countDownLatch.countDown();
                })
                .subscribe();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ExecutableCannotBeLoadedException(e.getMessage());
        }
    }

}
