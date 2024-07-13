package com.nodemcuui.tool.data.service;

import com.nodemcuui.tool.data.entity.EspDeviceInfo;
import com.nodemcuui.tool.data.enums.BaudRates;
import com.nodemcuui.tool.data.exceptions.CanNotBeReadDeviceException;
import com.nodemcuui.tool.data.mappers.EspDeviceInfoMapper;
import com.nodemcuui.tool.data.util.GetOsName;
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

import static com.nodemcuui.tool.data.util.UiToolConstants.BIN_SH_C;
import static com.nodemcuui.tool.data.util.UiToolConstants.CHIP_IS;
import static com.nodemcuui.tool.data.util.UiToolConstants.CHIP_TYPE;
import static com.nodemcuui.tool.data.util.UiToolConstants.CMD_C;
import static com.nodemcuui.tool.data.util.UiToolConstants.CRYSTAL_IS;
import static com.nodemcuui.tool.data.util.UiToolConstants.ESPTOOL_PY_NOT_FOUND;
import static com.nodemcuui.tool.data.util.UiToolConstants.ESPTOOL_PY_VERSION;
import static com.nodemcuui.tool.data.util.UiToolConstants.FLASH_SIZE;
import static com.nodemcuui.tool.data.util.UiToolConstants.MAC;
import static com.nodemcuui.tool.data.util.UiToolConstants.SERIAL_PORT;
import static com.nodemcuui.tool.data.util.UiToolConstants.SH_C;

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

    public static String[] bash() {
        GetOsName oS = GetOsName.getOsName();
        if (oS == GetOsName.WINDOWS) {
            return CMD_C;
        } else if (oS == GetOsName.LINUX) {
            return BIN_SH_C;
        } else if (oS == GetOsName.FREEBSD) {
            return BIN_SH_C;
        } else if (oS == GetOsName.MAC) {
            return SH_C;
        }
        return new String[]{GetOsName.OTHER.getName()};
    }

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
     * @return Flux<EspDeviceInfo>
     */
    public Flux<EspDeviceInfo> readAllDevices() {
        return Flux.fromIterable(comPortService.getPortsList())
                .flatMap(this::readFlashIdFromPort)
                .doOnNext(onNext -> log.info("Device {}", onNext));
    }

    /**
     * <p> It processes to use as parameters the <strong>--baud<strong>, the <strong>--port<strong> and the <strong>flash_id<strong>. </p>
     * <p>
     * With the above, this method creates a map that is then mapped to the entity EspDeviceInfo,
     * as many items are issued we need for convenience to have them in a single item or Mono.
     *
     * @param port the microcontroller port to be scanned
     * @return Mono<EspDeviceInfo>
     */
    public Mono<EspDeviceInfo> readFlashIdFromPort(String port) {
        final String[] commands = ArrayUtils.addAll(null, "esptool.py", "--port", port, "--baud",
                String.valueOf(BaudRates.BAUD_RATE_115200.getBaudRate()), "flash_id");

        return commandService.processCommands(commands)
                .filter(predicate)
                .collectMap(EspDeviceInfoMapper::key, EspDeviceInfoMapper::value)
                .flatMap(EspDeviceInfoMapper::mapToEspDeviceInfo)
                .switchIfEmpty(Mono.defer(() -> EspDeviceInfoMapper.fallback(port)));
    }



    /**
     * <p>This simply executes the command to get data from the default micro, with the <strong>flash_id<strong> parameter.</p>
     *
     * <li>
     * The command is: <strong>"esptool.py  flash_id"</strong>
     * </li>
     *
     * @return Mono<EspDeviceInfo>
     */
    public Mono<EspDeviceInfo> readFlashIdFromDefault() {
        final String[] commands = ArrayUtils.addAll(null, "esptool.py", "--baud",
                String.valueOf(BaudRates.BAUD_RATE_115200.getBaudRate()), "flash_id");

        return commandService.processCommands(commands)
                .filter(predicate)
                .collectMap(EspDeviceInfoMapper::key, EspDeviceInfoMapper::value)
                .flatMap(EspDeviceInfoMapper::mapToEspDeviceInfo)
                .switchIfEmpty(Mono.error(new CanNotBeReadDeviceException("No existe el micro controlador")))
                .doOnNext(onNext -> log.info("OnNext {}", onNext))
                .doOnError(error -> log.error("Error: {}", error));
    }

    /**
     * Check if esptool is installed by executing the command esptool.py version for the current system.
     *
     * @return Flux<String>
     */
    public Flux<String> showEsptoolVersion() {
        String[] commands = ArrayUtils.addAll(this.bash(), ESPTOOL_PY_VERSION);
        return this.commandService.processCommands(commands)
                .take(1)
                .map(this::processLineEsptoolVersion);
    }

    /**
     * This processes the line that has the esptool.py v and thus validates that the <strong>esptool.py</strong> is correctly installed.
     *
     * @param rawLine
     * @return String
     */
    private String processLineEsptoolVersion(final String rawLine) {
        if (!(rawLine.contains("esptool.py v"))) {
            return ESPTOOL_PY_NOT_FOUND;
        }
        return rawLine.split(System.lineSeparator())[0];
    }

    /**
     * esptool.py --port /dev/ttyUSB1 read_flash 0 ALL esp8266-backupflash.bin
     *
     * @param commands
     * @return Flux<String>
     */
    public Flux<String> downloadFlash(String... commands) {
        return commandService.processCommandsWithCustomCharset(commands)
                .distinct(this::splitPercentaje);
    }

    /**
     * @param input
     * @return
     */
    private String splitPercentaje(String input) {
        if (input.contains("\\((\\d{1,2}|100) %\\)")) {
            return input.split("\\((\\d{1,2}|100) %\\)")[0];
        }
        return input;
    }

    /**
     * Create the folder named esp-backup-flash-dir in the temporary directory of the operating system
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
