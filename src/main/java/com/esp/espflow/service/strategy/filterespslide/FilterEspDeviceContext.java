package com.esp.espflow.service.strategy.filterespslide;

import com.esp.espflow.entity.EspDeviceInfo;
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