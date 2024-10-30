package com.esp.espflow.mappers;

import com.esp.espflow.entity.EspDeviceInfo;
import com.esp.espflow.entity.EspDeviceWithTotalDevices;
import com.esp.espflow.mappers.provider.EspDeviceWithTotalDevicesMapperProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import static org.assertj.core.api.Assertions.assertThat;

class EspDeviceWithTotalDevicesMapperTest {

    @ParameterizedTest
    @ArgumentsSource(EspDeviceWithTotalDevicesMapperProvider.class)
    @DisplayName("Counting 3 devices and mapping to EspDeviceWithTotalDevices")
    void espDeviceWithTotalDevices(EspDeviceInfo actualEspDeviceInfo,
                                   EspDeviceWithTotalDevices expectedEspDeviceWithTotalDevices,
                                   long totalDevices) {

        var actualEspDeviceWithTotalDevices = EspDeviceWithTotalDevicesMapper.INSTANCE.espDeviceWithTotalDevices(actualEspDeviceInfo, totalDevices);

        assertThat(actualEspDeviceWithTotalDevices)
                .usingRecursiveAssertion()
                .isEqualTo(expectedEspDeviceWithTotalDevices);
    }

}