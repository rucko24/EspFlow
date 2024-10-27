package com.esp.espflow.service.strategy.filterespslide;

import com.esp.espflow.entity.EspDeviceInfo;
import com.esp.espflow.util.GetOsName;
import org.apache.commons.lang3.StringUtils;

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
     *      <strong>Linux:</strong> the descriptivePortName is, USB2.0-Serial
     *     </li>
     *     <li>
     *      <strong>MAC:</strong>
     *     </li>
     *     <li>
     *      <strong>FreeBSD:</strong> the descriptivePortName is, Serial Port (Dial-In)
     *     </li>
     * </ul>
     *
     * @param espDeviceInfo
     * @return boolean
     */
    @Override
    public boolean filter(EspDeviceInfo espDeviceInfo) {
        String usbSerial = StringUtils.EMPTY;

        if (GetOsName.getOsName() == GetOsName.WINDOWS) {
            usbSerial = "USB-SERIAL";
        } else if (GetOsName.getOsName() == GetOsName.LINUX) {
            usbSerial = "USB2.0-Serial";
        } else if (GetOsName.getOsName() == GetOsName.MAC) {
            usbSerial = "USB Serial";
        } else if (GetOsName.getOsName() == GetOsName.FREEBSD) {
            //FAILED to read CH340G driver
        } else {
            //Do nothing
        }

        return espDeviceInfo.chipType().contains("ESP8266")
                && espDeviceInfo.detectedFlashSize().equals("2MB")
                && espDeviceInfo.descriptivePortName().contains(usbSerial)
                && espDeviceInfo.chipIs().equals("ESP8285H16");
    }
}