package com.esp.espflow.entity;

import lombok.Builder;

/**
 *
 * @author rubn
 */
@Builder
public record EspDeviceInfoRecord(
        String port,
        String descriptivePortName,
        String name,
        String chipType,
        String chipIs,
        String crystalIs,
        String macAddress,
        String decimal,
        String hex,
        String detectedFlashSize) {
}