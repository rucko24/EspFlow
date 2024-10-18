package com.esp.espflow.data.service.strategy.filterespslide;

import com.esp.espflow.data.entity.EspDeviceInfo;

/**
 * The FilterEsp32S3
 */
public class FilterEsp32S3 implements FilterEspDeviceStrategy {

    /**
     * <ul>
     *     <li>
     *      <strong>Windows:</strong> the descriptivePortName is,
     *     </li>
     *     <li>
     *      <strong>Linux:</strong> the descriptivePortName is,
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
        return espDeviceInfo.chipType().endsWith("-S3");
    }
}