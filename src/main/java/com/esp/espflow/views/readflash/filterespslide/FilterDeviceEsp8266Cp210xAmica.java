package com.esp.espflow.views.readflash.filterespslide;

import com.esp.espflow.enums.GetOsName;
import org.apache.commons.lang3.StringUtils;

/**
 * The FilterDeviceEsp8266Cp210xAmica
 * @author rubn
 */
public class FilterDeviceEsp8266Cp210xAmica {

    private FilterDeviceEsp8266Cp210xAmica() {}
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
     * @return boolean
     */
    public static String amicaDescriptivePortName() {
        String usbSerial = StringUtils.EMPTY;
        if (GetOsName.getOsName() == GetOsName.WINDOWS) {
            usbSerial = "CP210x";
        } else if (GetOsName.getOsName() == GetOsName.LINUX) {
            usbSerial = "CP21";
        } else if (GetOsName.getOsName() == GetOsName.MAC) {
            usbSerial = "USB Serial";
        } else if(GetOsName.getOsName() == GetOsName.FREEBSD){
            usbSerial = "Serial Port (Dial-In)";
        } else {
            //Do nothing
        }
        return usbSerial;
    }

}