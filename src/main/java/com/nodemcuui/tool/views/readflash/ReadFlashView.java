package com.nodemcuui.tool.views.readflash;

import com.nodemcuui.tool.data.service.ComPortService;
import com.nodemcuui.tool.data.service.CommandService;
import com.nodemcuui.tool.data.service.EsptoolService;
import com.nodemcuui.tool.data.util.CommandNotFoundException;
import com.nodemcuui.tool.data.util.ResponsiveHeaderDiv;
import com.nodemcuui.tool.data.util.console.ConsoleCommandOutPutArea;
import com.nodemcuui.tool.views.MainLayout;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout.Orientation;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.JustifyContent;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin.Bottom;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static com.nodemcuui.tool.data.util.UiToolConstants.FLASH_SIZE;
import static com.nodemcuui.tool.data.util.UiToolConstants.HIDDEN;
import static com.nodemcuui.tool.data.util.UiToolConstants.MAC;
import static com.nodemcuui.tool.data.util.UiToolConstants.NOT_FOUND;
import static com.nodemcuui.tool.data.util.UiToolConstants.OVERFLOW_X;
import static com.nodemcuui.tool.data.util.UiToolConstants.OVERFLOW_Y;

/**
 * @author rubn
 */
@Log4j2
@UIScope
@SpringComponent
@PageTitle("Read flash")
@Route(value = "read-flash", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@RequiredArgsConstructor
public class ReadFlashView extends Div implements ResponsiveHeaderDiv {

    private final CommandService commandService;
    private final ComPortService comPortService;
    private final EsptoolService esptoolService;

    private final VerticalLayout content = new VerticalLayout();
    private final EspDevicesCarousel espDevicesCarousel = new EspDevicesCarousel();

    /**
     * Console output area
     */
    private final ConsoleCommandOutPutArea consoleCommandOutPutArea = new ConsoleCommandOutPutArea();
    private final HorizontalLayout footer = new HorizontalLayout();
    private final Span flashSize = new Span("Flash size: reading...");
    private final Span decimalSize = new Span("Decimal: reading...");
    private final Span hexSize = new Span("Hex: reading...");

    @PostConstruct
    public void init() {
        super.setSizeFull();
        super.getStyle().set("display", "flex");
        super.getStyle().set("flex-direction", "column");

        final SplitLayout splitLayout = getSplitLayout();
        super.add(splitLayout);
    }

    private SplitLayout getSplitLayout() {
        this.content.setSpacing(false);
        this.content.add(espDevicesCarousel);
        this.content.addClassNames(AlignItems.CENTER, JustifyContent.CENTER);

        final var splitLayout = new SplitLayout(Orientation.VERTICAL);

        splitLayout.setSplitterPosition(60);
        splitLayout.setSizeFull();
        splitLayout.addToPrimary(content);
        splitLayout.getStyle().set(OVERFLOW_Y, HIDDEN);

        final var footer = this.getFooter();
        consoleCommandOutPutArea.removeClassName(Bottom.SMALL);
        consoleCommandOutPutArea.getStyle().set(OVERFLOW_Y, "unset");
        final var verticalLayoutToSecondary = new VerticalLayout(consoleCommandOutPutArea, footer);
        verticalLayoutToSecondary.setFlexGrow(1, consoleCommandOutPutArea);

        verticalLayoutToSecondary.getStyle().set(OVERFLOW_Y, HIDDEN);
        verticalLayoutToSecondary.getStyle().set("background", "linear-gradient(var(--lumo-shade-5pct), var(--lumo-shade-5pct))");
        splitLayout.addToSecondary(verticalLayoutToSecondary);
        splitLayout.setSplitterPosition(60);

        splitLayout.getStyle().set(OVERFLOW_X, HIDDEN);
        splitLayout.getPrimaryComponent().getElement().getStyle().set(OVERFLOW_X, HIDDEN);
        splitLayout.getSecondaryComponent().getElement().getStyle().set(OVERFLOW_X, HIDDEN);

        return splitLayout;
    }

    /**
     * @return HorizontalLayout
     */
    private HorizontalLayout getFooter() {
        final Div div = new Div(this.flashSize);
        final Div div2 = new Div(this.decimalSize);
        final Div div3 = new Div(this.hexSize);

        footer.setWidthFull();
        footer.setAlignItems(Alignment.CENTER);
        footer.setJustifyContentMode(JustifyContentMode.AROUND);
        footer.add(div, div2, div3);

        return footer;
    }

    @SneakyThrows
    private void readFlashId(final UI ui) {
        this.esptoolService.readFlashId()
                .subscribe((ConcurrentHashMap<String, String> espInfo) -> {
                    getUI().ifPresent(ui2 -> ui2.access(() -> {
                        final String flashSize = espInfo.get(FLASH_SIZE);
                        if (flashSize != null) {
                            this.flashSize.setText("Flash size: " + flashSize);

                            final String toDec = flashSize.split("MB")[0].trim();
                            final String decSize = String.valueOf(Integer.parseInt(toDec) * 1048576);
                            this.decimalSize.setText("Decimal: " + decSize);

                            final String hexSize = Integer.toHexString(Integer.parseInt(decSize));
                            this.hexSize.setText("Hex: 0x" + hexSize);

                        }
                        final String mac = espInfo.get(MAC);

                        if(mac != null) {
                            Notification.show("MAC: " + mac);
                        }
                    }));
                });

        Stream.of(flashSize, this.decimalSize, this.hexSize)
                .forEach(span -> {
                    span.getStyle().set("font-size", "var(--lumo-font-size-xs)");
                    span.addClassName("row-span-flash-size-footer");
                });

    }

    /**
     *
     * PORT /dev/ttyUSB1, COM
     *
     * esptool.py --port /dev/ttyUSB1 read_flash 0 ALL esp8266-backupflash.bin
     *
     * @param ui
     */
    private void readFlash(final UI ui) {

    }


    @Override
    protected void onDetach(DetachEvent detachEvent) {

    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        if (attachEvent.isInitialAttach()) {
            final UI ui = attachEvent.getUI();
            this.readFlashId(ui);

        }
    }
}
