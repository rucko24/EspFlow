package com.esp.espflow.entity;

import lombok.Builder;

@Builder
public record EspDeviceWithTotalDevicesRecord(Long totalDevices, EspDeviceInfoRecord espDeviceInfoRecord) {

}