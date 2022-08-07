package com.nodemcutools.application.views.readwriteflash;

import com.nodemcutools.application.components.console.ConsoleCommandOutPutArea;
import com.nodemcutools.application.data.service.ComPortService;
import com.nodemcutools.application.data.service.CommandService;
import com.nodemcutools.application.data.service.EsptoolService;
import com.nodemcutools.application.data.util.ResponsiveHeaderDiv;
import com.nodemcutools.application.views.MainLayout;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout.Orientation;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static com.nodemcutools.application.data.util.UiToolConstants.FLASH_SIZE;
import static com.nodemcutools.application.data.util.UiToolConstants.HIDDEN;
import static com.nodemcutools.application.data.util.UiToolConstants.OVERFLOW_X;
import static com.nodemcutools.application.data.util.UiToolConstants.OVERFLOW_Y;

/**
 * @author rubn
 */
@Log4j2
@UIScope
@SpringComponent
@PageTitle("Read write flash")
@Route(value = "read-write-flash", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@RequiredArgsConstructor
public class ReadWriteFlashView extends Div implements ResponsiveHeaderDiv {

    private final CommandService commandService;
    private final ComPortService comPortService;
    private final EsptoolService esptoolService;

    private final VerticalLayout content = new VerticalLayout();
    private final Tab readTab = new Tab(VaadinIcon.ARROW_CIRCLE_DOWN.create(), new Span("Read flash"));
    private final Tab writeTab = new Tab(VaadinIcon.ARROW_CIRCLE_UP.create(), new Span("Write flash"));
    private final Tabs tabs = new Tabs(readTab, writeTab);

    /**
     * Console output area
     */
    private TextArea outPutArea;
    private final ConsoleCommandOutPutArea consoleCommandOutPutArea = new ConsoleCommandOutPutArea();
    private final HorizontalLayout footer = new HorizontalLayout();
    private final Span flashSize = new Span("Flash size: reading...");
    private final Span decimalSize = new Span("Decimal: reading...");
    private final Span hexSize = new Span("Hex: reading...");

    @PostConstruct
    public void init() {
        super.setSizeFull();

        tabs.addSelectedChangeListener(event -> setContent(event.getSelectedTab()));
        this.content.setSpacing(false);
        this.setContent(tabs.getSelectedTab());
        super.getStyle().set("display", "flex");
        super.getStyle().set("flex-direction", "column");

        final SplitLayout splitLayout = getSplitLayout();
        super.add(splitLayout);
    }

    private SplitLayout getSplitLayout() {
        final var splitLayout = new SplitLayout(Orientation.VERTICAL);

        splitLayout.setSplitterPosition(60);
        splitLayout.setSizeFull();
        splitLayout.addToPrimary(tabs, content);
        splitLayout.getStyle().set(OVERFLOW_Y, HIDDEN);
        this.outPutArea = consoleCommandOutPutArea.getTextArea();

        final var footer = this.getFooter();
        final var verticalLayout = new VerticalLayout(consoleCommandOutPutArea, footer);
        verticalLayout.setFlexGrow(1, consoleCommandOutPutArea);

        verticalLayout.getStyle().set("overflow-y", "hidden");
        verticalLayout.getStyle().set("background", "linear-gradient(var(--lumo-shade-5pct), var(--lumo-shade-5pct))");
        splitLayout.addToSecondary(verticalLayout);
        splitLayout.getSecondaryComponent().getElement().getStyle().set("flex-grow", "1");

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

    private void setContent(final Tab tab) {
        content.removeAll();
        if (tab.equals(readTab)) {
            content.add(new Paragraph("read flash"));
        } else if (tab.equals(writeTab)) {
            content.add(new Paragraph("write flash"));
        } else {

        }
    }

    @SneakyThrows
    private void readFlashId(final UI ui) {
        this.esptoolService.readFlashId()
                .subscribe((ConcurrentHashMap<String, String> espInfo) -> {
                    ui.access(() -> {
                        final String flashSize = espInfo.get(FLASH_SIZE);
                        if (flashSize != null) {
                            this.flashSize.setText("Flash size: " + flashSize);

                            final String toDec = flashSize.split("MB")[0].trim();
                            final String decSize = String.valueOf(Integer.parseInt(toDec) * 1048576);
                            this.decimalSize.setText("Decimal: " + decSize);

                            final String hexSize = Integer.toHexString(Integer.parseInt(decSize));
                            this.hexSize.setText("Hex: 0x" + hexSize);

                            final String mac = espInfo.get("MAC");
                            Notification.show("MAC: " + mac);
                        }
                    });
                });

        Stream.of(flashSize, this.decimalSize, this.hexSize)
                .forEach(span -> {
                    span.getStyle().set("font-size", "var(--lumo-font-size-xs)");
                    span.addClassName("row-span-flash-size-footer");
                });

    }

    private void readFlash(final UI ui) {

    }

    private void writeFlash(final UI ui) {
        //
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {

    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        if (attachEvent.isInitialAttach()) {
            final UI ui = attachEvent.getUI();
            this.readFlashId(ui);
            this.writeFlash(ui);
        }
    }
}
