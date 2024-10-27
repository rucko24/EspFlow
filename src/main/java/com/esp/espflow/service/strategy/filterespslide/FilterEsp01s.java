package com.esp.espflow.service.strategy.filterespslide;

import com.esp.espflow.entity.EspDeviceInfo;

/**
 * The FilterEsp01s
 */
public class FilterEsp01s implements FilterEspDeviceStrategy {

    /**
     * <ul>
     *     <li>
     *      <strong>Windows:</strong> descriptivePortName Silicon Labs CP210x USB to UART Bridge (COM7)
     *     </li>
     *     <li>
     *      <strong>Linux:</strong> descriptivePortName USB Serial
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
                && espDeviceInfo.detectedFlashSize().equals("1MB")
                && espDeviceInfo.descriptivePortName().contains("CP21");
    }
}