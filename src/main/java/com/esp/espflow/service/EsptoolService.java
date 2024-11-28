package com.esp.espflow.service;

import com.esp.espflow.entity.EspDeviceInfoRecord;
import com.esp.espflow.enums.BaudRatesEnum;
import com.esp.espflow.exceptions.CreateEspBackUpFlashDirException;
import com.esp.espflow.mappers.EspDeviceInfoMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Predicate;

import static com.esp.espflow.util.EspFlowConstants.BAUD_RATE;
import static com.esp.espflow.util.EspFlowConstants.CHIP_IS;
import static com.esp.espflow.util.EspFlowConstants.CHIP_TYPE;
import static com.esp.espflow.util.EspFlowConstants.CRYSTAL_IS;
import static com.esp.espflow.util.EspFlowConstants.DETECTED_FLASH_SIZE;
import static com.esp.espflow.util.EspFlowConstants.ESPTOOL_PY_NOT_FOUND;
import static com.esp.espflow.util.EspFlowConstants.FIRST_LINE_TO_CHECK_IF_IT_EXISTS;
import static com.esp.espflow.util.EspFlowConstants.FLASH_ID;
import static com.esp.espflow.util.EspFlowConstants.JAVA_IO_TEMPORAL_DIR_OS;
import static com.esp.espflow.util.EspFlowConstants.MAC;
import static com.esp.espflow.util.EspFlowConstants.PORT;
import static com.esp.espflow.util.EspFlowConstants.SERIAL_PORT;
import static com.esp.espflow.util.EspFlowConstants.VERSION;

/**
 * @author rubn
 */
@Log4j2
@Getter
@Service
@RequiredArgsConstructor
public class EsptoolService {

    private final CommandService commandService;
    private final ComPortService comPortService;
    private final EsptoolFallBackService esptoolFallBackService;
    private final EsptoolPathService esptoolPathService;

    /**
     * The predicate to filter only the necessary lines, if you want to process one more line, this condition should be added here
     */
    private final Predicate<String> predicate = line ->
            line.startsWith(SERIAL_PORT) ||
                    line.startsWith(MAC) ||
                    line.startsWith(DETECTED_FLASH_SIZE) || line.startsWith(CRYSTAL_IS) ||
                    line.startsWith(CHIP_TYPE) || line.startsWith(CHIP_IS);

    /**
     * This returns all existing esp32-based microcontrollers in the current system.
     *
     * @return A {@link Flux} with the {@link EspDeviceInfoRecord}`s corresponding to each <strong>esp32+</strong> microcontrollers
     */
    public Flux<EspDeviceInfoRecord> readAllDevices() {
        return Flux.fromIterable(comPortService.getPortsListWithFriendlyName())
                .switchIfEmpty(Mono.defer(this.esptoolFallBackService::fallbackEmptyPorts))
                .flatMap(this::readFlashIdFromPort)
                .doOnNext(onNext -> log.info("onNext device: {}", onNext));
    }

    /**
     * <p>Counting of all ports filtering out those of</p>
     *
     * <li>
     * <strong>Future Technology Devices International, Ltd FT232 Serial (UART) IC -> FT232R</strong>
     * </li>
     *
     * @return A {@link Mono} with port counting
     */
    public Mono<Long> countAllDevices() {
        return Mono.just(comPortService.countAllDevices());
    }

    /**
     * <p> It processes to use as parameters the <strong>--baud<strong>, the <strong>--port<strong> and the <strong>flash_id<strong>. </p>
     * <p>
     * With the above, this method creates a map that is then mapped to the entity EspDeviceInfo,
     * as many items are issued we need for convenience to have them in a single item or Mono. </p>
     *
     *  <p><strong>- On Window</strong></p>
     *  <blockquote>
     *      <pre>esptool.py --port parsePort --baud 115200 flash_id </pre>
     *  </blockquote>
     *
     *  <p><strong>- On Linux</strong></p>
     * <blockquote>
     *     <pre> esptool.py --port parsePort --baud 115200 flash_id </pre>
     * </blockquote>
     *
     * @param port the microcontroller port to be scanned
     * @return A {@link Mono} with the {@link EspDeviceInfoRecord} configured with each line of the inputstream
     */
    public Mono<EspDeviceInfoRecord> readFlashIdFromPort(String port) {
        final String parsedPort = port.split("@")[0];
        final String descriptivePortName = port.split("@")[1];

        final String[] commands = new String[]{
                esptoolPathService.esptoolPath(),
                PORT, parsedPort,
                BAUD_RATE, String.valueOf(BaudRatesEnum.BAUD_RATE_115200.getBaudRate()),
                FLASH_ID};

        return commandService.processInputStreamLineByLine(commands)
                .filter(predicate)
                .collectMap(EspDeviceInfoMapper.INSTANCE::key, EspDeviceInfoMapper.INSTANCE::value)
                .flatMap(collectedMapInfo -> EspDeviceInfoMapper.INSTANCE.mapToEspDeviceInfo(
                        collectedMapInfo, descriptivePortName)
                )
                .switchIfEmpty(Mono.defer(() -> this.esptoolFallBackService.fallback(parsedPort)));
    }

    /**
     *
     * <p>
     *     It is used to read line by line what is returned by the flash_id command, without <strong>mapping to any Object.</strong>
     * </p>
     *
     * <p><strong>- On Window</strong></p>
     * <blockquote>
     *     <pre>cmd.exe /c esptool.py --port parsePort --baud 115200 flash_id</pre>
     * </blockquote>
     *
     * <p><strong>- On Linux</strong></p>
     * <blockquote>
     *    <pre>esptool.py --port parsePort --baud 115200 flash_id </pre>
     * </blockquote>
     *
     * @param commands the commands
     *
     * @return A {@link Flux} with each String from this inputStream
     */
    public Flux<String> readRawFlashIdFromPort(final String ...commands) {
        return commandService.processInputStreamLineByLine(commands);
    }

    /**
     * Check if esptool is installed by executing the command esptool.py version for the current system.
     *
     * @return A {@link Flux<String> }
     */
    public Flux<String> showEsptoolVersion() {
        final String[] commands = {esptoolPathService.esptoolPath(), VERSION};
        return this.commandService.processInputStreamLineByLine(commands)
                .take(FIRST_LINE_TO_CHECK_IF_IT_EXISTS)
                .map(this::processLineEsptoolVersion);
    }

    /**
     * Check if esptool is installed by executing the command esptool.py version for the current system.
     *
     * @param absolutePath
     * @param isBundle or custom executable esptool
     *
     * @return A {@link Flux<String> }
     */
    public Flux<String> showEsptoolVersion(String absolutePath, boolean isBundle) {
        final String[] commands = {esptoolPathService.esptoolPath(absolutePath, isBundle), VERSION};
        return this.commandService.processInputStreamLineByLine(commands)
                .take(FIRST_LINE_TO_CHECK_IF_IT_EXISTS)
                .map(this::processLineEsptoolVersion);
    }

    /**
     * This processes the line that has the <strong>esptool.py v</strong> and thus validates that the <strong>esptool.py</strong> is correctly installed.
     *
     * @param rawLine from console
     * @return A {@link String}
     */
    private String processLineEsptoolVersion(final String rawLine) {
        if (!(rawLine.contains("esptool.py v"))) {
            return ESPTOOL_PY_NOT_FOUND;
        }
        return rawLine.split(System.lineSeparator())[0];
    }

    /**
     * Under background, the command executed is this one by default when you want to read the full flash
     * <p>
     * A custom port and baud rate is also set.
     *
     * <blockquote><pre>
     *      esptool.py --port /dev/ttyUSB1 --baud customBaudRate read_flash 0 ALL esp8266-backupflash.bin
     *   </pre>
     *   </blockquote><p>
     *
     * @param commands the commands to process
     * @return A {@link Flux<String>}
     */
    public Flux<String> readFlash(String... commands) {
        return commandService.processCommandsWithCustomCharset(commands)
                .distinct(this::splitPercentaje);
    }

    /**
     *
     *  <blockquote>
     *   <pre>
     *    esptool.py --port COM6 --baud 460800 write_flash --flash_mode dio --flash_size detect 0x00000 esp01s-sec2500.bin
     *   </pre>
     *   </blockquote><p>
     *
     * @param commands the commands to process
     * @return A {@link Flux<String>}
     */
    public Flux<String> writeFlash(String... commands) {
        return commandService.processInputStreamLineByLine(commands)
                .distinct(this::splitPercentaje);
    }

    /**
     * This String is the line read from the console just when the esptool.py displays the percentage
     *
     * @param input line
     * @return A {@link String}
     */
    private String splitPercentaje(String input) {
        if (input.contains("\\((\\d{1,2}|100) %\\)")) {
            return input.split("\\((\\d{1,2}|100) %\\)")[0];
        }
        return input;
    }

    /**
     * Create the folder named <strong>/esp-backup-flash-dir</strong> in the temporary directory of the operating system
     */
    public void createEspBackUpFlashDirIfNotExists() throws CreateEspBackUpFlashDirException {
        final Path espBackupFlashDir = Path.of(JAVA_IO_TEMPORAL_DIR_OS.concat("/esp-backup-flash-dir"));
        if (!Files.exists(espBackupFlashDir)) {
            try {
                Files.createDirectory(espBackupFlashDir);
            } catch (IOException e) {
                throw new CreateEspBackUpFlashDirException("Error creating directory /esp-backup-flash-dir on " + e.getMessage());
            }
        }
    }

}
