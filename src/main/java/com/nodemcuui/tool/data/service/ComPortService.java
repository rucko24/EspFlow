package com.nodemcuui.tool.data.service;

import com.fazecast.jSerialComm.SerialPort;
import com.nodemcuui.tool.data.util.GetOsName;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author rubn
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class ComPortService {

    private SerialPort serialPort;

    public Set<String> getPortsList() {
        try {
            SerialPort[] serialPorts = serialPort.getCommPorts();

            return Optional.ofNullable(Arrays.stream(serialPorts))
                    .map((Stream<SerialPort> portsStream) -> {
                        final Set<String> set = portsStream
                                .filter(this::isSerialPortFT232R)
                                .map(SerialPort::getSystemPortPath)
                                .map(this::replaceCharacters)
                                .collect(Collectors.toSet());
                        if (set.isEmpty()) {
                            return null;
                        }
                        return set;
                    }).orElse(null);
        } catch (Exception ex) {
            log.error("Error with serial port! {}", ex);
        }
        return null;
    }

    public long countAllDevices() {
        return getPortsList().size();
    }

    /**
     *
     * Filter if the port is Future Technology Devices International, Ltd FT232 Serial (UART) IC -> FT232R
     *
     * @param filterSerialPortFT232R
     * @return Boolean
     */
    private Boolean isSerialPortFT232R(SerialPort filterSerialPortFT232R) {
        return !(filterSerialPortFT232R.getDescriptivePortName().startsWith("FT232R"));
    }

    private String replaceCharacters(final String port) {
        if (GetOsName.getOsName() == GetOsName.WINDOWS) {
            return port.replaceAll("\\.", "")
                    .replaceAll("\\\\+", "");
        }
        return port;
    }


}
