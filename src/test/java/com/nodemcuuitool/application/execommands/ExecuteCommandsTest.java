package com.nodemcuuitool.application.execommands;

import com.nodemcuui.tool.data.service.ComPortService;
import com.nodemcuui.tool.data.service.CommandService;
import com.nodemcuui.tool.data.util.GetOsName;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.codec.StringDecoder;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static com.nodemcuui.tool.data.util.UiToolConstants.ESPTOOL_PY_VERSION;

/**
 * @author rubn
 */
@Log4j2
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ComPortService.class, CommandService.class})
class ExecuteCommandsTest {

    private static final String IFCONFIG = "ifconfig";
    private static final String IPCONFIG = "ipconfig";

    @Autowired
    private CommandService commandService;

    @Test
    @DisplayName("Put command to first line")
    void insertCommandToFirstPositionInProcessStream() {
        this.commandService.processCommands("ifconfig")
                .subscribe((String line) -> {
                    final String rLine = new StringBuilder(line).insert(0, "nueva linea \n").toString();
                    log.info("Result {}", rLine);
                });
    }

    @Test
    @SneakyThrows
    @DisplayName("esptool version")
    void esptoolVersion() {

        String[] commands = ArrayUtils.addAll(new String[]{"/bin/sh", "-c"}, ESPTOOL_PY_VERSION);

        StepVerifier.create(this.commandService.processCommands(commands)
                        .take(1))
                .expectNextMatches(line -> line.contains("esptool.py v"))
                .verifyComplete();


    }

//    private Flux<DataBuffer> readIntputStream(final String commands) {
//        try {
//            return DataBufferUtils.readInputStream(() ->
//                            this.commandService.processCommands(commands)
//                                    .getInputStream(),
//                    new DefaultDataBufferFactory(), FileCopyUtils.BUFFER_SIZE);
//        } catch (Exception ex) {
//            log.error("Error {}", ex);
//            return Flux.error(ex);
//        }
//    }


    public String getIpAddressInfo() {
        return GetOsName.getOsName() == GetOsName.WINDOWS ? IPCONFIG : IFCONFIG;
    }


    @Test
    @DisplayName("Handle error")
    @SneakyThrows
    void handleError() {
//        final CountDownLatch count = new CountDownLatch(1);
//        Flux.defer(() -> this.readIntputStream("esptool.py v"))
//                .subscribeOn(Schedulers.boundedElastic())
//                .transformDeferred(this::stringDecoder)
//                .onErrorResume(throwable -> Mono.error(new CommandNotFoundException(NOT_FOUND)))
//                .doOnTerminate(count::countDown)
//                .doOnError(line -> log.info("doOnError: {}", line))
//                .subscribe(line -> log.info("Subscribe: {}", line));
//        count.await();
    }

    @Test
    @DisplayName("Leyendo line a linea un inputStream decodificado para poder leer bien, sin mapping extraños")
    void testRead() {
//
//        final var countDownLatch = new CountDownLatch(1);
//        DataBufferUtils.readInputStream(() -> this.commandService.processCommands(getIpAddressInfo())
//                        .getInputStream(), DefaultDataBufferFactory.sharedInstance, FileCopyUtils.BUFFER_SIZE)
//                .transformDeferred(this::stringDecoder)
//                .delayElements(Duration.ofSeconds(1))
//                .log()
//                .doOnTerminate(countDownLatch::countDown)
//                .subscribe();
//
//        try {
//            countDownLatch.await();
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
    }

    private Flux<String> stringDecoder(Flux<DataBuffer> dataBuffer) {
        return StringDecoder.allMimeTypes().decode(dataBuffer, null, null, null);
    }

}
