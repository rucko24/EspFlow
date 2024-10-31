package com.esp.espflow.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RefreshDevicesEventTest {

    @Test
    @DisplayName("Sending ENABLE event, must be true when disabling the button.")
    void fromEventDisable() {

        var value = RefreshDevicesEvent.fromEvent(RefreshDevicesEvent.ENABLE);

        assertThat(value).isTrue();

    }

    @Test
    @DisplayName("Sending DISABLE event, must be true when disabling the button")
    void fromEventEnable() {

        var value = RefreshDevicesEvent.fromEvent(RefreshDevicesEvent.DISABLE);

        assertThat(value).isFalse();

    }

}