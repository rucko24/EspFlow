package com.esp.espflow.views.readflash.filterespslide;

import com.esp.espflow.enums.GetOsName;
import org.apache.commons.lang3.StringUtils;

/**
 * The FilterDeviceEsp8266CH340G
 * @author rubn
 */
public class FilterDeviceEsp8266CH340G {

    private FilterDeviceEsp8266CH340G() {}
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
     * @return boolean
     */
    public static String esp8266CH340GDescriptivePortName() {
        if (GetOsName.getOsName() == GetOsName.WINDOWS) {
            return "USB-SERIAL";
        } else if (GetOsName.getOsName() == GetOsName.LINUX) {
            return "USB Serial";
        } else if (GetOsName.getOsName() == GetOsName.MAC) {
            return "USB Serial";
        } else if (GetOsName.getOsName() == GetOsName.FREEBSD) {
            return StringUtils.EMPTY;
        }
        return StringUtils.EMPTY;
    }
}