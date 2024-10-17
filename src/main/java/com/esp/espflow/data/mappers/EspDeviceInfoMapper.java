package com.esp.espflow.data.mappers;

import com.esp.espflow.data.entity.EspDeviceInfo;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;

import static com.esp.espflow.data.util.EspFlowConstants.CHIP_IS;
import static com.esp.espflow.data.util.EspFlowConstants.CHIP_TYPE;
import static com.esp.espflow.data.util.EspFlowConstants.CRYSTAL_IS;
import static com.esp.espflow.data.util.EspFlowConstants.DETECTED_FLASH_SIZE;
import static com.esp.espflow.data.util.EspFlowConstants.MAC;
import static com.esp.espflow.data.util.EspFlowConstants.SERIAL_PORT;

/**
 * The EspDeviceInfoMapper
 */
public final class EspDeviceInfoMapper {

    public static final EspDeviceInfoMapper INSTANCE = new EspDeviceInfoMapper();

    private EspDeviceInfoMapper(){}

    public String key(final String key) {
        if(key.contains(SERIAL_PORT)) {
            return SERIAL_PORT;
        }
        if(key.contains(DETECTED_FLASH_SIZE)) {
            return DETECTED_FLASH_SIZE;
        }
        if(key.contains(MAC)) {
            return MAC;
        }
        if(key.contains(CRYSTAL_IS)) {
            return CRYSTAL_IS;
        }
        if(key.contains(CHIP_TYPE)) {
            return CHIP_TYPE;
        }
        if(key.contains(CHIP_IS)) {
            return CHIP_IS;
        }
        return "Unknown";
    }

    public String value(final String value) {
        if(value.contains(SERIAL_PORT)) {
            return parseSerialPort(value);
        }
        if(value.contains(DETECTED_FLASH_SIZE)) {
            return parseFlashSizeValue(value);
        }
        if(value.contains(MAC)) {
            return parseMacValue(value);
        }
        if(value.contains(CRYSTAL_IS)) {
            return parseCrystalIsValue(value);
        }
        if(value.contains(CHIP_TYPE)) {
            return parseChipTypeValue(value);
        }
        if(value.contains(CHIP_IS)) {
            return parseChipIsValue(value);
        }
        return "Unknown";
    }

    public Mono<EspDeviceInfo> mapToEspDeviceInfo(Map<String, String> map, String descriptivePortName) {
        var serialPort = map.get(SERIAL_PORT);
        var flashSize = map.get(DETECTED_FLASH_SIZE);

        if(Objects.isNull(flashSize)) { //In order to trigger the fallback
            return Mono.empty();
        }

        var mac = map.get(MAC);
        var crystalIs = map.get(CRYSTAL_IS);
        var chipType = map.get(CHIP_TYPE);
        var chipIs = map.get(CHIP_IS);
        var decimal = parseDecimal(flashSize);
        var hex = parseHexDecimal(decimal);

        return Mono.just(EspDeviceInfo.builder()
                .port(serialPort)
                .descriptivePortName(descriptivePortName)
                .detectedFlashSize(flashSize)
                .macAddress(mac)
                .crystalIs(crystalIs)
                .chipType(chipType)
                .chipIs(chipIs)
                .decimal(decimal)
                .hex(hex)
                .build());
    }

    private static String parseSerialPort(String line) {
        Objects.requireNonNull(line,"Parse Serial Port value must not be null");
        return line.split(" ")[2].trim();
    }

    private static String parseFlashSizeValue(String line) {
        Objects.requireNonNull(line,"Parse flashSize value must not be null");
        return line.split(":")[1].trim();
    }

    private static String parseMacValue(String line) {
        Objects.requireNonNull(line,"Parse mac value must not be null");
        return line.split(" ")[1].trim();
    }


    private static String parseChipTypeValue(String line) {
        Objects.requireNonNull(line,"Parse chipType value, must not be null");
        if (!(line.contains("Unsupported detection protocol"))) {
            return line.split("type...")[1].trim();
        }
        return line;
    }

    private static String parseCrystalIsValue(final String line) {
        Objects.requireNonNull(line,"Parse crystal value, line must not be null");
        return line.split(" ")[2].trim();
    }

    private static String parseChipIsValue(final String line) {
        Objects.requireNonNull(line,"Parse chip value, line must not be null");
        return line.replace(CHIP_IS,"").trim();
    }

    private static String parseDecimal(String line) {
        Objects.requireNonNull(line,"Parse decimal, line must not be null");
        final String toDec = line.split("MB")[0].trim();
        return String.valueOf(Integer.parseInt(toDec) * 1048576);
    }

    private static String parseHexDecimal(String line) {
        return Integer.toHexString(Integer.parseInt(line));
    }

}
