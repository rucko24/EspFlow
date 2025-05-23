package com.esp.espflow.util.svgfactory;

import com.esp.espflow.enums.GetOsName;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.server.StreamResource;

import java.util.Objects;

import static com.esp.espflow.util.EspFlowConstants.COPY_ALT_SVG;
import static com.esp.espflow.util.EspFlowConstants.FREE_BSD_ICON;
import static com.esp.espflow.util.EspFlowConstants.FRONTEND_IMAGES_SVG_ICONS;
import static com.esp.espflow.util.EspFlowConstants.LINUX_ICON;
import static com.esp.espflow.util.EspFlowConstants.MACOS_ICON;
import static com.esp.espflow.util.EspFlowConstants.NO_OS_ICON;
import static com.esp.espflow.util.EspFlowConstants.WINDOWS_ICON;

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
        final StreamResource iconResource = new StreamResource(COPY_ALT_SVG,
                () -> SvgFactory.class.getResourceAsStream(FRONTEND_IMAGES_SVG_ICONS + COPY_ALT_SVG));
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
        Objects.requireNonNull(fileName, "fileName is null, we must put it in the svg-icons folder");
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
     * @param customHeight in pixels, optional parameter
     * @param size         in pixels
     *
     * @return {@link SvgIcon}
     */
    public static SvgIcon OsIcon(String size, String customHeight) {
        SvgIcon svgIcon;
        if (GetOsName.WINDOWS == GetOsName.getOsName()) {
            svgIcon = SvgFactory.createIconFromSvg(WINDOWS_ICON, size, customHeight);
        } else if (GetOsName.LINUX == GetOsName.getOsName()) {
            svgIcon = SvgFactory.createIconFromSvg(LINUX_ICON, size, customHeight);
        } else if (GetOsName.MAC == GetOsName.getOsName()) {
            svgIcon = SvgFactory.createIconFromSvg(MACOS_ICON, size, customHeight);
        } else if (GetOsName.FREEBSD == GetOsName.getOsName()) {
            svgIcon = SvgFactory.createIconFromSvg(FREE_BSD_ICON, size, customHeight);
        } else {
            svgIcon = SvgFactory.createIconFromSvg(NO_OS_ICON, size, customHeight);
        }
        return svgIcon;
    }

}
