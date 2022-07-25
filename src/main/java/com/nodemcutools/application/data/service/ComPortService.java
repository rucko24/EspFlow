package com.nodemcutools.application.data.service;


import com.nodemcutools.application.data.util.Notification;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
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
                Notification.builder()
                        .withText("Port found!")
                        .withPosition(Position.MIDDLE)
                        .withDuration(3000)
                        .withIcon(VaadinIcon.WARNING)
                        .withThemeVariant(NotificationVariant.LUMO_PRIMARY)
                        .make();
            } else {
                Notification.builder()
                        .withText("Port not found!")
                        .withPosition(Position.MIDDLE)
                        .withDuration(3000)
                        .withIcon(VaadinIcon.WARNING)
                        .withThemeVariant(NotificationVariant.LUMO_ERROR)
                        .make();
            }
        } catch (UnsatisfiedLinkError e) {
            log.error("Error UnsatisfiedLinkError: {}", e);
            Notification.builder()
                    .withText("UnsatisfiedLinkError")
                    .withPosition(Position.MIDDLE)
                    .withDuration(3000)
                    .withIcon(VaadinIcon.WARNING)
                    .withThemeVariant(NotificationVariant.LUMO_ERROR)
                    .make();

        } catch (NoClassDefFoundError ee) {
            log.error("Error NoClassDefFoundError: {}", ee);
            Notification.builder()
                    .withText("NoClassDefFoundError")
                    .withPosition(Position.MIDDLE)
                    .withDuration(3000)
                    .withIcon(VaadinIcon.WARNING)
                    .withThemeVariant(NotificationVariant.LUMO_ERROR)
                    .make();
        }
        return portList;
    }

}
