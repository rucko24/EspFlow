package com.esp.espflow.data.service;

import com.esp.espflow.data.entity.EspDeviceInfo;
import com.esp.espflow.data.enums.BaudRates;
import com.esp.espflow.data.exceptions.CanNotBeReadDeviceException;
import com.esp.espflow.data.mappers.EspDeviceInfoMapper;
import com.esp.espflow.data.util.GetOsName;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Predicate;

import static com.esp.espflow.data.util.EspFlowConstants.CHIP_IS;
import static com.esp.espflow.data.util.EspFlowConstants.CHIP_TYPE;
import static com.esp.espflow.data.util.EspFlowConstants.CRYSTAL_IS;
import static com.esp.espflow.data.util.EspFlowConstants.ESPTOOL_PY_NOT_FOUND;
import static com.esp.espflow.data.util.EspFlowConstants.ESPTOOL_PY_VERSION;
import static com.esp.espflow.data.util.EspFlowConstants.FLASH_SIZE;
import static com.esp.espflow.data.util.EspFlowConstants.MAC;
import static com.esp.espflow.data.util.EspFlowConstants.SERIAL_PORT;

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

    /**
     * The predicate to filter only the necessary lines, if you want to process one more line, this condition should be added here
     */
    private final Predicate<String> predicate = line ->
            line.startsWith(SERIAL_PORT) ||
                    line.startsWith(MAC) ||
                    line.startsWith(FLASH_SIZE) || line.startsWith(CRYSTAL_IS) ||
                    line.startsWith(CHIP_TYPE) || line.startsWith(CHIP_IS);

    /**
     * This returns all existing esp32-based microcontrollers in the current system.
     *
     * @return A {@link Flux} with the {@link EspDeviceInfo}`s corresponding to each <strong>esp32+</strong> microcontrollers
     */
    public Flux<EspDeviceInfo> readAllDevices() {
        return Flux.fromIterable(comPortService.getPortsListWithFriendlyName())
                .switchIfEmpty(this.portListingIsEmpty())
                .flatMap(this::readFlashIdFromPort)
                .doOnNext(onNext -> log.info("onNext device: {}", onNext));
    }

    /**
     *
     * This allows us to raise the exception type {@link CanNotBeReadDeviceException}, when the port list is empty.
     *
     * @return A {@link Mono}
     */
    private Mono<String> portListingIsEmpty() {
        return Mono.defer(() -> Mono.error(new CanNotBeReadDeviceException("Possibly empty ports")));
    }

    /**
     * <p>Counting of all ports filtering out those of</p>
     *
     * <li>
     * <strong>Future Technology Devices International, Ltd FT232 Serial (UART) IC -> FT232R</strong>
     * </li>
     *
     * @return A {@link Mono} with port counting
     *
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
     * @param port the microcontroller port to be scanned
     * @return A {@link Mono} with the {@link EspDeviceInfo} configured with each line of the inputstream
     */
    public Mono<EspDeviceInfo> readFlashIdFromPort(String port) {
        final String parsePort = port.split("@")[0];
        final String descriptivePortName = port.split("@")[1];

        final String[] commands = ArrayUtils.addAll(null, "esptool.py", "--port", parsePort, "--baud",
                String.valueOf(BaudRates.BAUD_RATE_115200.getBaudRate()), "flash_id");

        return commandService.processIntputStreamLineByLine(commands)
                .filter(predicate)
                .collectMap(EspDeviceInfoMapper::key, EspDeviceInfoMapper::value)
                .flatMap(map -> EspDeviceInfoMapper.mapToEspDeviceInfo(map, descriptivePortName) )
                .switchIfEmpty(Mono.defer(() -> EspDeviceInfoMapper.fallback(parsePort)));
    }

    /**
     * Check if esptool is installed by executing the command esptool.py version for the current system.
     *
     * @return A {@link Flux<String> }
     */
    public Flux<String> showEsptoolVersion() {
        String[] commands = ArrayUtils.addAll(GetOsName.shellOsName(), ESPTOOL_PY_VERSION);
        return this.commandService.processIntputStreamLineByLine(commands)
                .take(1)
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
     *
     *  A custom port and baud rate is also set.
     *
     *   <blockquote><pre>
     *      esptool.py --port /dev/ttyUSB1 --baud customBaudRate read_flash 0 ALL esp8266-backupflash.bin
     *   </pre></blockquote><p>
     *

     * @param commands the commands to preocess
     * @return A {@link Flux<String>}
     */
    public Flux<String> downloadFlash(String... commands) {
        return commandService.processCommandsWithCustomCharset(commands)
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
    public void createEspBackUpFlashDirIfNotExists() {
        final Path espBackupFlashDir = Path.of(System.getProperty("java.io.tmpdir").concat("/esp-backup-flash-dir"));
        if (!Files.exists(espBackupFlashDir)) {
            try {
                Files.createDirectory(espBackupFlashDir);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
