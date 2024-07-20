package com.esp.espflow.data.service;

import com.esp.espflow.data.entity.EspDeviceInfo;
import com.esp.espflow.data.enums.BaudRates;
import com.esp.espflow.data.util.GetOsName;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static com.esp.espflow.data.util.EspFlowConstants.*;
import static org.mockito.Mockito.when;

/**
 * @author rubn
 */
@Log4j2
@ExtendWith(MockitoExtension.class)
class EsptoolServiceTest {

    @InjectMocks
    private EsptoolService esptoolService;

    @Mock
    private CommandService commandService;

    @Mock
    private ComPortService comPortService;

    @Test
    @SneakyThrows
    @DisplayName("esptool.py --port /dev/ttyACM0 --baud 115200 flash_id")
    void readFlashIdWithCustomPort() {

        Flux<String> actual = Flux.just(
                "Detecting chip type... ESP32-S3",
                "Chip is ESP32-S3 (QFN56) (revision v0.0)",
                "Detected flash size: 8MB");

        var expected = EspDeviceInfo.builder()
                .detectedFlashSize("8MB")
                .decimal("8388608")
                .hex("800000")
                .chipType("ESP32-S3")
                .chipIs("ESP32-S3 (QFN56) (revision v0.0)")
                .build();

        String[] commands = ArrayUtils.addAll(null, "esptool.py", "--port", "/dev/ttyACM0", "--baud", "115200", "flash_id");

        when(commandService.processIntputStreamLineByLine(commands))
                .thenReturn(actual);

        StepVerifier.create(esptoolService.readFlashIdFromPort("/dev/ttyACM0"))
                .expectNext(expected)
                .verifyComplete();

    }


    @Test
    @DisplayName("show esptool version")
    @SneakyThrows
    void showEsptoolVersion() {

        String[] commands = ArrayUtils.addAll(GetOsName.shellOsName(), ESPTOOL_PY_VERSION);

        when(commandService.processIntputStreamLineByLine(commands)).thenReturn(Flux.just("esptool.py v4.7.0"));

        StepVerifier.create(esptoolService.showEsptoolVersion())
                .expectNext("esptool.py v4.7.0")
                .verifyComplete();

    }

    @Test
    @DisplayName("read raw each String from this inputStream")
    void  readRawFlashIdFromPort() {

        String[] commands = ArrayUtils.addAll(
                GetOsName.shellOsName(),
                ESPTOOL_PY,
                PORT, "COM3",
                BAUD_RATE, String.valueOf(BaudRates.BAUD_RATE_115200.getBaudRate()),
                FLASH_ID);

        Flux<String> actual = Flux.just(
                "Detecting chip type... ESP32-S3",
                "Chip is ESP32-S3 (QFN56) (revision v0.0)",
                "Detected flash size: 8MB");

        when(commandService.processIntputStreamLineByLine(commands))
                .thenReturn(actual);

        String expetedFirtsLine = "Detecting chip type... ESP32-S3";
        String expetedSecondLine = "Chip is ESP32-S3 (QFN56) (revision v0.0)";
        String expetedThirdLine = "Detected flash size: 8MB";

        StepVerifier.create(this.esptoolService.readRawFlashIdFromPort(commands))
                .expectNext(expetedFirtsLine)
                .expectNext(expetedSecondLine)
                .expectNext(expetedThirdLine)
                .verifyComplete();

    }

}
