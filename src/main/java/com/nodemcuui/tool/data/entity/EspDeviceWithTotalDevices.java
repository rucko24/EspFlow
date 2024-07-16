package com.nodemcuui.tool.data.entity;

import lombok.Builder;

@Builder
public record EspDeviceWithTotalDevices(Long totalDevices, EspDeviceInfo espDeviceInfo) {

}