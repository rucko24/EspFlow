package com.esp.espflow.enums;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * - https://docs.espressif.com/projects/esptool/en/latest/esp32/esptool/basic-commands.html#erasing-flash-before-write
 *
 * @author rubn
 */
@Getter
@RequiredArgsConstructor
public enum EraseFlashEnum {

    NO(""),
    YES_WIPES_ALL_DATA("--erase-all");

    private final String value;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(value);
        return sb.toString();
    }

    /**
     * @return ComponentRenderer<Span, BaudRates>
     */
    public static ComponentRenderer<Div, EraseFlashEnum> rendererWithTooltip() {
        final SerializableBiConsumer<Div, EraseFlashEnum> eraseFlashWithToolTip = (div, eraseFlashEnum) -> {
            div.add(createSpan(eraseFlashEnum));
        };
        return new ComponentRenderer<>(Div::new, eraseFlashWithToolTip);
    }

    /**
     *
     * @param eraseFlashEnum
     * @return Span
     */
    private static Span createSpan(final EraseFlashEnum eraseFlashEnum) {
        final String wipeStringSpan = eraseFlashEnum == YES_WIPES_ALL_DATA ? "yes, wipes all data" : "no";
        final Span span = new Span(wipeStringSpan);
        if (EraseFlashEnum.YES_WIPES_ALL_DATA == eraseFlashEnum) {
            Tooltip.forComponent(span).setText("--erase-all");
        } else {
            Tooltip.forComponent(span).setText("nothing will go to the console");
        }
        return span;
    }

}
