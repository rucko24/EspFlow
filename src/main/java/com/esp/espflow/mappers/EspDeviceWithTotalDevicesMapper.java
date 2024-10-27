package com.esp.espflow.mappers;

import com.esp.espflow.entity.EspDeviceInfo;
import com.esp.espflow.entity.EspDeviceWithTotalDevices;

/**
 * @author rubn
 */
public final class EspDeviceWithTotalDevicesMapper {

    public static final EspDeviceWithTotalDevicesMapper INSTANCE = new EspDeviceWithTotalDevicesMapper();

    private EspDeviceWithTotalDevicesMapper(){}

    /**
     *
     * @param espDeviceInfo
     * @param totalDevices
     *
     * @return A record {@link EspDeviceWithTotalDevices}
     */
    public EspDeviceWithTotalDevices espDeviceWithTotalDevices(EspDeviceInfo espDeviceInfo, Long totalDevices) {
        return EspDeviceWithTotalDevices.builder()
                .totalDevices(totalDevices)
                .espDeviceInfo(espDeviceInfo)
                .build();
    }
}
