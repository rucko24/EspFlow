package com.esp.espflow.mappers.provider;

import com.esp.espflow.entity.EspDeviceInfoRecord;
import com.esp.espflow.entity.EspDeviceWithTotalDevicesRecord;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class EspDeviceWithTotalDevicesMapperProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {

        long totalDevices = 3L;

        EspDeviceInfoRecord actualEspDeviceInfoRecord = EspDeviceInfoRecord.builder()
                .port("/dev/ttyUSB0")
                .descriptivePortName("/dev/ttyUSB0")
                .chipType("ESP8266")
                .chipIs("ESP8266EX")
                .crystalIs("24MHz")
                .macAddress("2c:f4:32:10:1d:bf")
                .decimal("2097152")
                .hex("200000")
                .detectedFlashSize("2MB")
                .build();

        EspDeviceWithTotalDevicesRecord expectedEspDeviceWithTotalDevicesRecord = EspDeviceWithTotalDevicesRecord.builder()
                .espDeviceInfoRecord(actualEspDeviceInfoRecord)
                .totalDevices(totalDevices)
                .build();

        return Stream.of(Arguments.of(actualEspDeviceInfoRecord, expectedEspDeviceWithTotalDevicesRecord, totalDevices));

    }
}
