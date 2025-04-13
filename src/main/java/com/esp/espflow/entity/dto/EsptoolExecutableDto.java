package com.esp.espflow.entity.dto;

import com.esp.espflow.util.svgfactory.SvgFactory;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.theme.lumo.LumoUtility;
import lombok.Builder;

import static com.esp.espflow.util.EspFlowConstants.BLACK_TO_WHITE_ICON;
import static com.esp.espflow.util.EspFlowConstants.EXECUTABLE_ICON;

/**
 * @param id
 * @param name
 * @param isBundled
 * @param absolutePathEsptool
 * @param esptoolVersion
 * @param isSelected
 *
 * @author rub'n
 */
@Builder
public record EsptoolExecutableDto(
        Long id,
        String name,
        boolean isBundled,
        String absolutePathEsptool,
        String esptoolVersion,
        boolean isSelected,
        String sha256) {

    /**
     * @return A {@link ComponentRenderer}
     */
    public static ComponentRenderer<Div, EsptoolExecutableDto> rendererExecutableIcon() {
        final SerializableBiConsumer<Div, EsptoolExecutableDto> serializableBiConsumer = (div, esptoolBundleDto) -> {
            div.addClassNames(LumoUtility.Display.FLEX, LumoUtility.AlignItems.CENTER);
            final SvgIcon icon = SvgFactory.createIconFromSvg(EXECUTABLE_ICON, "20px", "20px");
            icon.addClassName(BLACK_TO_WHITE_ICON);
            final Span span = new Span(displayAbsoluteEsptoolPathForCombo(esptoolBundleDto));
            span.addClassNames(LumoUtility.Padding.Left.SMALL);
            div.add(icon, span);
        };
        return new ComponentRenderer<>(Div::new, serializableBiConsumer);
    }

    /**
     * If it starts with bundle, we concatenate the bundle plus the absolute path, but only the absolute path
     *
     * @param dtoParam
     * @return A {@link String}
     */
    public static String displayAbsoluteEsptoolPathForCombo(final EsptoolExecutableDto dtoParam) {
        final StringBuilder data = new StringBuilder();
        if (dtoParam.absolutePathEsptool.startsWith("/tmp")) {
            data.append("Bundled ".concat(dtoParam.absolutePathEsptool));
        } else {
            data.append(dtoParam.esptoolVersion);
            data.append(" ");
            data.append(dtoParam.absolutePathEsptool);
        }
        return data.toString();
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", absolutePathEsptool='" + absolutePathEsptool + '\'' +
                ", isBundled=" + isBundled +
                ", esptoolVersion='" + esptoolVersion + '\'' +
                ", isSelected=" + isSelected +
                ", sha256='" + sha256 + '\'' +
                '}';
    }

}
