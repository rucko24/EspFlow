package com.esp.espflow.views.readflash.wizard;

import com.esp.espflow.util.svgfactory.SvgFactory;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.theme.lumo.LumoUtility;
import lombok.extern.log4j.Log4j2;

import static com.esp.espflow.util.EspFlowConstants.FREE_BSD_ICON;
import static com.esp.espflow.util.EspFlowConstants.INNER_HTML;
import static com.esp.espflow.util.EspFlowConstants.LINUX_ICON;
import static com.esp.espflow.util.EspFlowConstants.MACOS_ICON;
import static com.esp.espflow.util.EspFlowConstants.WINDOWS_ICON;

/**
 * @author rub`n
 */
@Log4j2
public class StepRefreshDevicesContent extends VerticalLayout {

    private static final String TARGET_BLANK = "_blank";

    private final Div content = new Div();
    private final H3 overView = new H3("Overview");
    private final H3 someFeatures = new H3("Some of the features:");

    public StepRefreshDevicesContent() {

        content.addClassNames(LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN);

        var paragraphOverView = paragraphOverView();
        var listItemsWithFeatures = createListItemsWithFeatures();

        content.add(overView, paragraphOverView, someFeatures, listItemsWithFeatures);
        super.add(content);

    }

    private Paragraph paragraphOverView() {
        final Paragraph paragraphOverView = new Paragraph();
        paragraphOverView.getElement().setProperty(INNER_HTML, "In this view we can read all the devices present with " +
                "the <strong>Refresh Devices button</strong>," +
                " creating a Slide configured with device information read. <br> We can also read the firmware and save it, " +
                "to read it we must specify the memory area or read it fully, all the reading process will be shown" +
                " in the output console.");
        paragraphOverView.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);
        return paragraphOverView;
    }

    private UnorderedList createListItemsWithFeatures() {
        final UnorderedList unorderedList = new UnorderedList();

        final ListItem flashIdItem = new ListItem(createFlashIdAnchor());
        final ListItem readFlashItem = new ListItem(createReadFlashAnchor());
        final ListItem downloadFlash = new ListItem(new Span("Allows to download the read firmware."));
        final ListItem changePortPermissions = new ListItem(createChangePermissionsOnPortItem());
        unorderedList.add(flashIdItem, readFlashItem, downloadFlash, changePortPermissions);
        unorderedList.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);

        return unorderedList;
    }

    private Div createFlashIdAnchor() {
        final Anchor flashId = new Anchor("https://docs.espressif.com/projects/esptool/en/latest/esp8266/index.html?highlight=version#quick-start");
        flashId.getElement().setProperty(INNER_HTML, "<strong>flash_id</strong>");
        flashId.setTarget(TARGET_BLANK);
        final Div div = new Div();
        div.add(flashId);
        return div;
    }

    private Component createReadFlashAnchor() {
        final Anchor flashId = new Anchor("https://docs.espressif.com/projects/esptool/en/latest/esp8266/esptool/basic-commands.html?highlight=version#read-flash-contents-read-flash");
        flashId.getElement().setProperty(INNER_HTML, "<strong>read_flash</strong>");
        flashId.setTarget(TARGET_BLANK);
        return new Div(flashId);
    }

    private Component createChangePermissionsOnPortItem() {
        final Span span = new Span("Change of permissions on the serial port.");
        Tooltip.forComponent(span).setText("Change of permissions on the serial port.");

        final Div divSpan = new Div(span);
        divSpan.addClassNames(LumoUtility.Display.INLINE_GRID,
                LumoUtility.TextOverflow.ELLIPSIS,
                LumoUtility.Whitespace.PRE_WRAP);

        final HorizontalLayout row = new HorizontalLayout();
        var svgIconWin = SvgFactory.createIconFromSvg(WINDOWS_ICON, "20px", null);
        Tooltip.forComponent(svgIconWin).setText("Not compatible with Windows");
        var svgIconLinux = SvgFactory.createIconFromSvg(LINUX_ICON, "20px", null);
        Tooltip.forComponent(svgIconLinux).setText("Linux");
        var svgIconMac = SvgFactory.createIconFromSvg(MACOS_ICON, "20px", null);
        Tooltip.forComponent(svgIconMac).setText("macOs");
        var svgIconFreeBSD = SvgFactory.createIconFromSvg(FREE_BSD_ICON, "20px", null);
        Tooltip.forComponent(svgIconFreeBSD).setText("FreeBSD");
        var divIcons = new Div(svgIconWin, svgIconLinux, svgIconMac, svgIconFreeBSD);
        row.add(divSpan, divIcons);
        row.setAlignSelf(Alignment.START, divIcons);
        return row;
    }

}

