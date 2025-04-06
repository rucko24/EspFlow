package com.esp.espflow.entity;

import lombok.Builder;

/**
 * @author rubn
 * @param totalDevices
 * @param espDeviceInfoRecord
 */
@Builder
public record EspDeviceWithTotalDevicesRecord(Long totalDevices, EspDeviceInfoRecord espDeviceInfoRecord) {

}