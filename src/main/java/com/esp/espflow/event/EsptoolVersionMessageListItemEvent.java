package com.esp.espflow.event;

import com.vaadin.flow.component.messages.MessageListItem;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static com.esp.espflow.util.EspFlowConstants.EXECUTABLE_ICON;

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

        private final String executableType;

    }

    private final String esptoolVersion;
    private final String absolutePathEsptool;
    private EsptoolVersionEventEnum esptoolVersionEventEnum;

    public EsptoolVersionMessageListItemEvent(final EsptoolVersionEventEnum loadedType, final String esptoolVersion,
                                              final String absolutePathEsptool) {
        this.esptoolVersion = esptoolVersion;
        this.absolutePathEsptool = absolutePathEsptool;
        this.esptoolVersionEventEnum = loadedType;
        super.setText(esptoolVersion.concat(" ")
                .concat(absolutePathEsptool));
        super.setTime(LocalDateTime.now().toInstant(ZoneOffset.UTC));
        super.setUserName(loadedType.getExecutableType());
        super.setUserImage("frontend/images/svg-icons/" + EXECUTABLE_ICON);
    }

}
