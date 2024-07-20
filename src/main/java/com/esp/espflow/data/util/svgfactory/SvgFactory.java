package com.esp.espflow.data.util.svgfactory;

import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.server.StreamResource;

import java.nio.file.Path;

import static com.esp.espflow.data.util.EspFlowConstants.FRONTEND_IMAGES_SVG_ICONS;

/**
 * @author rubn
 */
public class SvgFactory {

    private SvgFactory(){}
    /**
     * A custom svg icon for usb port connection /images/usb-port-icon.svg
     *
     * @return A {@link SvgIcon}
     */
    public static SvgIcon createUsbIconFromSvg() {
        //usb-port-icon.svg
        final StreamResource iconResource = new StreamResource("usb-port.svg",
                () -> SvgFactory.class.getResourceAsStream(FRONTEND_IMAGES_SVG_ICONS + "usb-port.svg"));
        final SvgIcon icon = new SvgIcon(iconResource);
        icon.setSize("22px");
        return icon;
    }

    /**
     *
     * Button with svg copy style /images/copy-alt.svg
     *
     * @return {@link SvgIcon}
     */
    public static SvgIcon createCopyButtonFromSvg() {
        //copy-alt.svg
        final StreamResource iconResource = new StreamResource("copy-alt.svg",
                () -> SvgFactory.class.getResourceAsStream(FRONTEND_IMAGES_SVG_ICONS + "copy-alt.svg"));
        final SvgIcon icon = new SvgIcon(iconResource);
        icon.setSize("25px");
        return icon;
    }

    /**
     *
     * Button with svg copy style /images/filename.svg
     *
     * @return {@link SvgIcon}
     */
    public static SvgIcon createLogoEspFlowFromSvg(String path) {
        //
        var name = Path.of(path).getFileName().toString();
        final StreamResource iconResource = new StreamResource(name,
                () -> SvgFactory.class.getResourceAsStream(path));
        var icon = new SvgIcon(iconResource);
        icon.setSize("200px");
        icon.getStyle().set("height","45px");
        return icon;
    }

}
