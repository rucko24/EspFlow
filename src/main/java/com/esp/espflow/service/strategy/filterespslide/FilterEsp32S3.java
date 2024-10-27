package com.esp.espflow.service.strategy.filterespslide;

import com.esp.espflow.entity.EspDeviceInfo;
import com.esp.espflow.util.GetOsName;

/**
 * The FilterEsp32S3
 */
public class FilterEsp32S3 implements FilterEspDeviceStrategy {

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
     * @param espDeviceInfo
     * @return boolean
     */
    @Override
    public boolean filter(EspDeviceInfo espDeviceInfo) {

        String descriptivePortName = "";
        String chipIs = "";
        if(GetOsName.getOsName() == GetOsName.LINUX) {
            descriptivePortName = "CP2102N USB to UART";
            chipIs = "ESP32-S3";
        } else if(GetOsName.getOsName() == GetOsName.WINDOWS) {

        } else if(GetOsName.getOsName() == GetOsName.MAC){
            //Do nothing
        } else if(GetOsName.getOsName() == GetOsName.FREEBSD){
            //Do nothing
            descriptivePortName = "Serial Port";
            chipIs = "ESP32-S3";
        }

        return espDeviceInfo.chipType().endsWith("-S3")
                && espDeviceInfo.descriptivePortName().contains(descriptivePortName)
                && espDeviceInfo.chipIs().contains(chipIs);
    }
}