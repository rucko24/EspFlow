package com.esp.espflow.enums;

import com.esp.espflow.entity.EspDeviceInfoRecord;
import com.esp.espflow.views.readflash.filterespslide.FilterDeviceEsp01s;
import com.esp.espflow.views.readflash.filterespslide.FilterDeviceEsp32S3;
import com.esp.espflow.views.readflash.filterespslide.FilterDeviceEsp32S3Mini;
import com.esp.espflow.views.readflash.filterespslide.FilterDeviceEsp8266CH340G;
import com.esp.espflow.views.readflash.filterespslide.FilterDeviceEsp8266Cp210xAmica;
import com.esp.espflow.views.readflash.filterespslide.FilterDeviceEsp82852MB;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.function.Supplier;

/**
 * @author rubn
 */
@Getter
@RequiredArgsConstructor
public enum DeviceSlideEnum {

    ESP_01S("8266", "1MB", FilterDeviceEsp01s::esp01sDescriptivePortName, () -> ""),
    ESP8266_AMICA("8266", "4MB", FilterDeviceEsp8266Cp210xAmica::amicaDescriptivePortName, () -> ""),
    ESP8266("8266", "4MB", FilterDeviceEsp8266CH340G::esp8266CH340GDescriptivePortName, () -> ""),
    ESP8285_2MB("8266", "2MB", FilterDeviceEsp82852MB::esp82852MBDescriptivePortName, () -> "ESP8285H16"),
    ESP32_S3_MINI("-S3", "4MB", FilterDeviceEsp32S3Mini::esp32s3MiniDescriptivePorName, FilterDeviceEsp32S3Mini::esp32s3MiniChipIs),
    ESP32_S3("-S3", "8MB", FilterDeviceEsp32S3::esp32s3DescriptivePortName, () -> "ESP32-S3"),
    DEFAULT_DEVICE("N/P", "N/P", () -> "N/P", () -> "");

    private final String chipType;
    private final String detectedFlashSize;
    private final Supplier<String> supplierDescriptivePortName;
    private final Supplier<String> supplierChipIs;

    public static DeviceSlideEnum fromEspDeviceInfoRecordToDeviceSlideEnum(EspDeviceInfoRecord espDeviceInfoRecord) {
        return Arrays.stream(DeviceSlideEnum.values())
                .filter(deviceTest -> filter(deviceTest, espDeviceInfoRecord))
                .findFirst()
                .orElse(DEFAULT_DEVICE);
    }

    private static boolean filter(DeviceSlideEnum deviceTest, EspDeviceInfoRecord espDeviceInfoRecord) {
        return espDeviceInfoRecord.chipType().endsWith(deviceTest.getChipType())
                && espDeviceInfoRecord.detectedFlashSize().equals(deviceTest.getDetectedFlashSize())
                && espDeviceInfoRecord.descriptivePortName().contains(deviceTest.getSupplierDescriptivePortName().get())
                || espDeviceInfoRecord.chipIs().equals(deviceTest.getSupplierChipIs().get());

    }
}