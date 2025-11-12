package com.esp.espflow.views.readflash.filterespslide;

import com.esp.espflow.enums.GetOsName;
import org.apache.commons.lang3.StringUtils;

/**
 * The FilterDeviceEsp01s
 * @author rubn
 */
public class FilterDeviceEsp01s {

    private FilterDeviceEsp01s() {}

    public static String esp01sDescriptivePortName() {
        if(GetOsName.getOsName() == GetOsName.FREEBSD) {
            return  "Serial Port";
        } else if (GetOsName.getOsName() == GetOsName.LINUX) {
            return "CP21";
        } else if(GetOsName.getOsName() == GetOsName.WINDOWS) {
            return "CP21";
        } else if(GetOsName.getOsName() == GetOsName.MAC) {
            return "CP21";
        }
        return StringUtils.EMPTY;
    }

}