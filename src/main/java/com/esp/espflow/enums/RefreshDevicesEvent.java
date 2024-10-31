package com.esp.espflow.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *
 * <p>Events to enable or disable Refresh Devices button</p>
 *
 * <p><strong>DISABLE</strong> for disable button or <strong>ENABLE</strong> for enable button...</p>
 *
 * @author rubn
 */
@Getter
@RequiredArgsConstructor
public enum RefreshDevicesEvent {

    DISABLE(false),
    ENABLE(true);

    private final boolean value;

    public static boolean fromEvent(RefreshDevicesEvent event) {
        return event == ENABLE;
    }

}
