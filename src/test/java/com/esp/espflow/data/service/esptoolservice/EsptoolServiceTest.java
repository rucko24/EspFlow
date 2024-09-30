package com.esp.espflow.data.service.esptoolservice;

import com.esp.espflow.data.entity.EspDeviceInfo;
import com.esp.espflow.data.enums.BaudRates;
import com.esp.espflow.data.service.ComPortService;
import com.esp.espflow.data.service.CommandService;
import com.esp.espflow.data.service.EspDeviceInfoFallBackService;
import com.esp.espflow.data.service.EsptoolService;
import com.esp.espflow.data.service.esptoolservice.provider.EsptoolServiceArgumentProvider;
import com.esp.espflow.data.service.esptoolservice.provider.EsptoolServiceNoFlashSizeArgumentProvider;
import com.esp.espflow.data.service.esptoolservice.provider.EsptoolServiceRawFlashIdFromPortArgumentProvider;
import com.esp.espflow.data.util.GetOsName;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.esp.espflow.data.util.EspFlowConstants.BAUD_RATE;
import static com.esp.espflow.data.util.EspFlowConstants.ESPTOOL_PY;
import static com.esp.espflow.data.util.EspFlowConstants.ESPTOOL_PY_VERSION;
import static com.esp.espflow.data.util.EspFlowConstants.FLASH_ID;
import static com.esp.espflow.data.util.EspFlowConstants.PORT;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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

    @Mock
    private EspDeviceInfoFallBackService espDeviceInfoFallbackService;

    @ParameterizedTest
    @ArgumentsSource(EsptoolServiceArgumentProvider.class)
    @SneakyThrows
    @DisplayName("esptool.py --port /dev/ttyACM0 --baud 115200 flash_id, the port will come with the friendlyName")
    void readFlashIdWithCustomPort(String portForInputStream,
                                   String portWithFriendlyName,
                                   Flux<String> actualLines, EspDeviceInfo expectedLines) {

        //The method processIntputStreamLineByLine should receive the parsed port without the vendor
        String[] commands = ArrayUtils.addAll(GetOsName.shellOsName(), "esptool.py", "--port", portForInputStream, "--baud", "115200", "flash_id");

        when(commandService.processInputStreamLineByLine(commands))
                .thenReturn(actualLines);

        StepVerifier.create(esptoolService.readFlashIdFromPort(portWithFriendlyName))
                .expectNext(expectedLines)
                .verifyComplete();

    }


    @ParameterizedTest
    @ArgumentsSource(EsptoolServiceNoFlashSizeArgumentProvider.class)
    @SneakyThrows
    @DisplayName("esptool.py --port /dev/ttyACM0 --baud 115200 flash_id, " +
            "indicates that the response of the console is incomplete and the microcontroller reading was not correct")
    void flashSizeIsNullFromInputWhenMapping(String portForInputStream,
                         String portWithFriendlyName,
                         Flux<String> actualLines, EspDeviceInfo expectedLines) {

        //The method processInputStreamLineByLine should receive the parsed port without the vendor
        String[] commands = ArrayUtils.addAll(GetOsName.shellOsName(), "esptool.py", "--port", portForInputStream, "--baud", "115200", "flash_id");

        when(commandService.processInputStreamLineByLine(commands))
                .thenReturn(actualLines);

        when(espDeviceInfoFallbackService.fallback(portForInputStream)).thenReturn(Mono.just(expectedLines));

        StepVerifier.create(esptoolService.readFlashIdFromPort(portWithFriendlyName))
                .expectNext(expectedLines)
                .verifyComplete();

        verify(espDeviceInfoFallbackService, times(1)).fallback(portForInputStream);
        verifyNoMoreInteractions(espDeviceInfoFallbackService);

    }

    @Test
    @DisplayName("show esptool version")
    @SneakyThrows
    void showEsptoolVersion() {

        String[] commands = ArrayUtils.addAll(GetOsName.shellOsName(), ESPTOOL_PY_VERSION);

        when(commandService.processInputStreamLineByLine(commands)).thenReturn(Flux.just("esptool.py v4.7.0"));

        StepVerifier.create(esptoolService.showEsptoolVersion())
                .expectNext("esptool.py v4.7.0")
                .verifyComplete();

    }

    @ParameterizedTest
    @ArgumentsSource(EsptoolServiceRawFlashIdFromPortArgumentProvider.class)
    @DisplayName("read raw each String from this inputStream")
    void readRawFlashIdFromPort(Flux<String> actualLines, String expetedFirtsLine,
                                String expetedSecondLine, String expetedThirdLine) {

        String[] commands = ArrayUtils.addAll(
                GetOsName.shellOsName(),
                ESPTOOL_PY,
                PORT, "COM3",
                BAUD_RATE, String.valueOf(BaudRates.BAUD_RATE_115200.getBaudRate()),
                FLASH_ID);

        when(commandService.processInputStreamLineByLine(commands))
                .thenReturn(actualLines);

        StepVerifier.create(this.esptoolService.readRawFlashIdFromPort(commands))
                .expectNext(expetedFirtsLine)
                .expectNext(expetedSecondLine)
                .expectNext(expetedThirdLine)
                .verifyComplete();

    }

    @Disabled
    @DisplayName("Simple test, counter all devices")
    void countAllDevices() {
        //FIXME
    }


}
