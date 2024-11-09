package com.esp.espflow.views.flashesp;

import com.esp.espflow.util.svgfactory.SvgFactory;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility;
import lombok.extern.log4j.Log4j2;

import static com.esp.espflow.util.EspFlowConstants.FREE_BSD_ICON;
import static com.esp.espflow.util.EspFlowConstants.LINUX_ICON;
import static com.esp.espflow.util.EspFlowConstants.MACOS_ICON;
import static com.esp.espflow.util.EspFlowConstants.OK;
import static com.esp.espflow.util.EspFlowConstants.WINDOWS_ICON;

/**
 * @author rub`n
 */
@Log4j2
@UIScope
@SpringComponent
public class InitialInformationFlashEspViewDialog extends Dialog {

    private static final String TARGET_BLANK = "_blank";
    private static final String INNER_HTML = "innerHTML";

    private final Div content = new Div();
    private final H3 overView = new H3("Overview");
    private final H3 someFeatures = new H3("Some of the features:");

    public InitialInformationFlashEspViewDialog() {
        super.setModal(true);

        content.addClassNames(LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN);
        content.setWidth("500px");

        var paragraphOverView = paragraphOverView();
        var listItemsWithFeatures = createListItemsWithFeatures();

        content.add(overView, paragraphOverView, someFeatures, listItemsWithFeatures);
        super.add(content);

        final Button buttonOk = new Button(OK, (event -> super.close()));
        buttonOk.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        super.getFooter().add(buttonOk);

        super.open();
    }

    private Paragraph paragraphOverView() {
        final Paragraph paragraphOverView = new Paragraph();
        paragraphOverView.getElement().setProperty(INNER_HTML, "This view allows us to scan the available ports of" +
                " the system, see information of each <strong>esp8266</strong>, " +
                "<strong>esp32</strong>, write the firmware in each one of them. " +
                "Each command executed will write the result of the operation to the output console");
        return paragraphOverView;
    }

    private UnorderedList createListItemsWithFeatures() {
        final UnorderedList unorderedList = new UnorderedList();

        final ListItem flashIdItem = new ListItem(createFlashIdAnchor());
        final ListItem writeIdItem = new ListItem(createWriteFlashAnchor());
        final ListItem esptoolPyVersionItem = new ListItem("Show the version of esptool");
        final ListItem changePermissionsOnPort = new ListItem(createChangePermissionsOnPortItem());
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
        final Span span = new Span("Change of permissions on the serial port");
        Tooltip.forComponent(span).setText("Change of permissions on the serial port");

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

