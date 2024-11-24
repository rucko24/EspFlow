package com.esp.espflow.mappers;

import com.esp.espflow.entity.EspDeviceInfoRecord;
import com.esp.espflow.entity.EspDeviceWithTotalDevicesRecord;

/**
 * @author rubn
 */
public final class EspDeviceWithTotalDevicesMapper {

    public static final EspDeviceWithTotalDevicesMapper INSTANCE = new EspDeviceWithTotalDevicesMapper();

    private EspDeviceWithTotalDevicesMapper(){}

    /**
     *
     * @param espDeviceInfoRecord
     * @param totalDevices
     *
     * @return A record {@link EspDeviceWithTotalDevicesRecord}
     */
    public EspDeviceWithTotalDevicesRecord espDeviceWithTotalDevices(EspDeviceInfoRecord espDeviceInfoRecord, Long totalDevices) {
        return EspDeviceWithTotalDevicesRecord.builder()
                .totalDevices(totalDevices)
                .espDeviceInfoRecord(espDeviceInfoRecord)
                .build();
    }
}
