package com.nodemcuui.tool.views.readflash;

import com.infraleap.animatecss.Animated;
import com.infraleap.animatecss.Animated.Animation;
import com.nodemcuui.tool.data.mappers.EspDeviceWithTotalDevicesMapper;
import com.nodemcuui.tool.data.service.EsptoolService;
import com.nodemcuui.tool.data.util.NotificationBuilder;
import com.nodemcuui.tool.data.util.ResponsiveHeaderDiv;
import com.nodemcuui.tool.data.util.console.ConsoleOutPut;
import com.nodemcuui.tool.views.MainLayout;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout.Orientation;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignSelf;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.FlexDirection;
import com.vaadin.flow.theme.lumo.LumoUtility.JustifyContent;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin.Left;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin.Right;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.Objects;
import java.util.stream.Stream;

import static com.nodemcuui.tool.data.util.UiToolConstants.HIDDEN;
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
    //With default espcarousel div
    private final ProgressBar progressBar = new ProgressBar();
    private final Div divCarousel = new Div(new EspDevicesCarousel(new ProgressBar()));
    private final HorizontalLayout horizontalLayoutForPrimarySection = new HorizontalLayout();
    private final Button buttonRefreshDevices = new Button("Refresh devices", VaadinIcon.REFRESH.create());
    private final TextField startAddress = new TextField("Start address");
    private final TextField endAddress = new TextField("End address");
    private final Checkbox autoDetectFlashSize = new Checkbox("Autodetect flash size aka ALL");
    private final Div divWithPortErrors = new Div();

    /**
     * Console output
     */
    private final ConsoleOutPut consoleOutPut = new ConsoleOutPut();
    private final Span spanTotalDevices = new Span("Total devices:");
    private final Span spanTotalDevicesValue = new Span();

    private final Span spanPortWithError = new Span("Port failure:");
    private final Span spanPortWithErrorValue = new Span();

    @PostConstruct
    public void init() {
        super.setSizeFull();
        super.getStyle().set("display", "flex");
        super.getStyle().set("flex-direction", "column");
        super.getStyle().set("overflow-x", "hidden");

        final SplitLayout splitLayout = getSplitLayout();
        final var footer = this.getFooter();

        super.add(splitLayout, footer);
        Animated.animate(splitLayout, Animation.FADE_IN);

    }

    private SplitLayout getSplitLayout() {
        final var splitLayout = new SplitLayout(Orientation.VERTICAL);
        splitLayout.setSizeFull();
        splitLayout.setSplitterPosition(60);
        splitLayout.getStyle().set(OVERFLOW_Y, HIDDEN);
        splitLayout.getStyle().set(OVERFLOW_X, HIDDEN);
        /*
         * Primary section
         */
        final var rigthFormForAddress = this.rigthFormForAddress();
        final var divForLeftCarousel = divForLeftCarousel();
        final var horizontalLayoutToPrimarySection = horizontalLayoutToPrimarySection(rigthFormForAddress, divForLeftCarousel);
        splitLayout.addToPrimary(horizontalLayoutToPrimarySection);
        /*
         * Secondary section
         */
        final var divForConsoleOutput = divForConsoleOutput();
        splitLayout.addToSecondary(divForConsoleOutput);
        /*
         * Invoked after to prevent NPE
         */
        splitLayout.getPrimaryComponent().getElement().getStyle().set(OVERFLOW_X, HIDDEN);
        splitLayout.getSecondaryComponent().getElement().getStyle().set(OVERFLOW_X, HIDDEN);
        return splitLayout;
    }

    /**
     * @param rigthFormForAddress
     * @param divForLeftCarousel
     * @return {@link HorizontalLayout}
     */
    private HorizontalLayout horizontalLayoutToPrimarySection(Div rigthFormForAddress, Div divForLeftCarousel) {
        this.progressBar.setVisible(false);
        this.progressBar.setIndeterminate(true);
        this.horizontalLayoutForPrimarySection.setId("div-for-primary");
        this.horizontalLayoutForPrimarySection.setSizeFull();
        this.horizontalLayoutForPrimarySection.add(rigthFormForAddress, divForLeftCarousel);
        return horizontalLayoutForPrimarySection;
    }

    /**
     * This part contains the form for entering the memory addresses to be read.
     *
     * @return {@link FormLayout}
     */
    private Div rigthFormForAddress() {
        final Div formLayout = new Div(buttonRefreshDevices, startAddress, endAddress, autoDetectFlashSize, progressBar);
        Stream.of(buttonRefreshDevices, startAddress, endAddress, autoDetectFlashSize, progressBar)
                        .forEach(items -> items.addClassName(AlignSelf.BASELINE));
        formLayout.addClassNames(Display.FLEX, FlexDirection.COLUMN, AlignItems.START, JustifyContent.CENTER);

        startAddress.setTooltipText("Start address");
        endAddress.setTooltipText("Size address to read");
        autoDetectFlashSize.setTooltipText("ALL");
        autoDetectFlashSize.addValueChangeListener(event -> {
            if (event.getValue()) {
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

        final Div parent = new Div(formLayout);
        parent.setWidth("50%");
        parent.addClassNames(Display.FLEX,JustifyContent.CENTER, AlignItems.CENTER);

        return parent;
    }

    /**
     * Div for carousel
     *
     * @return {@link Div}
     */
    private Div divForLeftCarousel() {
        divCarousel.setId("div-carousel");
        divCarousel.setWidth("50%");
        divCarousel.addClassNames(Padding.LARGE, AlignItems.CENTER, JustifyContent.CENTER);
        return divCarousel;
    }

    /**
     * Div for console output
     *
     * @return {@link Div}
     */
    private Div divForConsoleOutput() {
        final var divRowToSecondary = new Div(consoleOutPut);
        divRowToSecondary.addClassNames(Display.FLEX, FlexDirection.ROW);
        divRowToSecondary.getStyle().set(OVERFLOW_Y, HIDDEN);
        divRowToSecondary.getStyle().set("background", "linear-gradient(var(--lumo-shade-5pct), var(--lumo-shade-5pct))");
        return divRowToSecondary;
    }

    /**
     * The Footer layout
     *
     * @return HorizontalLayout
     */
    private HorizontalLayout getFooter() {
        final HorizontalLayout footer = new HorizontalLayout();
        /*Margin left to span values */
        Stream.of(this.spanPortWithErrorValue, this.spanTotalDevicesValue)
                .forEach(spanValues -> spanValues.addClassName(Left.SMALL));
        /*Margin left to span values */
        Stream.of(this.spanPortWithError, this.spanPortWithErrorValue).forEach(spanPort -> {
            spanPort.getStyle().set("color", "red");
        });
        final Div divSpanTotalDevices = new Div(this.spanTotalDevices, this.spanTotalDevicesValue);
        this.divWithPortErrors.add(this.spanPortWithError, this.spanPortWithErrorValue);
        this.divWithPortErrors.setVisible(false);
        Stream.of(divSpanTotalDevices, divWithPortErrors)
                .forEach(divs -> {
                    divs.addClassName(Right.MEDIUM);
                    divs.getElement().setAttribute("theme", "badge");
                });

        footer.setWidthFull();
        footer.setHeight("40px");
        footer.getStyle().set("border-top", " 1px solid var(--lumo-contrast-10pct)");
        footer.getStyle().set("box-shadow", "0 2px 1px -1px rgba(0, 0, 0, .2), 0 1px 1px 0 rgba(0, 0, 0, .14), 0 1px 3px 0 rgba(0, 0, 0, .12)");
        footer.setAlignItems(Alignment.CENTER);
        footer.setJustifyContentMode(JustifyContentMode.START);

        final Div divForBadges = new Div(divSpanTotalDevices, divWithPortErrors);
        divForBadges.setId("divForBadges");
        divForBadges.setWidthFull();
        divForBadges.addClassName(Left.MEDIUM);

        footer.add(divForBadges);
        Stream.of(this.spanTotalDevices, this.spanTotalDevicesValue, this.spanPortWithError, this.spanPortWithErrorValue)
                .forEach(span -> {
                    span.getStyle().set("font-size", "var(--lumo-font-size-xs)");
                    span.addClassName("row-span-flash-size-footer");
                });
        return footer;
    }

    /**
     * This method is used to read the micros that are connected to the OS, in case of not being able to read any,
     * a red SPAN will be displayed with the name of the port with the failure, that failure could be, permissions, etc.
     *
     * @param ui
     * @param espDevicesCarousel
     */
    private void showDetectedDevices(final UI ui, final EspDevicesCarousel espDevicesCarousel) {
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
                        espDevicesCarousel.setVisible(true);
                    });
                })
                .flatMap(item -> this.esptoolService.countAllDevices()
                        .map(count -> EspDeviceWithTotalDevicesMapper.espDeviceWithTotalDevices(item, count)))
                .subscribe(espDeviceWithTotalDevices -> {
                    ui.access(() -> {
                        var espDeviceInfo = espDeviceWithTotalDevices.espDeviceInfo();
                        this.spanTotalDevicesValue.setText("  " + espDeviceWithTotalDevices.totalDevices());
                        if (Objects.isNull(espDeviceInfo.macAddress())) {
                            this.divWithPortErrors.setVisible(true);
                            this.spanPortWithErrorValue.setText("  " + espDeviceInfo.port());
                        }
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

    /**
     *
     * @param ui
     */
    private void refreshDevices(final UI ui) {
        buttonRefreshDevices.getStyle().set("box-shadow", "0 2px 1px -1px rgba(0, 0, 0, .2), 0 1px 1px 0 rgba(0, 0, 0, .14), 0 1px 3px 0 rgba(0, 0, 0, .12)");
        buttonRefreshDevices.addClickListener(event -> {
            final EspDevicesCarousel espDevicesNew = new EspDevicesCarousel(new ProgressBar());
            this.divCarousel.removeAll();
            this.divCarousel.add(espDevicesNew);
            this.showDetectedDevices(ui, espDevicesNew);
        });
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
            this.refreshDevices(ui);
        }
    }
}
