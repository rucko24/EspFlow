package com.esp.espflow.data.util;

import com.esp.espflow.data.exceptions.CommandNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.codec.StringDecoder;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.esp.espflow.data.util.UiToolConstants.CHARSET_CP850;
import static com.esp.espflow.data.util.UiToolConstants.COMMAND_NOT_FOUND;
import static com.esp.espflow.data.util.UiToolConstants.DMESG_TTY;
import static com.esp.espflow.data.util.UiToolConstants.NOT_FOUND;

/**
 * @author rubn
 */
@Log4j2
@Component
public final class ProcessCommandsInternals {

    /**
     *
     * This processes the commands that are passed to the OS shell but in a reactive way, allowing the execution in another useful Scheduler to not block the UI, and to get feedBack with each item or line read.
     * <p>
     * If the command is not found, a CommandNotFoundException is thrown.
     *
     * @param commands a String array with commands
     *
     * @return A {@link Flux}
     */
    public Flux<String> processIntputStreamLineByLine(final String... commands) {
        return Flux.defer(() -> this.processIntputStream(commands))
                .subscribeOn(Schedulers.boundedElastic())
                .transform(this::decodeDataBuffer)
                .onErrorResume(throwable -> Mono.error(new CommandNotFoundException(COMMAND_NOT_FOUND)));
    }

    /**
     *  The spring DataBufferUtils is used to read the inputStream coming from the shell, with a buffer of 8192 kbs.
     *
     * @param commands a String array with commands
     *
     * @return A {@link Flux}
     */
    private Flux<DataBuffer> processIntputStream(final String... commands) {
        try {
            return DataBufferUtils.readInputStream(() -> this.execute(commands).getInputStream(), DefaultDataBufferFactory.sharedInstance, FileCopyUtils.BUFFER_SIZE);
        } catch (Exception ex) {
            log.error("Error to process inputstream with DataBufferUtils {}", ex.getMessage());
            return Flux.error(ex);
        }
    }

    /**
     *
     * It is public in case you use it for additional processes, such as changing port permissions.
     *
     * @param commands a String array with commands
     *
     * @return {@link Process}
     *
     * @throws IOException in case of error
     */
    public Process execute(String... commands) throws IOException {
        return new ProcessBuilder()
                .command(commands)
                .redirectErrorStream(Boolean.TRUE)
                .start();
    }

    /**
     *
     * This allows the DataBuffer to be decoded and read line by line.
     *
     * @param dataBuffer to decode
     * @return A {@link Flux}
     */
    private Flux<String> decodeDataBuffer(final Flux<DataBuffer> dataBuffer) {
        return StringDecoder.allMimeTypes().decode(dataBuffer, null, null, null);
    }

    /**
     * Used for firmware reading
     *
     * @param commands a String array with commands
     * @return A {@link Flux}
     */
    public Flux<String> processCommandsWithCustomCharset(final String... commands) {
        return Flux.defer(() -> this.processIntputStream(commands))
                .subscribeOn(Schedulers.boundedElastic())
                .map(this::mappingDataBuffer)
                .onErrorResume(throwable -> {
                    log.error("onErrorResume: {}", throwable.getMessage());
                    return Mono.error(new CommandNotFoundException(COMMAND_NOT_FOUND));
                });
    }

    /**
     *
     * This method can also read the DataBuffer but, it does not give the possibility to decode so that it can be read
     * line by line if you want to read line by line see processIntputStreamLineByLine method
     *
     * @param dataBuffer to read
     *
     * @return A {@link String}
     */
    private String mappingDataBuffer(final DataBuffer dataBuffer) {
        final byte[] bytes = new byte[dataBuffer.readableByteCount()];
        dataBuffer.read(bytes);
        DataBufferUtils.release(dataBuffer);
        if (GetOsName.getOsName() == GetOsName.WINDOWS) {
            try {
                return new String(bytes, CHARSET_CP850);
            } catch (UnsupportedEncodingException e) {
                return StringUtils.EMPTY;
            }
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    @SuppressWarnings("unused")
    private Flux<String> executeDmesgForTtyPort() {
        return processIntputStreamLineByLine(DMESG_TTY)
                .map((String line) -> line.split(System.lineSeparator()))
                .map((String[] tmp) -> Stream.of(tmp)
                        .map((String line) -> {
                            if (line.contains("tty")) {
                                return "\n".concat(line)
                                        .replace("\\s+", StringUtils.EMPTY);
                            }
                            return StringUtils.EMPTY;
                        })
                        .collect(Collectors.joining())
                ).switchIfEmpty(Mono.just(NOT_FOUND));
    }

    @SuppressWarnings("unused")
    private static int getPID() {
        String tmp = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
        tmp = tmp.split("@")[0];
        return Integer.valueOf(tmp);
    }


}