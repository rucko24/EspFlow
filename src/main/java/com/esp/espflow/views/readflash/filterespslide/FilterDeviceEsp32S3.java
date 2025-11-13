package com.esp.espflow.views.readflash.filterespslide;

import com.esp.espflow.enums.GetOsName;
import org.apache.commons.lang3.StringUtils;

/**
 * The FilterDeviceEsp32S3
 * @author rubn
 */
public class FilterDeviceEsp32S3 {

    private FilterDeviceEsp32S3() {}

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
    public static String esp32s3DescriptivePortName() {
        if(GetOsName.getOsName() == GetOsName.LINUX) {
            return "CP2102N USB to UART";
        } else if(GetOsName.getOsName() == GetOsName.WINDOWS) {
            return "CP2102N USB to UART";
        } else if(GetOsName.getOsName() == GetOsName.MAC){
            return StringUtils.EMPTY;
        } else if(GetOsName.getOsName() == GetOsName.FREEBSD){
            return "Serial Port";
        }
        return StringUtils.EMPTY;
    }
}