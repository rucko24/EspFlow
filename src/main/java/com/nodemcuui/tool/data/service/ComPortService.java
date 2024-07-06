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

    public Set<String> getPortsList() {
        try {
            return Optional.ofNullable(Arrays.stream(SerialPort.getCommPorts()))
                    .map((Stream<SerialPort> portsStream) -> {
                        final Set<String> set = portsStream
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

    private String replaceCharacters(final String port) {
        if (GetOsName.getOsInfo() == GetOsName.WINDOWS) {
            return port.replaceAll("\\.", "")
                    .replaceAll("\\\\+", "");
        }
        return port;
    }


}
