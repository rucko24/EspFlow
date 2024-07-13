package com.nodemcuui.tool.data.mappers;

import com.nodemcuui.tool.data.entity.EspDeviceInfo;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Objects;

import static com.nodemcuui.tool.data.util.UiToolConstants.CHIP_IS;
import static com.nodemcuui.tool.data.util.UiToolConstants.CHIP_TYPE;
import static com.nodemcuui.tool.data.util.UiToolConstants.CRYSTAL_IS;
import static com.nodemcuui.tool.data.util.UiToolConstants.FLASH_SIZE;
import static com.nodemcuui.tool.data.util.UiToolConstants.MAC;
import static com.nodemcuui.tool.data.util.UiToolConstants.SERIAL_PORT;

/**
 * The EspDeviceInfoMapper
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class EspDeviceInfoMapper {

    public static String key(final String key) {
        if(key.contains(SERIAL_PORT)) {
            return SERIAL_PORT;
        }
        if(key.contains(FLASH_SIZE)) {
            return FLASH_SIZE;
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

    public static String value(final String value) {
        if(value.contains(SERIAL_PORT)) {
            return parseSerialPort(value);
        }
        if(value.contains(FLASH_SIZE)) {
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

    public static EspDeviceInfo mapToEspDeviceInfo(Map<String, String> map) {
        var serialPort = map.get(SERIAL_PORT);
        var flashSize = map.get(FLASH_SIZE);
        var mac = map.get(MAC);
        var crystalIs = map.get(CRYSTAL_IS);
        var chipType = map.get(CHIP_TYPE);
        var chipIs = map.get(CHIP_IS);
        var decimal = parseDecimal(flashSize);
        var hex = parseHexDecimal(decimal);

        return EspDeviceInfo.builder()
                .port(serialPort)
                .detectedFlashSize(flashSize)
                .macAddress(mac)
                .crystalIs(crystalIs)
                .chipType(chipType)
                .chipIs(chipIs)
                .decimal(decimal)
                .hex(hex)
                .build();
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
