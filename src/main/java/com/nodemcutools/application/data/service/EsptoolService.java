package com.nodemcutools.application.data.service;

import com.nodemcutools.application.data.enums.BaudRates;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.nodemcutools.application.data.util.UiToolConstants.COMMAND_NOT_FOUND;
import static com.nodemcutools.application.data.util.UiToolConstants.FLASH_SIZE;
import static com.nodemcutools.application.data.util.UiToolConstants.MAC;

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
    private String mac;
    private String flashSize;
    private String decflashSize;
    private String hexflashSize;
    private static final String NONE = "-";

    /**
     * esptool.py --port /dev/ttyUSB0 --baud 115200 flash_id
     */
    public Flux<ConcurrentHashMap<String, String>> readFlashId() {
        //read default port with esp8266
        return commandService.processInputStream("esptool.py", "--baud",
                        String.valueOf(BaudRates.BAUD_RATE_115200.getBaudRate()), "flash_id")
                .map((String line) -> Arrays.stream(line.split(System.lineSeparator()))
                        .map(this::mapper)
                        .collect(Collectors.toMap(this::keyMapper, this::valueMapper, String::join,
                                ConcurrentHashMap::new)))
                .doOnNext(map -> map.entrySet().removeIf(item -> item.getValue().equals("")))
                .doOnError(error -> log.error("Error: {}", error));
    }

    private String mapper(final String line) {
        final String tmp = line.split(System.lineSeparator())[0];
        if (tmp.contains(MAC)) {
            return tmp;
        } else if (tmp.contains(FLASH_SIZE)) {
            return tmp;
        }
        return StringUtils.EMPTY;
    }

    private String keyMapper(final String key) {
        if (key.contains(FLASH_SIZE)) {
            return key.split(":")[0].trim();
        } else if (key.contains(MAC)) {
            return key.split(" ")[0].trim().replace(":", "");
        }
        return StringUtils.EMPTY;
    }

    private String valueMapper(final String value) {
        if (value.contains(FLASH_SIZE)) {
            final String s = value.split(":")[1].trim();
            return s;
        } else if (value.contains(MAC)) {
            return value.split(" ")[1].trim();
        }
        return StringUtils.EMPTY;
    }

    public String getFlashSize() {
        if (this.flashSize == null) {
            return NONE;
        }
        return this.flashSize;
    }

    public String getDecflashSize() {
        if (this.decflashSize == null) {
            return NONE;
        }
        return this.decflashSize;
    }

    public String getHexFlashSize() {
        if (this.hexflashSize == null) {
            return NONE;
        }
        return this.hexflashSize;
    }

    private String parseData(final String line) {
        if (line.contains(COMMAND_NOT_FOUND)) {
            return COMMAND_NOT_FOUND;
        }
        return this.parse(line);
    }

    private String parse(String line) {
        //extract flash size
        if (line.contains("flash size")) {
            this.flashSize = line.split(":")[1];
            this.decflashSize = String.valueOf(Integer.parseInt(flashSize) * 1048576);
            this.hexflashSize = Integer.toHexString(Integer.parseInt(decflashSize));
        }
        if (line.startsWith("MAC:")) {
            //extrac mac
            this.mac = line.split(" ")[1];
        }
        return line;
    }

    public void readMac() {

    }

}
