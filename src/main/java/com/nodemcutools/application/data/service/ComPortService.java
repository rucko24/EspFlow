package com.nodemcutools.application.data.service;


import com.nodemcutools.application.data.util.NotificationsUtils;
import com.vaadin.flow.component.notification.Notification;
import jssc.SerialPortList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author rubn
 */
@Service
@RequiredArgsConstructor
public class ComPortService implements NotificationsUtils {

    private final Set<String> portList = new CopyOnWriteArraySet<>();

    public Set<String> getPortsList() {
        try {
            //Scan ports
            Arrays.stream(SerialPortList.getPortNames())
                    .forEach(portList::add);
            if (!portList.isEmpty()) {
                Notification.show("Puertos escaneados correcto");
            } else {
                showError("Puertos escaneados incorrecto, revisar conexiones");
            }
        } catch (UnsatisfiedLinkError e) {
            showError("Sin Conexion con arduino");
        } catch (NoClassDefFoundError ee) {
            showError("Sin Conexion con arduino, revisar servicios ");
        }
        return portList;
    }

}
