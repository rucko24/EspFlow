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
public enum FlashModeEnum {

    QUAD_IO("QIO"),
    Q_OUT("QOUT"),
    DUAL_IO("DIO"),
    DUAL_OUTPUT("DOUT");

    private final String flashMode;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(flashMode);
        return sb.toString();
    }

    /**
     *
     * @return ComponentRenderer<Span, BaudRates>
     */
    public static ComponentRenderer<Div, FlashModeEnum> rendererWithTooltip() {
        final SerializableBiConsumer<Div, FlashModeEnum> flashModesWithToolTip = (div, baudRate) -> {
            div.add(createSpan(baudRate));
        };
        return new ComponentRenderer<>(Div::new, flashModesWithToolTip);
    }

    /**
     *
     * @param flashModeEnum
     * @return Span
     */
    private static Span createSpan(final FlashModeEnum flashModeEnum) {
        final Span span = new Span(String.valueOf(flashModeEnum.getFlashMode()));

        if(FlashModeEnum.QUAD_IO == flashModeEnum) {

            Tooltip.forComponent(span).setText("SPI host uses the \"Quad I/O Fast Read\" command (EBh). Four SPI pins are used to write the flash address part of the command, and to read flash data out. Therefore these phases need a quarter the clock cycles compared to standard SPI.");

        } else if(FlashModeEnum.Q_OUT == flashModeEnum) {

            Tooltip.forComponent(span).setText("SPI host uses the \"Quad Output Fast Read\" command (6Bh) Four SPI pins are used to read the flash data out. Slightly slower than QIO, because the address is written via the single MOSI data pin.");

        } else if(FlashModeEnum.DUAL_IO == flashModeEnum) {

            Tooltip.forComponent(span).setText("Default, SPI host uses the \"Dual I/O Fast Read\" command (BBh).  Two SPI pins are used to write the flash address part of the command, and to read flash data out. Therefore these phases need half the clock cycles compared to standard SPI.");

        } else {// DUAL_OUTPUT

            Tooltip.forComponent(span).setText("SPI host uses the \"Dual Output Fast Read\" command (3Bh).Two SPI pins are used to read flash data out. Slightly slower than DIO, because the address is written via the single MOSI data pin.");

        }

        return span;
    }
}
