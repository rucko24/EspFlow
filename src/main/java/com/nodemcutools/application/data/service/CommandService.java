package com.nodemcutools.application.data.service;

import com.nodemcutools.application.data.util.CommandNotFoundException;
import com.nodemcutools.application.data.util.OSInfo;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.nodemcutools.application.data.util.UiToolConstants.CHARSET_CP850;
import static com.nodemcutools.application.data.util.UiToolConstants.CMD_C;
import static com.nodemcutools.application.data.util.UiToolConstants.COMMAND_NOT_FOUND;
import static com.nodemcutools.application.data.util.UiToolConstants.DMESG_TTY;
import static com.nodemcutools.application.data.util.UiToolConstants.ESPTOOL_PY_NOT_FOUND;
import static com.nodemcutools.application.data.util.UiToolConstants.ESPTOOL_PY_VERSION;
import static com.nodemcutools.application.data.util.UiToolConstants.NOT_FOUND;
import static com.nodemcutools.application.data.util.UiToolConstants.SH_C;

/**
 * @author rubn
 */
@Service
@Log4j2
@Getter
public class CommandService {

    public String[] bash() {
        if (OSInfo.isWindows()) {
            return CMD_C;
        } else if (OSInfo.isLinux()) {
            //FIXME /bin/sh -c
            return new String[]{};
        } else if (OSInfo.isFreeBSD()) {
            //FIXME
            return new String[]{};
        } else if (OSInfo.isMac()) {
            return SH_C;
        }
        return new String[]{OSInfo.OTHER.getOsName()};
    }

    public Flux<String> esptoolVersion() {
        String[] commands = ArrayUtils.addAll(this.bash(), ESPTOOL_PY_VERSION);
        return this.processInputStream(commands)
                .map(this::processLineEsptoolVersion)
                .onErrorResume(throwable -> {
                    log.error("onErrorResume: {}", throwable);
                    return Mono.error(new CommandNotFoundException(COMMAND_NOT_FOUND));
                });
    }

    /**
     * Invoke this method to execute commands on the console
     *
     * @param commands the array strings with commands
     * @return Flux<String>
     */
    public Flux<String> processInputStream(final String... commands) {
        return Flux.defer(() -> this.readIntputStream(commands))
                .subscribeOn(Schedulers.boundedElastic())
                .map(this::mappingDataBuffer);
    }

    public Process execute(String... commands) throws IOException {
        return new ProcessBuilder()
                .command(commands)
                .redirectErrorStream(Boolean.TRUE)
                .start();
    }

    private Flux<DataBuffer> readIntputStream(final String... commands) {
        try {
            return DataBufferUtils.readInputStream(() ->
                            this.execute(commands)
                                    .getInputStream(),
                    new DefaultDataBufferFactory(), FileCopyUtils.BUFFER_SIZE);
        } catch (Exception ex) {
            log.error("Error {}", ex);
            return Flux.error(ex);
        }
    }

    private String mappingDataBuffer(final DataBuffer dataBuffer) {
        final byte[] bytes = new byte[dataBuffer.readableByteCount()];
        dataBuffer.read(bytes);
        DataBufferUtils.release(dataBuffer);
        if (OSInfo.isWindows()) {
            try {
                return new String(bytes, CHARSET_CP850);
            } catch (UnsupportedEncodingException e) {
                return StringUtils.EMPTY;
            }
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private String processLineEsptoolVersion(final String rawLine) {
        if (!(rawLine.contains("esptool.py v"))) {
            return ESPTOOL_PY_NOT_FOUND;
        }
        return rawLine.split(System.lineSeparator())[0];
    }

    public Flux<String> executeDmesgForTtyPort() {
        return processInputStream(DMESG_TTY)
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
