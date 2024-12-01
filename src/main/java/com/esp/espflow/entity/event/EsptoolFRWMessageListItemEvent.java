package com.esp.espflow.entity.event;

import com.esp.espflow.util.svgfactory.SvgFactory;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.server.StreamResource;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static com.esp.espflow.util.EspFlowConstants.FRONTEND_IMAGES_SVG_ICONS;

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
        super.setUserImageResource(setCustomImage());
    }

    private static StreamResource setCustomImage() {
        return new StreamResource("usb-port-black.svg",
                () -> SvgFactory.class.getResourceAsStream(FRONTEND_IMAGES_SVG_ICONS + "usb-port-black.svg"));

    }

}
