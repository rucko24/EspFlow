package com.esp.espflow.entity.event;

import com.vaadin.flow.component.messages.MessageListItem;
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
public class EspflowMessageListItemEvent extends MessageListItem {

    public EspflowMessageListItemEvent(String message, String userName, String icon) {
        super.setText(message);
        super.setTime(LocalDateTime.now().toInstant(ZoneOffset.UTC));
        super.setUserName(userName);
        super.setUserImage("frontend/images/svg-icons/" + icon);
    }

}
