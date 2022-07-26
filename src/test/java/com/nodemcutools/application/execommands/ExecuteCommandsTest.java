package com.nodemcutools.application.execommands;


import com.nodemcutools.application.data.service.ComPortService;
import com.nodemcutools.application.data.service.CommandService;
import com.nodemcutools.application.data.util.CommandNotFoundException;
import com.nodemcutools.application.data.util.OSInfo;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.FileCopyUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;

import static com.nodemcutools.application.data.util.UiToolConstants.CHARSET_CP850;
import static com.nodemcutools.application.data.util.UiToolConstants.NOT_FOUND;

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
        this.commandService.processInputStream("ifconfig")
                .subscribe((String line) -> {
                    final String rLine = new StringBuilder(line).insert(0, "nueva linea \n").toString();
                    log.info("Result {}", rLine);
                });
    }

    @Test
    @SneakyThrows
    @DisplayName("Execute ipconfig with specific charset CP850 for windows")
    void executeWithCharset() {
        final CountDownLatch count = new CountDownLatch(1);
        Flux.defer(() -> this.readIntputStream(getIpAddressInfo()))
                .subscribeOn(Schedulers.boundedElastic())
                .map(this::mappinDataBuffer)
                .doOnTerminate(count::countDown)
                .onErrorResume(throwable -> Mono.empty())
                .switchIfEmpty(Mono.just(NOT_FOUND))
                .subscribe(log::info);
        count.await();
    }

    private Flux<DataBuffer> readIntputStream(final String commands) {
        try {
            return DataBufferUtils.readInputStream(() ->
                            this.commandService.execute(commands)
                                    .getInputStream(),
                    new DefaultDataBufferFactory(), FileCopyUtils.BUFFER_SIZE);
        } catch (Exception ex) {
            log.error("Error {}", ex);
            return Flux.error(ex);
        }
    }

    private String mappinDataBuffer(final DataBuffer dataBuffer) {
        final byte[] bytes = new byte[dataBuffer.readableByteCount()];
        dataBuffer.read(bytes);
        DataBufferUtils.release(dataBuffer);
        if (OSInfo.osName(OSInfo.WINDOWS)) {
            try {
                return new String(bytes, CHARSET_CP850);
            } catch (UnsupportedEncodingException e) {
                return StringUtils.EMPTY;
            }
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public String getIpAddressInfo() {
        return OSInfo.osName(OSInfo.WINDOWS) ? IPCONFIG : IFCONFIG;
    }


    @Test
    @DisplayName("Handle error")
    @SneakyThrows
    void handleError() {
        final CountDownLatch count = new CountDownLatch(1);
        Flux.defer(() -> this.readIntputStream("esptool.py"))
                .subscribeOn(Schedulers.boundedElastic())
                .map(this::mappinDataBuffer)
                .onErrorResume(throwable -> Mono.error(new CommandNotFoundException(NOT_FOUND)))
                .doOnTerminate(count::countDown)
                .doOnError(line -> log.info("doOnError: {}", line))
                .subscribe(line -> log.info("Subscribe: {}", line));
        count.await();
    }

}
