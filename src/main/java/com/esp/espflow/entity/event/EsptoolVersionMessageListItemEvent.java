package com.esp.espflow.entity.event;

import com.esp.espflow.util.svgfactory.SvgFactory;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.server.StreamResource;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static com.esp.espflow.util.EspFlowConstants.EXECUTABLE_ICON;
import static com.esp.espflow.util.EspFlowConstants.FRONTEND_IMAGES_SVG_ICONS;

/**
 * @author rubn
 */
@Log4j2
@Getter
public class EsptoolVersionMessageListItemEvent extends MessageListItem {

    @Getter
    @RequiredArgsConstructor
    public enum EsptoolVersionEventEnum {
        BUNDLED("Loaded bundled esptool"),
        CUSTOM("Loaded custom esptool");

        private final String getExecutableType;

    }

    private final String esptoolVersion;
    private final String absolutePathEsptool;

    public EsptoolVersionMessageListItemEvent(final EsptoolVersionEventEnum loadedType, final String esptoolVersion,
                                              final String absolutePathEsptool) {
        this.esptoolVersion = esptoolVersion;
        this.absolutePathEsptool = absolutePathEsptool;
        super.setText(esptoolVersion.concat(" ")
                .concat(absolutePathEsptool));
        super.setTime(LocalDateTime.now().toInstant(ZoneOffset.UTC));
        super.setUserName(loadedType.getGetExecutableType());
        super.setUserImageResource(setCustomImage());
    }

    private static StreamResource setCustomImage() {
        return new StreamResource(EXECUTABLE_ICON,
                () -> SvgFactory.class.getResourceAsStream(FRONTEND_IMAGES_SVG_ICONS + EXECUTABLE_ICON));
    }

}