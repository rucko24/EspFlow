package com.esp.espflow.entity;

import lombok.Builder;

@Builder
public record EspDeviceInfo(
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