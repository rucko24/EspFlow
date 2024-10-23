package com.esp.espflow.data.service;

import com.esp.espflow.data.util.GetOsName;
import com.fazecast.jSerialComm.SerialPort;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.*;
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
 * <blockquote><pre>
 *      sudo chmod 666 /dev/ttyUSB1
 *  </pre></blockquote>
 *
 * @author rubn
 */
@Log4j2
@Service
public class ComPortService {

    private final SerialPort[] serialPorts = SerialPort.getCommPorts();

    /**
     * A Set to avoid repeating the processed ports.
     *
     * @return A {@link Set} with name of the ports,
     */
    public Set<String> getPortsListWithFriendlyName() {
        return serialPorts == null
                ? Collections.emptySet()
                : this.mappingPorts(Arrays.stream(serialPorts));
    }

    /**
     * Mapping this Set<String>
     *
     * @param portsStream to apply the mapping
     * @return A {@link Set} with process and filter ports
     */
    private Set<String> mappingPorts(Stream<SerialPort> portsStream) {
        return portsStream
                .filter(this::isSerialPortFT232R)
                .map(this::concatSystemPortPathWithFriendlyName)
                .map(this::replaceCharacters)
                .collect(Collectors.toSet());
    }

    /**
     * Here we concatenate the friendlyName or vendor
     *
     * <blockquote>
     * <pre>Windows: COM3@Silicon Labs CP210x USB to UART Bridge (COM3)</pre>
     * <pre>Linux: /dev/ttyUSB1@USB2.0-Serial</pre>
     * <pre>FreeBSD: /dev/cuaU0@Serial</pre>
     * </blockquote>
     *
     * @param serialPort the current serial port
     * @return A {@link String}
     */
    private String concatSystemPortPathWithFriendlyName(final SerialPort serialPort) {
        return serialPort.getSystemPortPath().concat("@").concat(serialPort.getDescriptivePortName());
    }

    /**
     * @return a {@link long} with 0 or more items
     */
    public long countAllDevices() {
        return Objects.isNull(getPortsListWithFriendlyName()) ? 0L : getPortsListWithFriendlyName().size();
    }

    /**
     * Filter if the port is Future Technology Devices International, Ltd FT232 Serial (UART) IC -> FT232R
     *
     * @param serialPort maybe the infamous <strong>FT232R</strong>
     * @return Boolean
     */
    private Boolean isSerialPortFT232R(SerialPort serialPort) {
        if (Objects.isNull(serialPort)) {
            log.debug("serialPort isSerialPortFT232R() {}", "is null");
            return false;
        }
        return !(serialPort.getDescriptivePortName().startsWith("FT232R")
                || serialPort.getPortDescription().startsWith("FT232R"));
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

    /**
     * A List with only port names
     *
     * @return A {@link List} with name of the ports
     */
    public List<String> getOnlyPortsList() {
        return this.getPortsListWithFriendlyName()
                .stream()
                .map(port -> port.split("@")[0])
                .toList();
    }

}
