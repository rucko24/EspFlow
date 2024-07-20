package com.esp.espflow.data.util.svgfactory;

import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.server.StreamResource;

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
                () -> SvgFactory.class.getResourceAsStream("/META-INF/resources/images/svg-icons/usb-port.svg"));
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
                () -> SvgFactory.class.getResourceAsStream("/META-INF/resources/images/svg-icons/copy-alt.svg"));
        final SvgIcon icon = new SvgIcon(iconResource);
        icon.setSize("25px");
        return icon;
    }

    /**
     *
     * Button with svg copy style /images/espflow.svg
     *
     * @return {@link SvgIcon}
     */
    public static SvgIcon createLogoEspFlowFromSvg() {
        //espflow.svg
        final StreamResource iconResource = new StreamResource("espflow.svg",
                () -> SvgFactory.class.getResourceAsStream("/META-INF/resources/images/svg-icons/espflow-new.logo.svg"));
        var icon = new SvgIcon(iconResource);
        icon.setSize("200px");
        icon.getStyle().set("height","45px");
        return icon;
    }

}
