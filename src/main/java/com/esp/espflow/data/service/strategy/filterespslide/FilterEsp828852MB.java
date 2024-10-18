package com.esp.espflow.data.service.strategy.filterespslide;

import com.esp.espflow.data.entity.EspDeviceInfo;
import com.esp.espflow.data.util.GetOsName;

/**
 * The FilterEsp828852MB
 */
public class FilterEsp828852MB implements FilterEspDeviceStrategy {

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
     * @param espDeviceInfo
     * @return boolean
     */
    @Override
    public boolean filter(EspDeviceInfo espDeviceInfo) {
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

        return espDeviceInfo.chipType().contains("ESP8285H")
                && espDeviceInfo.detectedFlashSize().equals("2MB")
                && espDeviceInfo.descriptivePortName().startsWith(usbSerial)
                && espDeviceInfo.chipIs().equals("ESP8285H16");
    }
}