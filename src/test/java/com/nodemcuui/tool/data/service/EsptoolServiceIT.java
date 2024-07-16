package com.nodemcuui.tool.data.service;

import com.nodemcuui.tool.data.util.ProcessCommandsInternals;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.util.concurrent.CountDownLatch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * @author rubn
 */
@Log4j2
@SpringBootTest(classes = {EsptoolService.class, CommandService.class, ComPortService.class, ProcessCommandsInternals.class})
class EsptoolServiceIT {

    @Autowired
    private EsptoolService esptoolService;

    @Autowired
    private ComPortService comPortService;

    @Test
    @DisplayName("Se leen todas los dispositivos en el OS")
    void readAllDevices() {
        assertThatCode(() -> {

            final CountDownLatch countDownLatch = new CountDownLatch(1);
            esptoolService.readAllDevices()
                    .doOnTerminate(countDownLatch::countDown)
                    .subscribe();

            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }).doesNotThrowAnyException();

    }

    @Test
    @DisplayName("Counting of serial ports in the OS, we process zero or more numbers")
    void countAllDevices() {

        StepVerifier.create(esptoolService.countAllDevices())
                .consumeNextWith(item -> {
                    assertThat(item).isNotNull();
                });

    }

    @Test
    @SneakyThrows
    @DisplayName("esptool.py --port /dev/ttyACM0 --baud 115200 flash_id")
    void readFlashIdFromPort() {

        String port = comPortService.getPortsList()
                .stream()
                .findFirst()
                .orElse("");

        StepVerifier.create(esptoolService.readFlashIdFromPort(port))
                .consumeNextWith(espDeviceInfo -> {
                    assertThat(espDeviceInfo.port()).isNotNull();
                });

    }

    @Test
    @DisplayName("Default read to com port, but it is not being used. ")
    void readFlashIdFromDefault() {

    }

    @Test
    void downloadFlash() {

    }

    @Test
    void createEspBackUpFlashDirIfNotExists() {
    }

    @Test
    void getCommandService() {
    }

    @Test
    void getComPortService() {
    }

    @Test
    void getPredicate() {
    }


    @Test
    @DisplayName("show esptool version")
    @SneakyThrows
    void showEsptoolVersion() {

        StepVerifier.create(esptoolService.showEsptoolVersion())
                .expectNextMatches(line -> line.contains("esptool.py v"))
                .verifyComplete();

    }


}