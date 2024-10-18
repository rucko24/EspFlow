package com.esp.espflow.data.service.strategy.filterespslide;

import com.esp.espflow.data.entity.EspDeviceInfo;
import lombok.RequiredArgsConstructor;

/**
 * The FilterContext
 */
@RequiredArgsConstructor
public class FilterEspDeviceContext {

    private final FilterEspDeviceStrategy filterEspDeviceStrategy;

    public boolean filter(EspDeviceInfo espDeviceInfo) {
        return filterEspDeviceStrategy.filter(espDeviceInfo);
    }
}