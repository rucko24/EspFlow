package com.esp.espflow.data.service.strategy.filterespslide;

import com.esp.espflow.data.entity.EspDeviceInfo;
import com.esp.espflow.data.util.GetOsName;

/**
 * The FilterEsp8266Cp210x
 */
public class FilterEsp8266Cp210x implements FilterEspDeviceStrategy {

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
     *      <strong>FreeBSD:</strong>
     *     </li>
     * </ul>
     *
     * @param espDeviceInfo
     * @return boolean
     */
    @Override
    public boolean filter(EspDeviceInfo espDeviceInfo) {
        return espDeviceInfo.chipType().endsWith("8266")
                && espDeviceInfo.detectedFlashSize().equals("4MB")
                && espDeviceInfo.descriptivePortName().contains("CP21");
    }
}