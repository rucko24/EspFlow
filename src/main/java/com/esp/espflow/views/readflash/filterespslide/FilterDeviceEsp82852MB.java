package com.esp.espflow.views.readflash.filterespslide;

import com.esp.espflow.enums.GetOsName;
import org.apache.commons.lang3.StringUtils;

/**
 * The FilterDeviceEsp82852MB
 * @author rubn
 */
public class FilterDeviceEsp82852MB {

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
     * @return boolean
     */
    public static String esp82852MBDescriptivePortName() {
        if(GetOsName.getOsName() == GetOsName.FREEBSD) {
            return "Serial Port";
        } else if (GetOsName.getOsName() == GetOsName.LINUX) {
            return "CP21";
        } else if(GetOsName.getOsName() == GetOsName.WINDOWS) {
            return "CP21";
        } else if(GetOsName.getOsName() == GetOsName.MAC) {
            return  "CP21";
        }
        return  StringUtils.EMPTY;
    }
    
}