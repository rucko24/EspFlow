package com.esp.espflow.entity.event;

import com.esp.espflow.util.svgfactory.SvgFactory;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.server.StreamResource;
import lombok.extern.log4j.Log4j2;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;

import static com.esp.espflow.util.EspFlowConstants.FRONTEND_IMAGES_SVG_ICONS;

/**
 * @author rub'n
 */
@Log4j2
public class EspMessageListItemEvent extends MessageListItem {
    /*
     * For Custom colors
     */
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public EspMessageListItemEvent(String operation, String port) {
        super.setText(operation);
        super.setTime(LocalDateTime.now().toInstant(ZoneOffset.UTC));
        super.setUserName(port);
        super.setUserImageResource(setCustomImage());
        var colors = Arrays.asList(0, 1, 2, 3, 4, 5, 6);
        super.setUserColorIndex(colors.get(SECURE_RANDOM.nextInt(colors.size())));
    }

    private static StreamResource setCustomImage() {
        return new StreamResource("usb-port-black.svg",
                () -> SvgFactory.class.getResourceAsStream(FRONTEND_IMAGES_SVG_ICONS + "usb-port-black.svg"));

    }

}
