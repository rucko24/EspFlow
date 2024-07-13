package com.nodemcuui.tool.data.util;

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

import static com.nodemcuui.tool.data.util.UiToolConstants.CHARSET_CP850;
import static com.nodemcuui.tool.data.util.UiToolConstants.COMMAND_NOT_FOUND;
import static com.nodemcuui.tool.data.util.UiToolConstants.DMESG_TTY;
import static com.nodemcuui.tool.data.util.UiToolConstants.NOT_FOUND;

@Log4j2
@Component
public final class ProcessCommandsInternals {

    public Flux<String> processCommands(final String... commands) {
        return Flux.defer(() -> this.processCommandsInternal(commands))
                .subscribeOn(Schedulers.boundedElastic())
                .transform(this::decodeDataBuffer)
                .onErrorResume(throwable -> Mono.error(new CommandNotFoundException(COMMAND_NOT_FOUND)));
    }

    private Flux<DataBuffer> processCommandsInternal(final String... commands) {
        try {
            return DataBufferUtils.readInputStream(() -> this.execute(commands).getInputStream(), DefaultDataBufferFactory.sharedInstance, FileCopyUtils.BUFFER_SIZE);
        } catch (Exception ex) {
            log.error("Error {}", ex);
            return Flux.error(ex);
        }
    }

    public Process execute(String... commands) throws IOException {
        return new ProcessBuilder()
                .command(commands)
                .redirectErrorStream(Boolean.TRUE)
                .start();
    }

    private Flux<String> decodeDataBuffer(final Flux<DataBuffer> dataBuffer) {
        return StringDecoder.allMimeTypes().decode(dataBuffer, null, null, null);
    }

    /**
     * Used for firmware reading
     *
     * @param commands
     * @return Flux<String>
     */
    public Flux<String> processCommandsWithCustomCharset(final String... commands) {
        return Flux.defer(() -> this.processCommandsInternal(commands))
                .subscribeOn(Schedulers.boundedElastic())
                .map(this::mappingDataBuffer)
                .onErrorResume(throwable -> {
                    log.error("onErrorResume: {}", throwable);
                    return Mono.error(new CommandNotFoundException(COMMAND_NOT_FOUND));
                });
    }

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
    public Flux<String> executeDmesgForTtyPort() {
        return processCommands(DMESG_TTY)
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


}
