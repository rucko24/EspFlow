package com.esp.espflow.mappers.provider;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class ExtractChipIsFromStringMapperSuccessProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {

        String parseMePlease = "esptool.py --port COM5 --baud 115200 " +
                "flash_id esptool.py v4.7.0 " +
                "Serial port COM5 " +
                "Connecting.... " +
                "Detecting chip type... " +
                "Unsupported detection protocol, switching and trying again... " +
                "Connecting.... " +
                "Detecting chip type... ESP8266 " +
                "Chip is ESP8266EX " +
                "Features: WiFi " +
                "Crystal is 26MHz " +
                "MAC: 2c:f4:32:10:1d:bf " +
                "Uploading stub... " +
                "Running stub... " +
                "Stub running... " +
                "Manufacturer: 85 " +
                "Device: 6014 " +
                "Detected flash size: 1MB " +
                "Hard resetting via RTS pin...";
        return Stream.of(Arguments.of(parseMePlease));
    }
}
