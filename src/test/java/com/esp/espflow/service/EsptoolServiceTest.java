package com.esp.espflow.service;

import com.esp.espflow.entity.EspDeviceInfo;
import com.esp.espflow.enums.BaudRatesEnum;
import com.esp.espflow.exceptions.CanNotBeReadDeviceException;
import com.esp.espflow.service.provider.EsptoolServiceArgumentsProvider;
import com.esp.espflow.service.provider.EsptoolServiceNoFlashSizeArgumentsProvider;
import com.esp.espflow.service.provider.EsptoolServiceRawFlashIdFromPortArgumentsProvider;
import com.esp.espflow.service.provider.EsptoolServiceReadAllDevicesArgumentsProvider;
import com.esp.espflow.service.provider.EsptoolServiceReadFlashArgumentsProvider;
import com.esp.espflow.service.provider.EsptoolServiceWriteFlashArgumentsProvider;
import com.esp.espflow.util.GetOsName;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Stream;

import static com.esp.espflow.util.EspFlowConstants.BAUD_RATE;
import static com.esp.espflow.util.EspFlowConstants.ESPTOOL_PY;
import static com.esp.espflow.util.EspFlowConstants.ESPTOOL_PY_NOT_FOUND;
import static com.esp.espflow.util.EspFlowConstants.ESPTOOL_PY_VERSION;
import static com.esp.espflow.util.EspFlowConstants.FLASH_ID;
import static com.esp.espflow.util.EspFlowConstants.PORT;
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

    @ParameterizedTest
    @ArgumentsSource(EsptoolServiceReadAllDevicesArgumentsProvider.class)
    @SetSystemProperty(key = "os.name", value = "linux")
    @DisplayName("we read all the devices, the reading is completely correct, execute flash_id command")
    void readAllDevices(Flux<String> actualLinesFlashId,
                        Set<String> actualSerialPortWithFriendlyName, EspDeviceInfo expectedEspDeviceInfo) {

        when(comPortService.getPortsListWithFriendlyName()).thenReturn(actualSerialPortWithFriendlyName);

        String[] commands = new String[]{
                "/tmp/esptool-bundle-dir/esptool-linux-amd64/esptool",
                "--port",
                "/dev/ttyUSB1",
                "--baud",
                "115200",
                "flash_id"
        };

        when(commandService.processInputStreamLineByLine(commands)).thenReturn(actualLinesFlashId);

        when(esptoolPathService.esptoolPath())
                .thenReturn("/tmp/esptool-bundle-dir/esptool-linux-amd64/esptool");

        Flux<EspDeviceInfo> actualEspDeviceInfo = esptoolService.readAllDevices();

        StepVerifier.create(actualEspDeviceInfo)
                .expectNext(expectedEspDeviceInfo)
                .verifyComplete();

        verify(esptoolFallbackService, times(0)).fallback("/dev/ttyUSB1");
        verify(esptoolFallbackService, times(0)).fallbackEmptyPorts();
        verifyNoInteractions(esptoolFallbackService);

    }

    @Test
    @SetSystemProperty(key = "os.name", value = "linux")
    @DisplayName("If when reading the ports we get an empty Set, we process a CanNotBeReadDeviceException with error message Possibly empty ports 🤔")
    void readAllDevicesEmptyPorts() {

        when(comPortService.getPortsListWithFriendlyName()).thenReturn(Set.of());

        assertThat(comPortService.getPortsListWithFriendlyName()).isEmpty();

        when(esptoolFallbackService.fallbackEmptyPorts())
                .thenReturn(Mono.error(new CanNotBeReadDeviceException("Possibly empty ports")));

        StepVerifier.create(esptoolService.readAllDevices())
                .expectErrorMatches(error -> error.getMessage().contains("Possibly empty ports"))
                .verify();

        verify(esptoolFallbackService, times(1)).fallbackEmptyPorts();
        verifyNoMoreInteractions(esptoolFallbackService);

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

        verify(esptoolFallbackService, times(0)).fallback(portForInputStream);
        verifyNoInteractions(esptoolFallbackService);

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

        when(esptoolPathService.esptoolPath()).thenReturn("esptool.py");

        when(commandService.processInputStreamLineByLine(ESPTOOL_PY_VERSION))
                .thenReturn(Flux.just("not found"));

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
                BAUD_RATE, String.valueOf(BaudRatesEnum.BAUD_RATE_115200.getBaudRate()),
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
    @DisplayName("The entire flash of the device is read using the ALL parameter, the console will display the percentage in real time, on linux")
    void readFlash(Flux<String> actualLines, String[] expectedLinesFromConsole) {

        String[] commands = {"esptool.py", "--port", "/dev/ttyUSB1", "--baud", "115200", "read_flash",
                "0", "ALL", "esp8266-backupflash.bin"};

        when(commandService.processCommandsWithCustomCharset(commands)).thenReturn(actualLines);

        StepVerifier.create(esptoolService.readFlash(commands))
                .expectNext(expectedLinesFromConsole[0])
                .expectNext(expectedLinesFromConsole[1])
                .expectNext(expectedLinesFromConsole[2])
                .expectNext(expectedLinesFromConsole[3])
                .expectNext(expectedLinesFromConsole[4])
                .expectNext(expectedLinesFromConsole[5])
                .expectNext(expectedLinesFromConsole[6])
                .expectNext(expectedLinesFromConsole[7])
                .expectNext(expectedLinesFromConsole[8])
                .expectNext(expectedLinesFromConsole[9])
                .expectNext(expectedLinesFromConsole[10])
                .expectNext(expectedLinesFromConsole[11])
                .expectNext(expectedLinesFromConsole[12])
                .verifyComplete();

    }

    @ParameterizedTest
    @ArgumentsSource(EsptoolServiceWriteFlashArgumentsProvider.class)
    @DisplayName("We write the flash and process line by line")
    void writeFlash(Flux<String> actualLines, String[] expectedLines) {

        String[] commands = {"esptool.py", "--port", "/dev/ttyUSB1", "--baud", "115200", "write_flash",
                "--flash_mode", "dio", "--flash_size", "detect",
                "0x00000",
                "esp01s-sec2500.bin"};

        when(commandService.processInputStreamLineByLine(commands)).thenReturn(actualLines);

        StepVerifier.create(esptoolService.writeFlash(commands))
                .expectNext(expectedLines[0])
                .expectNext(expectedLines[1])
                .expectNext(expectedLines[2])
                .expectNext(expectedLines[3])
                .expectNext(expectedLines[4])
                .expectNext(expectedLines[5])
                .expectNext(expectedLines[6])
                .expectNext(expectedLines[7])
                .expectNext(expectedLines[8])
                .expectNext(expectedLines[9])
                .expectNext(expectedLines[10])
                .expectNext(expectedLines[11])
                .expectNext(expectedLines[12])
                .expectNext(expectedLines[13])
                .expectNext(expectedLines[14])
                .expectNext(expectedLines[15])
                .expectNext(expectedLines[16])
                .expectNext(expectedLines[17])
                .expectNext(expectedLines[18])
                .expectNext(expectedLines[19])
                .expectNext(expectedLines[20])
                .expectNext(expectedLines[21])
                .expectNext(expectedLines[22])
                .expectNext(expectedLines[23])
                .expectNext(expectedLines[24])
                .expectNext(expectedLines[25])
                .expectNext(expectedLines[26])
                .verifyComplete();

    }

    @Test
    @SetSystemProperty(key = "java.io.tmpdir", value = "/tmp")
    @SetSystemProperty(key = "os.name", value = "linux")
    @DisplayName("Creation of backup file in temporary directory, on linux")
    void createEspBackUpFlashDirIfNotExists() throws IOException {

        Path tmpPath = Path.of("/tmp/esp-backup-flash-dir");

        if (Files.exists(tmpPath)) {
            try (Stream<Path> dirStream = Files.walk(tmpPath)) {

                dirStream
                        .filter(Files::isRegularFile)
                        .map(Path::toFile)
                        .forEach(File::delete);
            }

            Files.deleteIfExists(tmpPath);
            assertThatCode(() ->
                    this.esptoolService.createEspBackUpFlashDirIfNotExists()).doesNotThrowAnyException();

        } else {

            assertThatExceptionOfType(IOException.class)
                    .isThrownBy(() -> this.esptoolService.createEspBackUpFlashDirIfNotExists())
                    .withMessage("Error creating directory /esp-backup-flash-dir on not-dir/esp-backup-flash-dir");
        }


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
