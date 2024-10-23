package com.esp.espflow.data.service;

import com.esp.espflow.data.service.provider.ComPortServiceArgumentProvider;
import com.fazecast.jSerialComm.SerialPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author rubn
 */
@ExtendWith(MockitoExtension.class)
class ComPortServiceTest {

    @InjectMocks
    private ComPortService comPortService;

    @ParameterizedTest
    @ArgumentsSource(ComPortServiceArgumentProvider.class)
    @DisplayName("We obtain the list of serial ports, of the type comPort@friendlyName, " +
            "the portDescription is equal to the comPort")
    void getPortsListWithFriendlyName(SerialPort[] actualSerialPorts, String expectedDevUsb1, String expectedDevUsb2) {

        ReflectionTestUtils.setField(comPortService, "serialPorts", actualSerialPorts);

        Set<String> actualPortsListWithFriendlyName= comPortService.getPortsListWithFriendlyName();

        assertThat(actualPortsListWithFriendlyName)
                .isNotEmpty()
                .containsExactly(expectedDevUsb1, expectedDevUsb2);

    }

    @ParameterizedTest
    @ArgumentsSource(ComPortServiceArgumentProvider.class)
    @DisplayName("Serial port count, for this example there are 2 ports")
    void countAllDevices(SerialPort[] actualSerialPorts, String expectedDevUsb1, String expectedDevUsb2) {

        ReflectionTestUtils.setField(comPortService, "serialPorts", actualSerialPorts);

        assertThat(comPortService.countAllDevices())
                .isNotZero()
                .isEqualTo(2);

    }

    @ParameterizedTest
    @ArgumentsSource(ComPortServiceArgumentProvider.class)
    @DisplayName("We verify that you only have the name of the ports before the @, for linux")
    void getOnlyPortsList(SerialPort[] actualSerialPorts, String expectedDevUsb1, String expectedDevUsb2) {

        ReflectionTestUtils.setField(comPortService, "serialPorts", actualSerialPorts);

        assertThat(comPortService.getOnlyPortsList())
                .isNotEmpty()
                .containsExactly(expectedDevUsb1.split("@")[0], expectedDevUsb2.split("@")[0]);

    }

    @Test
    @DisplayName("When the port list is empty")
    void listaDePuertosVacia() {
        final SerialPort[] serialPorts = null;

        ReflectionTestUtils.setField(comPortService, "serialPorts", serialPorts);

        Set<String> portDescription = comPortService.getPortsListWithFriendlyName();

        assertThat(portDescription).isEmpty();

    }

}