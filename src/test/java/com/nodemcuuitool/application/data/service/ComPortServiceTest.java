package com.nodemcuuitool.application.data.service;

import com.fazecast.jSerialComm.SerialPort;
import com.nodemcuui.tool.data.service.ComPortService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockedStatic.Verification;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ComPortServiceTest {

    @InjectMocks
    private ComPortService comPortService;


    @Test
    @DisplayName("port list")
    void getPortsList() {
        try (MockedStatic<SerialPort> serialPortMockedStatic = Mockito.mockStatic(SerialPort.class)) {

            var comPort = SerialPort.getCommPort("COM");
            serialPortMockedStatic.when(() -> SerialPort.getCommPort("COM" )).thenReturn(comPort);
            serialPortMockedStatic.when(() -> SerialPort.getCommPorts()).thenReturn(new SerialPort[]{SerialPort.getCommPort("COM")});

            assertThat(comPortService.getPortsList()).contains("COM");
        }

    }

    @Test
    void testGetPortsList_NonEmptyList() {
        // Given
        SerialPort mockPort = Mockito.mock(SerialPort.class);
        when(mockPort.getDescriptivePortName()).thenReturn("FT232R USB UART");
        when(mockPort.getSystemPortPath()).thenReturn("COM3");

        when(SerialPort.getCommPorts()).thenReturn(new SerialPort[]{mockPort});

        // When
        Set<String> portsList = comPortService.getPortsList();

        // Then
        assertThat(portsList.size()).isGreaterThan(1);
        assertThat(portsList.iterator().next()).contains("COM3");
    }

    @Test
    @DisplayName("read serial ports tty linux")
    void serialPortIsTty() {
        final String ttyUsb0 = "/dev/ttyUSB1";
        final Set<String> set = Set.of(ttyUsb0);

        when(comPortService.getPortsList()).thenReturn(set);

        assertThat(comPortService.getPortsList()).isEqualTo(Set.of("/dev/ttyUSB1"));
    }


    @Test
    @DisplayName("test port  FreeBSD")
    void serialPortFreeBSD() {

        final Set<String> set = Set.of("/dev/ttyU0");
        when(comPortService.getPortsList()).thenReturn(set);

        assertThat(comPortService.getPortsList()).isEqualTo(Set.of("/dev/ttyU0"));

        final Set<String> set1 = Set.of("/dev/cuaU0");
        when(comPortService.getPortsList()).thenReturn(set1);

        assertThat(comPortService.getPortsList()).isEqualTo(Set.of("/dev/cuaU0"));
    }

    @Test
    @DisplayName("Replace characters in COM PORT windows")
    void replaceDobleBackSlash() {
        final String comportWin = "\\\\.\\COM3";

        final String actual = comportWin.replaceAll("\\\\", "")
                .replaceAll("\\.", "");

        assertThat(actual).isEqualTo("COM3");
    }

}