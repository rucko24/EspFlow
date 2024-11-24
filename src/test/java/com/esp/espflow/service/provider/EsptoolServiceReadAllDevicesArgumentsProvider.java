package com.esp.espflow.service.provider;

import com.esp.espflow.entity.EspDeviceInfoRecord;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import reactor.core.publisher.Flux;

import java.util.Set;
import java.util.stream.Stream;

public class EsptoolServiceReadAllDevicesArgumentsProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {

        Flux<String> actualLinesFlashId = Flux.just(
                "Serial port /dev/ttyUSB1",
                "Detecting chip type... ESP32-S3",
                "Chip is ESP32-S3 (QFN56) (revision v0.0)",
                "Crystal is 26MHz",
                "MAC: 2c:f4:32:10:1d:bf",
                "Detected flash size: 8MB");

        Set<String> actualSerialPortWithFriendlyName = Set.of("/dev/ttyUSB1@Serial-1");

        EspDeviceInfoRecord expectedEspDeviceInfoRecord = EspDeviceInfoRecord.builder()
                .port("/dev/ttyUSB1")
                .descriptivePortName("Serial-1")
                .chipType("ESP32-S3")
                .chipIs("ESP32-S3 (QFN56) (revision v0.0)")
                .crystalIs("26MHz")
                .macAddress("2c:f4:32:10:1d:bf")
                .decimal("8388608")
                .hex("800000")
                .detectedFlashSize("8MB")
                .build();

        return Stream.of(
                Arguments.of(actualLinesFlashId, actualSerialPortWithFriendlyName, expectedEspDeviceInfoRecord)
        );
    }
}
