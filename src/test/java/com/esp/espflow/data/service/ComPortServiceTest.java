package com.esp.espflow.data.service;

import com.esp.espflow.data.service.provider.ComPortServiceArgumentProvider;
import com.fazecast.jSerialComm.SerialPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junitpioneer.jupiter.SetSystemProperty;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author rubn
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class ComPortServiceTest {

    @InjectMocks
    private ComPortService comPortService;

    @Order(1)
    @ParameterizedTest
    @ArgumentsSource(ComPortServiceArgumentProvider.class)
    @DisplayName("We obtain the list of serial ports, of the type comPort@friendlyName, " +
            "the portDescription is equal to the comPort")
    void getPortsListWithFriendlyName(SerialPort[] actualSerialPorts, String expectedDevUsb1, String expectedDevUsb2) {

        try (MockedStatic<SerialPort> mockedStatic = Mockito.mockStatic(SerialPort.class)) {

            mockedStatic.when(SerialPort::getCommPorts).thenReturn(actualSerialPorts);

            Set<String> actualPortsListWithFriendlyName = comPortService.getPortsListWithFriendlyName();

            assertThat(actualPortsListWithFriendlyName)
                    .isNotEmpty()
                    .containsExactly(expectedDevUsb1, expectedDevUsb2);

        }

    }

    @Order(2)
    @ParameterizedTest
    @ArgumentsSource(ComPortServiceArgumentProvider.class)
    @DisplayName("Serial port count, for this example there are 2 ports")
    void countAllDevices(SerialPort[] actualSerialPorts, String expectedDevUsb1, String expectedDevUsb2) {

        try (MockedStatic<SerialPort> mockedStatic = Mockito.mockStatic(SerialPort.class)) {

            mockedStatic.when(SerialPort::getCommPorts).thenReturn(actualSerialPorts);

            assertThat(comPortService.countAllDevices())
                    .isNotZero()
                    .isEqualTo(2);
        }

    }

    @Order(3)
    @Test
    @DisplayName("countAllDevices() should return 0")
    void countAllDevicesReturn0() {

        try (MockedStatic<SerialPort> mockedStatic = Mockito.mockStatic(SerialPort.class)) {

            mockedStatic.when(SerialPort::getCommPorts).thenReturn(null);

            assertThat(comPortService.countAllDevices())
                    .isZero();
        }

    }

    @Order(4)
    @ParameterizedTest
    @ArgumentsSource(ComPortServiceArgumentProvider.class)
    @DisplayName("We verify that you only have the name of the ports before the @, for linux")
    void getOnlyPortsList(SerialPort[] actualSerialPorts, String expectedDevUsb1, String expectedDevUsb2) {

        try (MockedStatic<SerialPort> mockedStatic = Mockito.mockStatic(SerialPort.class)) {

            mockedStatic.when(SerialPort::getCommPorts).thenReturn(actualSerialPorts);

            assertThat(comPortService.getOnlyPortsList())
                    .isNotEmpty()
                    .containsExactly(expectedDevUsb1.split("@")[0], expectedDevUsb2.split("@")[0]);
        }

    }

    @Order(5)
    @Test
    @DisplayName("When the port list is empty")
    void emptyPortList() {

        try (MockedStatic<SerialPort> mockedStatic = Mockito.mockStatic(SerialPort.class)) {
            final SerialPort[] actualSerialPorts = null;

            mockedStatic.when(SerialPort::getCommPorts).thenReturn(actualSerialPorts);

            Set<String> portDescription = comPortService.getPortsListWithFriendlyName();

            assertThat(portDescription).isEmpty();

        }

    }

    @Order(6)
    @ParameterizedTest
    @ArgumentsSource(ComPortServiceArgumentProvider.class)
    @DisplayName("One of the serial ports is null")
    void oneSerialPortIsNull(SerialPort[] actualSerialPorts, String expectedDevUsb1, String expectedDevUsb2) {

        try (MockedStatic<SerialPort> mockedStatic = Mockito.mockStatic(SerialPort.class)) {

            //Set one port to null
            actualSerialPorts[1] = null;

            mockedStatic.when(SerialPort::getCommPorts).thenReturn(actualSerialPorts);

            assertThat(comPortService.countAllDevices())
                    .isNotZero()
                    .isEqualTo(1);
        }

    }

    @Order(7)
    @SetSystemProperty(key = "os.name", value = "window")
    @ParameterizedTest
    @ArgumentsSource(ComPortServiceArgumentProvider.class)
    @DisplayName("We simulate window to replace the characters of the port")
    void replaceCharacters(SerialPort[] actualSerialPorts, String expectedDevUsb1, String expectedDevUsb2) {

        try (MockedStatic<SerialPort> mockedStatic = Mockito.mockStatic(SerialPort.class)) {

            mockedStatic.when(SerialPort::getCommPorts).thenReturn(actualSerialPorts);

            assertThat(comPortService.countAllDevices())
                    .isNotZero()
                    .isEqualTo(2);
        }

    }


}