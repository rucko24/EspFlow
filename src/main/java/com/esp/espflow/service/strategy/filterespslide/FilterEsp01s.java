package com.esp.espflow.service.strategy.filterespslide;

import com.esp.espflow.entity.EspDeviceInfoRecord;
import com.esp.espflow.enums.GetOsName;
import org.apache.commons.lang3.StringUtils;

/**
 * The FilterEsp01s
 * @author rubn
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
     * @param espDeviceInfoRecord
     * @return boolean
     */
    @Override
    public boolean filter(EspDeviceInfoRecord espDeviceInfoRecord) {
        String descriptivePortName = StringUtils.EMPTY;

        if(GetOsName.getOsName() == GetOsName.FREEBSD) {

            descriptivePortName = "Serial Port";

        } else if (GetOsName.getOsName() == GetOsName.LINUX) {

            descriptivePortName = "CP21";

        } else if(GetOsName.getOsName() == GetOsName.WINDOWS) {

            descriptivePortName = "CP21";

        } else if(GetOsName.getOsName() == GetOsName.MAC) {

            descriptivePortName = "CP21";

        } else {
            //Do nothing
        }

        return espDeviceInfoRecord.chipType().endsWith("8266")
                && espDeviceInfoRecord.detectedFlashSize().equals("1MB")
                && espDeviceInfoRecord.descriptivePortName().contains(descriptivePortName);
    }
}