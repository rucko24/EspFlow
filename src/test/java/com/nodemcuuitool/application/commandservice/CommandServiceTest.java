package com.nodemcuuitool.application.commandservice;

import com.nodemcuui.tool.data.service.CommandService;
import com.nodemcuui.tool.data.util.CommandNotFoundException;
import com.nodemcuui.tool.data.util.ProcessCommandsInternals;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.nodemcuui.tool.data.util.UiToolConstants.ESPTOOL_PY_NOT_FOUND;
import static com.nodemcuui.tool.data.util.UiToolConstants.NOT_FOUND;

/**
 * @author rubn
 */
@Log4j2
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {CommandService.class, ProcessCommandsInternals.class})
class CommandServiceTest {

    @Autowired
    private CommandService commandService;

//    @Mock
//    private ProcessCommandsInternals processCommandsInternals;

    @Test
    @DisplayName("Expect esptool.py: not found!")
    void esptoolNotFound() {

//        commandService.processCommands()
//        Mockito.when(this.processCommandsInternals.processCommands("esptool.py")).thenReturn(Flux.just(ESPTOOL_PY_NOT_FOUND));
//
//        StepVerifier.create(this.commandService.processCommands("esptool.py"))
//                .expectNextMatches(e -> e.contains(ESPTOOL_PY_NOT_FOUND))
//                .verifyComplete();
    }

//    @Test
//    @DisplayName("esptool.py v4+")
//    void esptoolVersionV() {

//        Mockito.when(this.commandService.esptoolVersion()).thenReturn(Flux.just("esptool.py v4.3"));
//
//        StepVerifier.create(this.commandService.esptoolVersion())
//                .expectNextMatches((String esp) -> esp.contains("esptool.py v"))
//                .verifyComplete();
//    }

//    @Test
//    @DisplayName("the output should be: not found! with wrong parameter")
//    @SneakyThrows
//    void handleErrorWithWrongCommand() {
//
//        Mockito.when(this.commandService.processCommands("esptoo.pys", "version"))
//                .thenReturn(Flux.just(NOT_FOUND));
//
//        Flux<String> processInput = this.commandService.processCommands("esptoo.pys", "version")
//                .onErrorResume(throwable -> Mono.error(new CommandNotFoundException(NOT_FOUND)));
//
//        StepVerifier.create(processInput)
//                .expectNextMatches(s -> s.contains(NOT_FOUND))
//                .verifyComplete();
//    }

}
