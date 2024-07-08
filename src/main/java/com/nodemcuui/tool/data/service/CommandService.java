package com.nodemcuui.tool.data.service;

import com.nodemcuui.tool.data.util.CommandNotFoundException;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.codec.StringDecoder;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.nodemcuui.tool.data.util.UiToolConstants.COMMAND_NOT_FOUND;
import static com.nodemcuui.tool.data.util.UiToolConstants.DMESG_TTY;
import static com.nodemcuui.tool.data.util.UiToolConstants.NOT_FOUND;


/**
 * @author rubn
 */
@Service
@Log4j2
@Getter
public class CommandService {

    /**
     * Invoke this method to execute commands on the console
     *
     * @param commands the array strings with commands
     * @return Flux<String>
     */
    public Flux<String> processCommands(final String... commands) {
        return Flux.defer(() -> this.processCommandsInternal(commands))
                .subscribeOn(Schedulers.boundedElastic())
                .transformDeferred(this::decodedDataBuffer)
                .onErrorResume(throwable -> Mono.error(new CommandNotFoundException(COMMAND_NOT_FOUND)));
    }

    private Flux<DataBuffer> processCommandsInternal(final String... commands) {
        try {
            return DataBufferUtils.readInputStream(() -> this.execute(commands).getInputStream(), new DefaultDataBufferFactory(), FileCopyUtils.BUFFER_SIZE);
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

    private Flux<String> decodedDataBuffer(final Flux<DataBuffer> dataBuffer) {
        return StringDecoder.allMimeTypes().decode(dataBuffer, null, null, null);
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
