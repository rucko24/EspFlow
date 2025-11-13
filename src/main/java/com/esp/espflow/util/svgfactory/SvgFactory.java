package com.esp.espflow.util.svgfactory;

import com.esp.espflow.enums.GetOsName;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.server.streams.DownloadHandler;

import java.util.Objects;

import static com.esp.espflow.util.EspFlowConstants.COPY_ALT_SVG;
import static com.esp.espflow.util.EspFlowConstants.FREE_BSD_ICON;
import static com.esp.espflow.util.EspFlowConstants.FRONTEND_IMAGES_SVG_ICONS;
import static com.esp.espflow.util.EspFlowConstants.ICONS_RESPONSIVE_SIZE;
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
        final DownloadHandler downloadHandler = DownloadHandler.forClassResource(SvgFactory.class, FRONTEND_IMAGES_SVG_ICONS + "usb-port.svg");
        final SvgIcon icon = new SvgIcon(downloadHandler);
        icon.setSize("22px");
        icon.addClassName(ICONS_RESPONSIVE_SIZE);
        return icon;
    }

    /**
     * Button with svg copy style /images/copy-alt.svg
     *
     * @return {@link SvgIcon}
     */
    public static SvgIcon createCopyButtonFromSvg() {
        //copy-alt.svg
        final DownloadHandler downloadHandler = DownloadHandler.forClassResource(SvgFactory.class, FRONTEND_IMAGES_SVG_ICONS + COPY_ALT_SVG);
        final SvgIcon icon = new SvgIcon(downloadHandler);
        icon.setSize("25px");
        icon.addClassName(ICONS_RESPONSIVE_SIZE);
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
        final DownloadHandler downloadHandler = DownloadHandler.forClassResource(SvgFactory.class, FRONTEND_IMAGES_SVG_ICONS + fileName);
        var icon = new SvgIcon(downloadHandler);
        icon.setSize(size);
        if (Objects.nonNull(customHeight)) {
            icon.getStyle().set("height", customHeight);
        }
        icon.addClassName(ICONS_RESPONSIVE_SIZE);
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
        svgIcon.addClassName(ICONS_RESPONSIVE_SIZE);
        return svgIcon;
    }

}
