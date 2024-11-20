package com.esp.espflow.views.flashesp.wizards;

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
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;
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
@RolesAllowed("ADMIN")
public class InitialInformationFlashEspViewDialog extends Div {

    private static final String TARGET_BLANK = "_blank";
    private final Div content = new Div();
    private final H3 someFeatures = new H3("Some of the features in this view:");

    public InitialInformationFlashEspViewDialog() {

        content.addClassNames(LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN);

        var paragraphOverView = paragraphOverView();
        var listItemsWithFeatures = createListItemsWithFeatures();

        content.add(this.welcomeToEspFlow(), paragraphOverView, someFeatures, listItemsWithFeatures);
        super.add(content);
    }

    private H3 welcomeToEspFlow() {
        final H3 welcome = new H3("Welcome to ");
        final Anchor welcomeAnchor = new Anchor("https://github.com/rucko24/EspFlow");
        welcomeAnchor.getElement().setProperty(INNER_HTML, "<strong>EspFlow</strong>");
        welcomeAnchor.setTarget(TARGET_BLANK);
        welcome.add(welcomeAnchor);
        return welcome;
    }

    private Paragraph paragraphOverView() {
        final Paragraph paragraphOverView = new Paragraph();
        paragraphOverView.getElement().setProperty(INNER_HTML, "This view allows us to scan the available ports of" +
                " the system, see information of each <strong>esp8266</strong>, " +
                "<strong>esp32</strong>, write the firmware in each one of them. " +
                "Each command executed will write the result of the operation to the output console");
        paragraphOverView.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);
        return paragraphOverView;
    }

    private UnorderedList createListItemsWithFeatures() {
        final UnorderedList unorderedList = new UnorderedList();

        final ListItem flashIdItem = new ListItem(createFlashIdAnchor());
        final ListItem writeIdItem = new ListItem(createWriteFlashAnchor());
        final ListItem esptoolPyVersionItem = new ListItem("Show the version of esptool");
        esptoolPyVersionItem.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);
        final ListItem changePermissionsOnPort = new ListItem(createChangePermissionsOnPortItem());
        changePermissionsOnPort.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);
        changePermissionsOnPort.setWidthFull();
        unorderedList.add(flashIdItem, writeIdItem, esptoolPyVersionItem, changePermissionsOnPort);

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

    private Div createWriteFlashAnchor() {
        final Anchor flashId = new Anchor("https://docs.espressif.com/projects/esptool/en/latest/esp8266/esptool/basic-commands.html?highlight=flash_id#write-binary-data-to-flash-write-flash");
        flashId.getElement().setProperty(INNER_HTML, "<strong>write_flash</strong>");
        flashId.setTarget(TARGET_BLANK);
        final Div div = new Div();
        div.add(flashId);
        return div;
    }

    private Component createChangePermissionsOnPortItem() {
        final Span span = new Span("Change of permissions on the serial port.");
        Tooltip.forComponent(span).setText("Change of permissions on the serial port.");

        final Div divSpan = new Div(span);
        divSpan.addClassNames(LumoUtility.Display.INLINE_GRID,
                LumoUtility.TextOverflow.ELLIPSIS,
                LumoUtility.Width.FULL,
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
        divIcons.setWidth("50%");
        row.add(divSpan, divIcons);
        return row;
    }

}

