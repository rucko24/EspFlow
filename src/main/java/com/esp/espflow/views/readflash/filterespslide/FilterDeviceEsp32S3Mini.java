package com.esp.espflow.views.readflash.filterespslide;

import com.esp.espflow.enums.GetOsName;
import org.apache.commons.lang3.StringUtils;

/**
 * The FilterDeviceEsp32S3Mini
 * @author rubn
 */
public class FilterDeviceEsp32S3Mini {

    private FilterDeviceEsp32S3Mini() {}

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
     * @return boolean
     */
    public static String esp32s3MiniDescriptivePorName() {
        if(GetOsName.getOsName() == GetOsName.LINUX) {
            return "USB JTAG/serial debug unit";
        } else if(GetOsName.getOsName() == GetOsName.WINDOWS) {
            return StringUtils.EMPTY;
        } else if(GetOsName.getOsName() == GetOsName.MAC){
            return StringUtils.EMPTY;
        } else if(GetOsName.getOsName() == GetOsName.FREEBSD){
            return "Serial Port";
        }
        return StringUtils.EMPTY;
    }

    public static String esp32s3MiniChipIs() {
        if(GetOsName.getOsName() == GetOsName.LINUX) {
            return"ESP32-S3";
        } else if(GetOsName.getOsName() == GetOsName.WINDOWS) {
            return StringUtils.EMPTY;
        } else if(GetOsName.getOsName() == GetOsName.MAC){
            return StringUtils.EMPTY;
        } else if(GetOsName.getOsName() == GetOsName.FREEBSD){
            return  "ESP32-S3";
        }
        return StringUtils.EMPTY;
    }

}