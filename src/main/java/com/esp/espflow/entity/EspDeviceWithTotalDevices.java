package com.esp.espflow.entity;

import lombok.Builder;

@Builder
public record EspDeviceWithTotalDevices(Long totalDevices, EspDeviceInfo espDeviceInfo) {

}