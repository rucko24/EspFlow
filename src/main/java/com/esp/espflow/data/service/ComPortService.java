package com.esp.espflow.data.service;

import com.esp.espflow.data.util.GetOsName;
import com.fazecast.jSerialComm.SerialPort;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>It will return a set with the processed ports, with length zero if none process, otherwise it will return an available and processed port.</p>
 * <p>Ports that do not have <strong>write permissions</strong> on nix systems will still be returned in this list,
 * to give permissions to that port you can use the following
 * </p>
 *
 * <blockquote><pre>
 *       sudo chmod a+rw /dev/ttyUBS1 /dev/ttyACM0 etc..
 *  </pre></blockquote>
 *
 *  <blockquote><pre>
 *      sudo chmod 666 /dev/ttyUSB1
 *  </pre></blockquote>
 *
 *
 *
 * @author rubn
 */
@Log4j2
@Service
public class ComPortService {

    /**
     * A Set to avoid repeating the processed ports.
     *
     * @return A {@link Set} with name of the ports,
     */
    public Set<String> getPortsList() {
        final SerialPort[] serialPorts = SerialPort.getCommPorts();
        return Optional.of(Arrays.stream(serialPorts))
                .map(this::mappingPorts)
                .orElseGet(() -> {
                    log.info("orElseGet getPortsList {}", serialPorts.length);
                    return Collections.emptySet();
                });
    }

    /**
     *
     * Mapping this Set<String>
     *
     * @param portsStream to apply the mapping
     * @return A {@link Set} with process and filter ports
     */
    private Set<String> mappingPorts(Stream<SerialPort> portsStream) {
            return portsStream
                    .filter(this::isSerialPortFT232R)
                    .map(SerialPort::getSystemPortPath)
                    .map(this::replaceCharacters)
                    .collect(Collectors.toSet());
        }

    /**
     * @return a {@link long} with 0 or more items
     */
    public long countAllDevices()  {
        return Objects.isNull(getPortsList()) ? 0L : getPortsList().size();
    }

    /**
     * Filter if the port is Future Technology Devices International, Ltd FT232 Serial (UART) IC -> FT232R
     *
     * @param filterSerialPortFT232R the infamous <strong>FT232R</strong>
     * @return Boolean
     */
    private Boolean isSerialPortFT232R(SerialPort filterSerialPortFT232R) {
        return !(filterSerialPortFT232R.getDescriptivePortName().startsWith("FT232R"));
    }

    /**
     * Character replacement handled in windows
     *
     * @param port the port to be cleaned
     * @return a {@link String}
     */
    private String replaceCharacters(final String port) {
        if (GetOsName.getOsName() == GetOsName.WINDOWS) {
            return port.replaceAll("\\.", "")
                    .replaceAll("\\\\+", "");
        }
        return port;
    }


}
