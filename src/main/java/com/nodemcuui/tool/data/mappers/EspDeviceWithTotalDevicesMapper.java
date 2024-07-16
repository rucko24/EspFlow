package com.nodemcuui.tool.data.mappers;

import com.nodemcuui.tool.data.entity.EspDeviceInfo;
import com.nodemcuui.tool.data.entity.EspDeviceWithTotalDevices;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 * @author rubn
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class EspDeviceWithTotalDevicesMapper {

    /**
     *
     * @param espDeviceInfo
     * @param totalDevices
     *
     * @return A record {@link EspDeviceWithTotalDevices}
     */
    public static EspDeviceWithTotalDevices espDeviceWithTotalDevices(EspDeviceInfo espDeviceInfo, Long totalDevices) {
        return EspDeviceWithTotalDevices.builder()
                .totalDevices(totalDevices)
                .espDeviceInfo(espDeviceInfo)
                .build();
    }
}
