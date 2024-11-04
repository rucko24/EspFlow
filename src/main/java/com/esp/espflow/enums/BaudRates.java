package com.esp.espflow.enums;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author rubn
 */
@Getter
@RequiredArgsConstructor
public enum BaudRates {

    BAUD_RATE_9600(9600),
    BAUD_RATE_57600(57600),
    BAUD_RATE_74880(74880),
    BAUD_RATE_115200(115200),
    BAUD_RATE_230400(230400),
    BAUD_RATE_460800(460800),
    BAUD_RATE_921600(921600);

    private final int baudRate;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder()
                .append(baudRate)
                .append(" bds");
        return sb.toString();
    }

    /**
     * @return ComponentRenderer<Span, BaudRates>
     */
    public static ComponentRenderer<Div, BaudRates> rendererWithTooltip() {
        final SerializableBiConsumer<Div, BaudRates> baudRatesWithToolTip = (div, baudRate) -> {
            div.add(createSpan(baudRate));
        };
        return new ComponentRenderer<>(Div::new, baudRatesWithToolTip);
    }

    /**
     *
     * @param baudRate
     * @return Span
     */
    private static Span createSpan(final BaudRates baudRate) {
        Span span = new Span(String.valueOf(baudRate.getBaudRate()).concat(" bds"));
        if (BAUD_RATE_115200 == baudRate) {
            Tooltip.forComponent(span).setText("Default baud rate");
        } else {
            Tooltip.forComponent(span).setText(String.valueOf(baudRate.getBaudRate()).concat(" baudes"));
        }
        return span;
    }

}
