package com.esp.espflow.service.strategy.filterespslide;

import com.esp.espflow.entity.EspDeviceInfoRecord;
import com.esp.espflow.enums.GetOsName;

/**
 * The FilterEsp32S3 mini
 */
public class FilterEsp32S3Mini implements FilterEspDeviceStrategy {

    /**
     * <ul>
     *     <li>
     *      <strong>Windows:</strong> the descriptivePortName is,
     *     </li>
     *     <li>
     *      <strong>Linux:</strong> the descriptivePortName is, CP2102N USB to UART Bridge Controller
     *     </li>
     *     <li>
     *      <strong>MAC:</strong>
     *     </li>
     *     <li>
     *      <strong>FreeBSD:</strong>
     *     </li>
     * </ul>
     *
     * @param espDeviceInfoRecord
     * @return boolean
     */
    @Override
    public boolean filter(EspDeviceInfoRecord espDeviceInfoRecord) {

        String descriptivePortName = "";
        String chipIs = "";
        if(GetOsName.getOsName() == GetOsName.LINUX) {
            descriptivePortName = "USB JTAG/serial debug unit";
            chipIs = "ESP32-S3";
        } else if(GetOsName.getOsName() == GetOsName.WINDOWS) {

        } else if(GetOsName.getOsName() == GetOsName.MAC){
            //Do nothing
        } else if(GetOsName.getOsName() == GetOsName.FREEBSD){
            //Do nothing
            descriptivePortName = "Serial Port";
            chipIs = "ESP32-S3";
        }

        return espDeviceInfoRecord.chipType().endsWith("-S3")
                && espDeviceInfoRecord.descriptivePortName().contains(descriptivePortName)
                && espDeviceInfoRecord.chipIs().contains(chipIs) &&  espDeviceInfoRecord.detectedFlashSize().equals("4MB");
    }
}