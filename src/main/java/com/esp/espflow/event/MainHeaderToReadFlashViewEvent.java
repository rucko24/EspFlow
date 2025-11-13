package com.esp.espflow.event;

import com.esp.espflow.enums.RefreshDevicesEvent;
import com.vaadin.flow.component.UI;

/**
 *
 * @param refreshDevicesEvent
 * @param width
 */
public record MainHeaderToReadFlashViewEvent(
        UI ui,
        RefreshDevicesEvent refreshDevicesEvent,
        int width) {
}
