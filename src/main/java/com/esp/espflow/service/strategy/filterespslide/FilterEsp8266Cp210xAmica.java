package com.esp.espflow.service.strategy.filterespslide;

import com.esp.espflow.entity.EspDeviceInfo;
import com.esp.espflow.util.GetOsName;
import org.apache.commons.lang3.StringUtils;

/**
 * The FilterEsp8266Cp210x
 */
public class FilterEsp8266Cp210xAmica implements FilterEspDeviceStrategy {

    /**
     * <ul>
     *     <li>
     *      <strong>Windows:</strong> the descriptivePortName is, Silicon Labs CP210x USB to UART Bridge (COM7)
     *     </li>
     *     <li>
     *      <strong>Linux:</strong> the descriptivePortName is, USB Serial
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
        String chipIs = StringUtils.EMPTY;
        if (GetOsName.getOsName() == GetOsName.WINDOWS) {
            usbSerial = "CP210x";
        } else if (GetOsName.getOsName() == GetOsName.LINUX) {
            usbSerial = "CP21";
            chipIs = "ESP8266EX";
        } else if (GetOsName.getOsName() == GetOsName.MAC) {
            usbSerial = "USB Serial";
        } else if(GetOsName.getOsName() == GetOsName.FREEBSD){
            usbSerial = "Serial Port (Dial-In)";
            chipIs = "ESP8266EX";
        } else {
            //Do nothing
        }

        return espDeviceInfo.chipType().endsWith("8266")
                && espDeviceInfo.detectedFlashSize().equals("4MB")
                && espDeviceInfo.descriptivePortName().contains(usbSerial);
    }
}