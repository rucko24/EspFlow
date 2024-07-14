package com.nodemcuui.tool.views.readflash;

import com.nodemcuui.tool.data.entity.EspDeviceInfo;
import com.nodemcuui.tool.data.entity.EspDeviceWithTotalDevices;
import com.nodemcuui.tool.data.service.EsptoolService;
import com.nodemcuui.tool.data.util.NotificationBuilder;
import com.nodemcuui.tool.data.util.ResponsiveHeaderDiv;
import com.nodemcuui.tool.data.util.console.ConsoleOutPut;
import com.nodemcuui.tool.views.MainLayout;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout.Orientation;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.FlexDirection;
import com.vaadin.flow.theme.lumo.LumoUtility.JustifyContent;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.Objects;
import java.util.stream.Stream;

import static com.nodemcuui.tool.data.util.UiToolConstants.HIDDEN;
import static com.nodemcuui.tool.data.util.UiToolConstants.MARGIN_10_PX;
import static com.nodemcuui.tool.data.util.UiToolConstants.MARGIN_LEFT;
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

    private final EsptoolService esptoolService;
    private final EspDevicesCarousel espDevicesCarousel = new EspDevicesCarousel();

    private final HorizontalLayout contentForPrimarySection = new HorizontalLayout();
    private final ProgressBar progressBar = new ProgressBar();
    private final TextField startAddress = new TextField("Start address");
    private final TextField endAddress = new TextField("End address");
    private final Checkbox autoDetectFlashSize = new Checkbox("Autodetect flash size aka ALL");

    /**
     * Console output
     */
    private final ConsoleOutPut consoleOutPut = new ConsoleOutPut();
    private final HorizontalLayout footer = new HorizontalLayout();

    private final Span spanTotalDevices = new Span("Total devices: ");
    private final Span spanTotalDevicesValue = new Span(" ");

    private final Span spanPortWithError = new Span("Port failure: ");
    private final Span spanPortWithErrorValue = new Span(" ");

    @PostConstruct
    public void init() {
        super.setSizeFull();
        super.getStyle().set("display", "flex");
        super.getStyle().set("flex-direction", "column");
        super.getStyle().set("overflow-x", "hidden");

        final SplitLayout splitLayout = getSplitLayout();
        final var footer = this.getFooter();

        super.add(splitLayout, footer);
    }

    private SplitLayout getSplitLayout() {
        /**
         * Primary section
         */
        final var splitLayout = new SplitLayout(Orientation.VERTICAL);
        splitLayout.setSplitterPosition(60);
        splitLayout.setSizeFull();
        splitLayout.addToPrimary(contentForPrimarySection);
        splitLayout.getStyle().set(OVERFLOW_Y, HIDDEN);

        this.progressBar.setVisible(false);
        this.progressBar.setIndeterminate(true);
        //this.contentForPrimarySection.setSpacing(false);
        this.contentForPrimarySection.setId("div-for-primary");
        this.contentForPrimarySection.setSizeFull();

        startAddress.setTooltipText("Start address");
        endAddress.setTooltipText("Size address to read");
        autoDetectFlashSize.setTooltipText("ALL");
        autoDetectFlashSize.addValueChangeListener(event -> {
            if(event.getValue()) {
                endAddress.clear();
                endAddress.setEnabled(false);
            } else {
                endAddress.clear();
                endAddress.setEnabled(true);
            }
        });
        Stream.of(startAddress, endAddress).forEach(textField -> {
            textField.setPrefixComponent(new Span("0x"));
            textField.setClearButtonVisible(true);
        });
        final VerticalLayout memoryControls = new VerticalLayout(startAddress, endAddress, autoDetectFlashSize, progressBar);
        memoryControls.setWidth("50%");
        memoryControls.setJustifyContentMode(JustifyContentMode.CENTER);
        memoryControls.setAlignSelf(FlexComponent.Alignment.CENTER, endAddress, startAddress, autoDetectFlashSize);

        Div divCarousel = new Div(espDevicesCarousel);
        divCarousel.setId("div-carousel");
        divCarousel.setWidth("50%");
        divCarousel.addClassNames(Padding.LARGE, AlignItems.CENTER, JustifyContent.CENTER);

        this.contentForPrimarySection.add(memoryControls, divCarousel);

        //consoleOutPut.removeClassName(Bottom.SMALL);
        //consoleOutPut.getStyle().set(OVERFLOW_Y, "unset");

        /**
         * Secondary section
         */
        final var divRowToSecondary = new Div(consoleOutPut);
        divRowToSecondary.addClassNames(Display.FLEX, FlexDirection.ROW);
        divRowToSecondary.getStyle().set(OVERFLOW_Y, HIDDEN);
        divRowToSecondary.getStyle().set("background", "linear-gradient(var(--lumo-shade-5pct), var(--lumo-shade-5pct))");
        splitLayout.addToSecondary(divRowToSecondary);

        splitLayout.getStyle().set(OVERFLOW_X, HIDDEN);
        splitLayout.getPrimaryComponent().getElement().getStyle().set(OVERFLOW_X, HIDDEN);
        splitLayout.getSecondaryComponent().getElement().getStyle().set(OVERFLOW_X, HIDDEN);

        return splitLayout;
    }

    /**
     * @return HorizontalLayout
     */
    private HorizontalLayout getFooter() {
        Stream.of(this.spanPortWithError, this.spanPortWithErrorValue).forEach(spanPort -> {
            spanPort.setVisible(false);
            spanPort.getStyle().set("color", "red");
        });
        final Div divSpanTotalDevices = new Div(this.spanTotalDevices, this.spanTotalDevicesValue);
        final Div divWithPortErrors = new Div(this.spanPortWithError, this.spanPortWithErrorValue);
        divSpanTotalDevices.getElement().setAttribute("theme", "badge");
        divWithPortErrors.getElement().setAttribute("theme", "badge");
//        final Div div2 = new Div(this.decimalSize);
//        final Div div3 = new Div(this.hexSize);
        footer.setWidthFull();
        footer.setHeight("40px");
        footer.getStyle().set("border-top", " 1px solid var(--lumo-contrast-10pct)");
        footer.getStyle().set("box-shadow", "0 2px 1px -1px rgba(0, 0, 0, .2), 0 1px 1px 0 rgba(0, 0, 0, .14), 0 1px 3px 0 rgba(0, 0, 0, .12)");
        footer.setAlignItems(Alignment.CENTER);
        footer.setJustifyContentMode(JustifyContentMode.START);

        final Div divForBadges = new Div(divSpanTotalDevices, divWithPortErrors);
        divForBadges.setId("divForBadges");
        divForBadges.setWidthFull();
        divForBadges.getStyle().set(MARGIN_LEFT, MARGIN_10_PX);

        footer.add(divForBadges);
        Stream.of(this.spanTotalDevices, this.spanTotalDevicesValue, this.spanPortWithError, this.spanPortWithErrorValue)
                .forEach(span -> {
                    span.getStyle().set("font-size", "var(--lumo-font-size-xs)");
                    span.addClassName("row-span-flash-size-footer");
                });
        return footer;
    }

    private void showDetectedDevices(final UI ui) {

        this.progressBar.setVisible(true);
        this.esptoolService.readAllDevices()
                .doOnError(error -> {
                    ui.access(() -> {
                        this.progressBar.setVisible(false);
                        log.info("Error: {}", error);
                        NotificationBuilder.builder()
                                .withText("Error reading the microcontroller " + error)
                                .withPosition(Position.MIDDLE)
                                .withDuration(3000)
                                .withIcon(VaadinIcon.WARNING)
                                .withThemeVariant(NotificationVariant.LUMO_ERROR)
                                .make();
                    });
                })
                .doOnComplete(() -> {
                    ui.access(() -> {
                        this.progressBar.setVisible(false);
                        espDevicesCarousel.createSlides();
                        this.espDevicesCarousel.setVisible(true);
                    });
                })
                .flatMap(item -> this.esptoolService.countAllDevices()
                        .map(count -> this.espDeviceWithTotalDevices(item, count)))
                .subscribe(espDeviceWithTotalDevices -> {
                    ui.access(() -> {
                        var espDeviceInfo = espDeviceWithTotalDevices.espDeviceInfo();
                        this.spanTotalDevicesValue.setText("  " + espDeviceWithTotalDevices.totalDevices());
                        if (Objects.isNull(espDeviceInfo.macAddress())) {
                            spanPortWithError.setVisible(true);
                            spanPortWithErrorValue.setVisible(true);
                            this.spanPortWithErrorValue.setText(" " + espDeviceInfo.port());
                        }
                        //this.spanPortWithErrorValue.setText();
                        ShowDevices.builder()
                                .withEspDevicesCarousel(espDevicesCarousel)
                                .withEsptoolService(esptoolService)
                                .withEspDeviceInfo(espDeviceInfo)
                                .withConsoleOutStage(consoleOutPut)
                                .withUi(ui)
                                .withStartSizeAddress(this.startAddress)
                                .withCustomFlashSizeAddress(this.endAddress)
                                .withAutoDetectFlashSize(this.autoDetectFlashSize)
                                .createSlides()
                                .make();
                    });
                });
    }

    private EspDeviceWithTotalDevices espDeviceWithTotalDevices(EspDeviceInfo espDeviceInfo, Long totalDevices) {
        return EspDeviceWithTotalDevices.builder()
                .totalDevices(totalDevices)
                .espDeviceInfo(espDeviceInfo)
                .build();
    }


    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        if (attachEvent.isInitialAttach()) {
            super.onAttach(attachEvent);
            final UI ui = attachEvent.getUI();
            this.showDetectedDevices(ui);
        }
    }
}
