package com.esp.espflow.entity.event;

import com.vaadin.flow.component.messages.MessageListItem;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 *
 * <p>
 * 3 types of events will be used from here, see <strong>esptool.py</strong> command
 *
 * <ul>
 *     <li><strong>flash_id</strong> </li>
 *     <li><strong>read_flash</strong> </li>
 *     <li><strong>write_flash</strong> </li>
 *
 * </ul>
 *
 * </p>
 *
 * @author rub'n
 */
@Log4j2
public class EsptoolFRWMessageListItemEvent extends MessageListItem {

    @Getter
    @RequiredArgsConstructor
    public enum EsptoolEventEnum {
        FLASH_ID("flash_id"),
        READ_FLASH("read_flash"),
        WRITE_FLASH("write_flash"),
        VERSION("version");

        private final String esptoolFRWEvent;

    }

    public EsptoolFRWMessageListItemEvent(String operation, String port) {
        super.setText(operation);
        super.setTime(LocalDateTime.now().toInstant(ZoneOffset.UTC));
        super.setUserName(port);
        super.setUserImage("frontend/images/svg-icons/" + "usb-port-black.svg");
    }

}
