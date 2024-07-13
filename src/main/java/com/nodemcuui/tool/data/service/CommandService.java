package com.nodemcuui.tool.data.service;

import com.nodemcuui.tool.data.util.CommandNotFoundException;
import com.nodemcuui.tool.data.util.ProcessCommandsInternals;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class CommandService {

    private final ProcessCommandsInternals processCommandsInternals;

    /**
     * Invoke this method to execute commands on the console
     *
     * @param commands the array strings with commands
     * @return Flux<String>
     */
    public Flux<String> processCommands(final String... commands) {
        return processCommandsInternals.processCommands(commands);
    }

    /**
     *
     * Used for firmware reading
     *
     * @param commands
     * @return Flux<String>
     */
    public Flux<String> processCommandsWithCustomCharset(final String... commands) {
        return processCommandsInternals.processCommandsWithCustomCharset(commands);
    }
}
