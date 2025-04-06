package com.esp.espflow.service.strategy.filterespslide;

import com.esp.espflow.entity.EspDeviceInfoRecord;
import lombok.RequiredArgsConstructor;

/**
 * The FilterContext
 * @author rubn
 */
@RequiredArgsConstructor
public class FilterEspDeviceContext {

    private final FilterEspDeviceStrategy filterEspDeviceStrategy;

    public boolean filter(EspDeviceInfoRecord espDeviceInfoRecord) {
        return filterEspDeviceStrategy.filter(espDeviceInfoRecord);
    }
}