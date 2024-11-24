package com.esp.espflow.mappers;

import com.esp.espflow.entity.EspDeviceInfoRecord;
import com.esp.espflow.entity.EspDeviceWithTotalDevicesRecord;
import com.esp.espflow.mappers.provider.EspDeviceWithTotalDevicesMapperProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import static org.assertj.core.api.Assertions.assertThat;

class EspDeviceWithTotalDevicesRecordMapperTest {

    @ParameterizedTest
    @ArgumentsSource(EspDeviceWithTotalDevicesMapperProvider.class)
    @DisplayName("Counting 3 devices and mapping to EspDeviceWithTotalDevices")
    void espDeviceWithTotalDevices(EspDeviceInfoRecord actualEspDeviceInfoRecord,
                                   EspDeviceWithTotalDevicesRecord expectedEspDeviceWithTotalDevicesRecord,
                                   long totalDevices) {

        var actualEspDeviceWithTotalDevices = EspDeviceWithTotalDevicesMapper.INSTANCE.espDeviceWithTotalDevices(actualEspDeviceInfoRecord, totalDevices);

        assertThat(actualEspDeviceWithTotalDevices)
                .usingRecursiveAssertion()
                .isEqualTo(expectedEspDeviceWithTotalDevicesRecord);
    }

}