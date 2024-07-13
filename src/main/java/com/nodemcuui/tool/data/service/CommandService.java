package com.nodemcuui.tool.data.service;

import com.nodemcuui.tool.data.util.ProcessCommandsInternals;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;


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
     * Used for firmware reading
     *
     * @param commands
     * @return Flux<String>
     */
    public Flux<String> processCommandsWithCustomCharset(final String... commands) {
        return processCommandsInternals.processCommandsWithCustomCharset(commands);
    }
}
