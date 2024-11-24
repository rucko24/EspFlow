package com.esp.espflow.service.strategy.filterespslide;

import com.esp.espflow.entity.EspDeviceInfoRecord;
import com.esp.espflow.util.GetOsName;

/**
 * The FilterEsp8266CH340G
 */
public class FilterEsp8266CH340G implements FilterEspDeviceStrategy {

    /**
     * <ul>
     *     <li>
     *      <strong>Windows:</strong> the descriptivePortName is, USB-SERIAL CH340 (COM6)
     *     </li>
     *     <li>
     *      <strong>Linux:</strong> the descriptivePortName is, USB Serial
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
        String usbSerial = "";

        if (GetOsName.getOsName() == GetOsName.WINDOWS) {
            usbSerial = "USB-SERIAL";
        } else if (GetOsName.getOsName() == GetOsName.LINUX) {
            usbSerial = "USB Serial";
        } else if (GetOsName.getOsName() == GetOsName.MAC) {
            usbSerial = "USB Serial";
        } else {
            //FreeBSD or other
        }

        return espDeviceInfoRecord.chipType().endsWith("8266")
                && espDeviceInfoRecord.detectedFlashSize().equals("4MB")
                && espDeviceInfoRecord.descriptivePortName().startsWith(usbSerial);
    }
}