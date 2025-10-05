package com.esp.espflow.util;

import com.esp.espflow.exceptions.ExecutableCannotBeLoadedException;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.util.FileCopyUtils;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

/**
 * @author rubn
 */
@Deprecated
public interface CreateCustomDirectory {

    Logger log = Logger.getLogger(CreateCustomDirectory.class.getName());

    /**
     *
     * @param inputStream the inputStream from upload event
     * @param fixedDir,   tmp dir or user.home
     * @param fileName    the filename
     */
    default void createCustomDirectory(InputStream inputStream, String fixedDir, String fileName) {
        final var targetDir = Path.of(fixedDir);
        if (!Files.exists(targetDir)) {
            try {
                Files.createDirectories(targetDir);
            } catch (IOException ex) {
                log.info(() -> "Error when creating directory " + targetDir + " " + ex.getMessage());
            }
        }

        //FIXME normalize for security ?
        final Path fileNameResult = targetDir.resolve(Path.of(fileName)).normalize();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        DataBufferUtils.readInputStream(() -> inputStream, DefaultDataBufferFactory.sharedInstance, FileCopyUtils.BUFFER_SIZE)
                .subscribeOn(Schedulers.boundedElastic())
                .doOnError(error -> log.info("doOnError: {}" + error.getMessage()))
                .onErrorResume(throwable ->
                        Mono.error(new RuntimeException("Error when writing flash to directory " + targetDir + " " + throwable.getMessage())))
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
