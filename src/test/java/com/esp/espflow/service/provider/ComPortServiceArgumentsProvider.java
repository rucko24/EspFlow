package com.esp.espflow.service.provider;

import com.esp.espflow.enums.GetOsName;
import com.fazecast.jSerialComm.SerialPort;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.stream.Stream;

public class ComPortServiceArgumentsProvider implements ArgumentsProvider {

    private static final String COM_PORT = "comPort";
    private static final String PORT_DESCRIPTION = "portDescription";
    private static final String FRIENDLY_NAME = "friendlyName";

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {

        if(GetOsName.getOsName() == GetOsName.WINDOWS) {

            return Stream.of(windowsSerialPorts());

        } else if(GetOsName.getOsName() == GetOsName.LINUX) {

            return Stream.of(linuxSerialPorts());

        } else if(GetOsName.getOsName() == GetOsName.FREEBSD) {

            return Stream.of(freeBsdSerialPorts());

        } else if (GetOsName.getOsName() == GetOsName.MAC) {
            //
            return null;
        }

        return Stream.of(Arguments.of(windowsSerialPorts()));

    }

    /**
     * //COM3@Silicon Labs CP210x USB to UART Bridge (COM3)
     *
     * @return A {@link Arguments}
     */
    private Arguments windowsSerialPorts() {

        final SerialPort serialPort1 = SerialPort.getCommPort("COM1");
        /*
         * Set custom port
         */
        ReflectionTestUtils.setField(serialPort1, COM_PORT, "COM2");
        ReflectionTestUtils.setField(serialPort1, PORT_DESCRIPTION, "COM2");
        ReflectionTestUtils.setField(serialPort1, FRIENDLY_NAME, "Silicon Labs CP210x USB to UART Bridge (COM2)");

        final SerialPort serialPort2 = SerialPort.getCommPort("COM1");
        /*
         * Set custom port
         */
        ReflectionTestUtils.setField(serialPort2, COM_PORT, "COM3");
        ReflectionTestUtils.setField(serialPort2, PORT_DESCRIPTION, "COM3");
        ReflectionTestUtils.setField(serialPort2, FRIENDLY_NAME, "Silicon Labs CP210x USB to UART Bridge (COM3)");

        final SerialPort[] actualSerialPorts = new SerialPort[]{serialPort1, serialPort2};

        final String expectedPort1 = "COM2@Silicon Labs CP210x USB to UART Bridge (COM2)";
        final String expectedPort2 = "COM3@Silicon Labs CP210x USB to UART Bridge (COM3)";

        return Arguments.of(actualSerialPorts, expectedPort1, expectedPort2);
    }

    private Arguments linuxSerialPorts() {

        final SerialPort serialPort1 = SerialPort.getCommPort("/dev/ttyUSB0");
        /*
         * Set custom port
         */
        ReflectionTestUtils.setField(serialPort1, COM_PORT, "/dev/ttyUSB1");
        ReflectionTestUtils.setField(serialPort1, PORT_DESCRIPTION, "/dev/ttyUSB1");
        ReflectionTestUtils.setField(serialPort1, FRIENDLY_NAME, "Serial-1");

        final SerialPort serialPort2 = SerialPort.getCommPort("/dev/ttyUSB0");
        /*
         * Set custom port
         */
        ReflectionTestUtils.setField(serialPort2, COM_PORT, "/dev/ttyUSB2");
        ReflectionTestUtils.setField(serialPort2, PORT_DESCRIPTION, "/dev/ttyUSB2");
        ReflectionTestUtils.setField(serialPort2, FRIENDLY_NAME, "Serial-2");

        final SerialPort[] actualSerialPorts = new SerialPort[]{serialPort1, serialPort2};

        final String expectedPort1 = "/dev/ttyUSB1@Serial-1";
        final String expectedPort2 = "/dev/ttyUSB2@Serial-2";

        return Arguments.of(actualSerialPorts, expectedPort1, expectedPort2);


    }

    private Arguments freeBsdSerialPorts() {

        final SerialPort serialPort1 = SerialPort.getCommPort("/dev/cuaU1");
        /*
         * Set custom port
         */
        ReflectionTestUtils.setField(serialPort1, COM_PORT, "/dev/cuaU1");
        ReflectionTestUtils.setField(serialPort1, PORT_DESCRIPTION, "/dev/cuaU1");
        ReflectionTestUtils.setField(serialPort1, FRIENDLY_NAME, "Serial-1");

        final SerialPort serialPort2 = SerialPort.getCommPort("/dev/cuaU0");
        /*
         * Set custom port
         */
        ReflectionTestUtils.setField(serialPort2, COM_PORT, "/dev/cuaU2");
        ReflectionTestUtils.setField(serialPort2, PORT_DESCRIPTION, "/dev/cuaU2");
        ReflectionTestUtils.setField(serialPort2, FRIENDLY_NAME, "Serial-2");

        final SerialPort[] actualSerialPorts = new SerialPort[]{serialPort1, serialPort2};

        final String expectedPort1 = "/dev/cuaU1@Serial-1";
        final String expectedPort2 = "/dev/cuaU2@Serial-2";

        return Arguments.of(actualSerialPorts, expectedPort1, expectedPort2);


    }

}
