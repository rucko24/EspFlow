package com.esp.espflow.service;

import com.esp.espflow.entity.EspDeviceInfo;
import com.esp.espflow.enums.BaudRates;
import com.esp.espflow.exceptions.CanNotBeReadDeviceException;
import com.esp.espflow.service.provider.ComPortServiceArgumentsProvider;
import com.esp.espflow.service.provider.EsptoolServiceArgumentsProvider;
import com.esp.espflow.service.provider.EsptoolServiceNoFlashSizeArgumentsProvider;
import com.esp.espflow.service.provider.EsptoolServiceRawFlashIdFromPortArgumentsProvider;
import com.esp.espflow.service.provider.EsptoolServiceReadFlashArgumentsProvider;
import com.esp.espflow.util.GetOsName;
import com.fazecast.jSerialComm.SerialPort;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junitpioneer.jupiter.SetSystemProperty;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import static com.esp.espflow.util.EspFlowConstants.BAUD_RATE;
import static com.esp.espflow.util.EspFlowConstants.CHIP_IS;
import static com.esp.espflow.util.EspFlowConstants.CHIP_TYPE;
import static com.esp.espflow.util.EspFlowConstants.CRYSTAL_IS;
import static com.esp.espflow.util.EspFlowConstants.DETECTED_FLASH_SIZE;
import static com.esp.espflow.util.EspFlowConstants.ESPTOOL_PY;
import static com.esp.espflow.util.EspFlowConstants.ESPTOOL_PY_NOT_FOUND;
import static com.esp.espflow.util.EspFlowConstants.ESPTOOL_PY_VERSION;
import static com.esp.espflow.util.EspFlowConstants.FLASH_ID;
import static com.esp.espflow.util.EspFlowConstants.MAC;
import static com.esp.espflow.util.EspFlowConstants.PORT;
import static com.esp.espflow.util.EspFlowConstants.SERIAL_PORT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * @author rubn
 */
@Log4j2
@TestMethodOrder(MethodOrderer.class)
@ExtendWith(MockitoExtension.class)
class EsptoolServiceTest {

    @InjectMocks
    private EsptoolService esptoolService;

    @Mock
    private CommandService commandService;

    @Mock
    private ComPortService comPortService;

    @Mock
    private EsptoolFallBackService esptoolFallbackService;

    @Mock
    private EsptoolPathService esptoolPathService;

    @SetSystemProperty(key = "os.name", value = "linux")
    @ParameterizedTest
    @ArgumentsSource(ComPortServiceArgumentsProvider.class)
    @DisplayName("we read all the devices, the reading is completely correct")
    void readAllDevices(SerialPort[] actualSerialPorts, String expectedDevUsb1,
                        String expectedDevUsb2) {

        final Predicate<String> predicate = line -> line.startsWith(SERIAL_PORT)
                || line.startsWith(MAC)
                || line.startsWith(DETECTED_FLASH_SIZE)
                || line.startsWith(CRYSTAL_IS)
                || line.startsWith(CHIP_TYPE)
                || line.startsWith(CHIP_IS);

        ReflectionTestUtils.setField(esptoolService, "predicate", predicate);

        when(comPortService.getPortsListWithFriendlyName()).thenReturn(Set.of(expectedDevUsb1));

        String[] commands = new String[]{
                "/tmp/esptool-bundle-dir/esptool-linux-amd64/esptool",
                "--port",
                "/dev/ttyUSB1",
                "--baud",
                "115200",
                "flash_id"
        };

        Flux<String> actualLines = Flux.just(
                "Serial port /dev/ttyUSB1",
                "Detecting chip type... ESP32-S3",
                "Chip is ESP32-S3 (QFN56) (revision v0.0)",
                "Detected flash size: 8MB");

        when(commandService.processInputStreamLineByLine(commands)).thenReturn(actualLines);

        when(esptoolPathService.esptoolPath())
                .thenReturn("/tmp/esptool-bundle-dir/esptool-linux-amd64/esptool");

        EspDeviceInfo expectedEspDeviceInfo = EspDeviceInfo.builder()
                .port("/dev/ttyUSB1")
                .descriptivePortName("Serial-1")
                .chipType("ESP32-S3")
                .chipIs("ESP32-S3 (QFN56) (revision v0.0)")
                //.crystalIs("24MHz")
                //.macAddress("2c:f4:32:10:1d:bf")
                .decimal("8388608")
                .hex("800000")
                .detectedFlashSize("8MB")
                .build();

        StepVerifier.create(esptoolService.readAllDevices())
                .expectNext(expectedEspDeviceInfo)
                .verifyComplete();

        verify(esptoolFallbackService, times(0)).fallback("/dev/ttyUSB1");
        verify(esptoolFallbackService, times(0)).portListingIsEmpty();
        verifyNoInteractions(esptoolFallbackService);

    }

    @Test
    @SetSystemProperty(key = "os.name", value = "linux")
    @DisplayName("Si al leer los puertos tenemos un Set vacio, procesamos un CanNotBeReadDeviceException con mensaje de error" +
            " Possibly empty ports 🤔")
    void readAllDevicesEmptyPorts() {

        when(comPortService.getPortsListWithFriendlyName()).thenReturn(Set.of());

        assertThat(comPortService.getPortsListWithFriendlyName()).isEmpty();

        when(esptoolFallbackService.portListingIsEmpty())
                .thenReturn(Mono.error(new CanNotBeReadDeviceException("Possibly empty ports")));

        StepVerifier.create(esptoolService.readAllDevices())
                .expectErrorMatches(error -> error.getMessage().contains("Possibly empty ports"))
                .verify();

        verify(esptoolFallbackService, times(1)).portListingIsEmpty();
        verifyNoMoreInteractions(esptoolFallbackService);

    }

    @Test
    void aTst() {

        var empty = Flux.fromIterable(Set.of());

        StepVerifier.create(empty)
                .verifyComplete();
    }


    @ParameterizedTest
    @ArgumentsSource(EsptoolServiceArgumentsProvider.class)
    @SneakyThrows
    @DisplayName("esptool.py --port /dev/ttyACM0 --baud 115200 flash_id, the port will come with the friendlyName")
    void readFlashIdWithCustomPort(String portForInputStream,
                                   String portWithFriendlyName,
                                   Flux<String> actualLines, EspDeviceInfo expectedLines) {

        //The method processIntputStreamLineByLine should receive the parsed port without the vendor
        String[] commands = {"esptool.py", "--port", portForInputStream, "--baud", "115200", "flash_id"};

        when(commandService.processInputStreamLineByLine(commands))
                .thenReturn(actualLines);

        when(esptoolPathService.esptoolPath()).thenReturn("esptool.py");

        StepVerifier.create(esptoolService.readFlashIdFromPort(portWithFriendlyName))
                .expectNext(expectedLines)
                .verifyComplete();

    }


    @ParameterizedTest
    @ArgumentsSource(EsptoolServiceNoFlashSizeArgumentsProvider.class)
    @SneakyThrows
    @DisplayName("esptool.py --port /dev/ttyACM0 --baud 115200 flash_id, " +
            "indicates that the response of the console is incomplete and the microcontroller reading was not correct")
    void flashSizeIsNullFromInputWhenMapping(String portForInputStream,
                                             String portWithFriendlyName,
                                             Flux<String> actualLines, EspDeviceInfo expectedLines) {

        //The method processInputStreamLineByLine should receive the parsed port without the vendor
        String[] commands = {"esptool.py", "--port", portForInputStream, "--baud", "115200", "flash_id"};

        when(commandService.processInputStreamLineByLine(commands))
                .thenReturn(actualLines);

        when(esptoolFallbackService.fallback(portForInputStream)).thenReturn(Mono.just(expectedLines));

        when(esptoolPathService.esptoolPath()).thenReturn("esptool.py");

        StepVerifier.create(esptoolService.readFlashIdFromPort(portWithFriendlyName))
                .expectNext(expectedLines)
                .verifyComplete();

        verify(esptoolFallbackService, times(1)).fallback(portForInputStream);
        verifyNoMoreInteractions(esptoolFallbackService);

    }

    @Test
    @DisplayName("show esptool version")
    @SneakyThrows
    void showEsptoolVersion() {

        String[] commands = ESPTOOL_PY_VERSION;

        when(commandService.processInputStreamLineByLine(commands)).thenReturn(Flux.just("esptool.py v4.7.0"));

        when(esptoolPathService.esptoolPath()).thenReturn("esptool.py");

        StepVerifier.create(esptoolService.showEsptoolVersion())
                .expectNext("esptool.py v4.7.0")
                .verifyComplete();

    }

    @Test
    @DisplayName("esptool.py not found!")
    @SneakyThrows
    void showEsptoolVersionFailure() {

        String[] commands = ESPTOOL_PY_VERSION;

        when(esptoolPathService.esptoolPath()).thenReturn("esptool.py");

        when(commandService.processInputStreamLineByLine(commands)).thenReturn(Flux.just("not found"));

        StepVerifier.create(esptoolService.showEsptoolVersion())
                .expectNext(ESPTOOL_PY_NOT_FOUND)
                .verifyComplete();

    }

    @ParameterizedTest
    @ArgumentsSource(EsptoolServiceRawFlashIdFromPortArgumentsProvider.class)
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

    @Test
    @DisplayName("Simple test, counter all devices")
    void countAllDevices() {

        when(comPortService.countAllDevices()).thenReturn(3L);

        StepVerifier.create(esptoolService.countAllDevices())
                .expectNext(3L)
                .verifyComplete();

    }

    @ParameterizedTest
    @ArgumentsSource(EsptoolServiceReadFlashArgumentsProvider.class)
    @SetSystemProperty(key = "java.osname", value = "linux")
    @DisplayName("The entire flash of the device is read using the ALL parameter, the console will display the percentage in real time, en linux")
    void downloadFlash(Flux<String> actualLines, String expectedLinesFromConsole) {

        String[] commands = {"esptool.py", "--port", "/dev/ttyUSB1", "--baud", "115200", "read_flash",
                "0", "ALL", "esp8266-backupflash.bin"};

        when(commandService.processCommandsWithCustomCharset(commands)).thenReturn(actualLines);

        StepVerifier.create(esptoolService.downloadFlash(commands))
                .expectNext(expectedLinesFromConsole)
                .verifyComplete();

    }

    @Test
    @SetSystemProperty(key = "java.io.tmpdir", value = "/tmp")
    @SetSystemProperty(key = "os.name", value = "linux")
    @DisplayName("Creacion de fichero de backup en directorio temporal")
    void createEspBackUpFlashDirIfNotExists() throws IOException {

        Files.deleteIfExists(Path.of("/tmp/esp-backup-flash-dir"));

        assertThatCode(() ->
                this.esptoolService.createEspBackUpFlashDirIfNotExists()).doesNotThrowAnyException();

    }

    @Test
    @SetSystemProperty(key = "os.name", value = "linux")
    @SetSystemProperty(key = "java.io.tmpdir", value = "not-dir")
    @DisplayName("Error creating directory /esp-backup-flash-dir in a directory that does not exist")
    void createEspBackUpFlashDirIfNotExistsFailure() throws IOException {

        Files.deleteIfExists(Path.of("not-dir/esp-backup-flash-dir"));

        assertThatExceptionOfType(IOException.class)
                .isThrownBy(() -> this.esptoolService.createEspBackUpFlashDirIfNotExists())
                .withMessage("Error creating directory /esp-backup-flash-dir on not-dir/esp-backup-flash-dir");

    }

}
