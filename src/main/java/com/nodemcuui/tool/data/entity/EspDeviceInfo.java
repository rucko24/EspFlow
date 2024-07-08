package com.nodemcuui.tool.data.entity;

import lombok.Builder;

@Builder
public record EspDeviceInfo(
        String port,
        String name,
        String chipType,
        String chipIs,
        String crystalIs,
        String macAddress,
        String decimal,
        String hex,
        String detectedFlashSize) {
}