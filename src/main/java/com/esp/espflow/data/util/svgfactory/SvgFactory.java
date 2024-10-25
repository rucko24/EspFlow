package com.esp.espflow.data.util.svgfactory;

import com.esp.espflow.data.util.GetOsName;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.server.StreamResource;

import java.util.Objects;

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
     * Button with svg copy style /images/filename.svg
     *
     * @param customHeight in pixels, optional parameter
     * @param size         in pixels
     * @return {@link SvgIcon}
     */
    public static SvgIcon createIconFromSvg(String fileName, String size, String customHeight) {
        //
        final StreamResource iconResource = new StreamResource(fileName,
                () -> SvgFactory.class.getResourceAsStream(FRONTEND_IMAGES_SVG_ICONS + fileName));
        var icon = new SvgIcon(iconResource);
        icon.setSize(size);
        if (Objects.nonNull(customHeight)) {
            icon.getStyle().set("height", customHeight);
        }
        return icon;
    }

    /**
     * Os logo at runtime
     *
     * @return {@link SvgIcon}
     */
    public static SvgIcon prefixOsIcon() {
        SvgIcon svgIcon;
        if (GetOsName.WINDOWS == GetOsName.getOsName()) {
            svgIcon = SvgFactory.createIconFromSvg("window.svg", "50", null);
        } else if (GetOsName.LINUX == GetOsName.getOsName()) {
            svgIcon = SvgFactory.createIconFromSvg("linux.svg", "50", null);
        } else if (GetOsName.MAC == GetOsName.getOsName()) {
            svgIcon = SvgFactory.createIconFromSvg("mac.svg", "50", null);
        } else if (GetOsName.FREEBSD == GetOsName.getOsName()) {
            svgIcon = SvgFactory.createIconFromSvg("freebsd.svg", "50", null);
        } else {
            svgIcon = SvgFactory.createIconFromSvg("no-os.svg", "50", null);
        }
        return svgIcon;
    }

}
