package com.nodemcutools.application.data.service;

import jssc.SerialPortList;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author rubn
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class ComPortService {

    private final Set<String> portList = new CopyOnWriteArraySet<>();

    public Set<String> getPortsList() {
        try {
            //Scan ports
            Arrays.stream(SerialPortList.getPortNames())
                    .forEach(portList::add);
            if (!portList.isEmpty()) {
                log.info("Port found! {}", portList);
            } else {
                log.info("Port not found! {}", portList);
            }
        } catch (UnsatisfiedLinkError e) {
            log.error("Error UnsatisfiedLinkError: {}", e);
        } catch (NoClassDefFoundError ee) {
            log.error("Error NoClassDefFoundError: {}", ee);
        }
        return portList;
    }

}
