package com.esp.espflow.views.readflash;

import com.esp.espflow.data.entity.EspDeviceWithTotalDevices;
import com.esp.espflow.data.mappers.EspDeviceWithTotalDevicesMapper;
import com.esp.espflow.data.service.EsptoolService;
import com.esp.espflow.data.util.ConfirmDialogBuilder;
import com.esp.espflow.data.util.ResponsiveHeaderDiv;
import com.esp.espflow.data.util.console.ConsoleOutPut;
import com.esp.espflow.views.MainLayout;
import com.infraleap.animatecss.Animated;
import com.infraleap.animatecss.Animated.Animation;
import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout.Orientation;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility.*;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin.Left;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin.Right;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

import static com.esp.espflow.data.util.EspFlowConstants.*;

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
    private final IntegerField startAddress = new IntegerField("Start address");
    private final IntegerField endAddress = new IntegerField("Set size address to read");
    private final ToggleButton autoDetectFlashSize = new ToggleButton();
    private final Span spanAutoDetectFlashSize = new Span("Set size address to ALL");
    private final Div divWithPortErrors = new Div();
    /**
     * Console output
     */
    private final ConsoleOutPut consoleOutPut = new ConsoleOutPut();
    private final Span spanTotalDevices = new Span("Total devices:");
    private final Span spanPortFailure = new Span("Port failure: ");
    private final Span spanTotalDevicesValue = new Span();

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

    /**
     * The SplitLayout
     *
     * @return A configured {@link SplitLayout}
     */
    private SplitLayout getSplitLayout() {
        final var splitLayout = new SplitLayout(Orientation.VERTICAL);
        splitLayout.setSizeFull();
        splitLayout.setSplitterPosition(65);
        splitLayout.getStyle().set(OVERFLOW_Y, HIDDEN);
        splitLayout.getStyle().set(OVERFLOW_X, HIDDEN);
        /*
         * Primary section
         */
        final var rightFormForAddress = this.rigthFormForAddress();
        final var divForLeftCarousel = divForLeftCarousel();
        final var horizontalLayoutToPrimarySection = horizontalLayoutToPrimarySection(rightFormForAddress, divForLeftCarousel);
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
     * @param rightFormForAddress The form in the right-hand corner with the textfields for the memory zones
     * @param divForLeftCarousel  The carousel with the scanned scans
     * @return A {@link HorizontalLayout} with the form on the right and the carousel on the left side
     */
    private HorizontalLayout horizontalLayoutToPrimarySection(Div rightFormForAddress, Div divForLeftCarousel) {
        this.progressBar.setVisible(false);
        this.progressBar.setIndeterminate(true);
        this.horizontalLayoutForPrimarySection.setId("div-for-primary");
        this.horizontalLayoutForPrimarySection.setSizeFull();
        this.horizontalLayoutForPrimarySection.add(rightFormForAddress, divForLeftCarousel);
        return horizontalLayoutForPrimarySection;
    }

    @SuppressWarnings("unused")
    private HorizontalLayout rowStepAvatar(final String numberStep,
                                           final String tooltip, final H3 header) {
        final Avatar avatar = new Avatar(numberStep);
        Tooltip.forComponent(avatar).setText(tooltip);
        avatar.setThemeName(AvatarVariant.LUMO_LARGE.getVariantName());
        avatar.addClassName(BOX_SHADOW_VAADIN_BUTTON);
        avatar.getStyle().set("background", "var(--lumo-primary-color)");
        avatar.getStyle().set("color", "var(--lumo-primary-contrast-color)");
        final var row = new HorizontalLayout(avatar, header);
        row.addClassNames(AlignItems.START, JustifyContent.START);
        return row;
    }

    /**
     * This part contains the form for entering the memory addresses to be read.
     *
     * @return A {@link FormLayout}
     */
    private Div rigthFormForAddress() {
        var rowAutoSize = new HorizontalLayout(autoDetectFlashSize, spanAutoDetectFlashSize);
        rowAutoSize.setWidthFull();
        final Div formLayout = new Div(buttonRefreshDevices, startAddress, endAddress, rowAutoSize, progressBar);
        Stream.of(buttonRefreshDevices, startAddress, endAddress, rowAutoSize, progressBar)
                .forEach(items -> {
                    items.addClassName(AlignSelf.BASELINE);
                    items.setWidthFull();
                });
        formLayout.addClassNames(Display.FLEX, FlexDirection.COLUMN, AlignItems.START, JustifyContent.CENTER);

        startAddress.setTooltipText("default size is 0");
        endAddress.setTooltipText("default size is 0, enable button to set to ALL");
        Stream.of(startAddress, endAddress).forEach(textField -> {
            textField.setStepButtonsVisible(true);
            textField.setWidthFull();
            textField.setMin(0);
            textField.setValue(0);
            textField.setPrefixComponent(new Span("0x"));
            textField.setClearButtonVisible(true);
            textField.setValueChangeMode(ValueChangeMode.ON_CHANGE);
        });
        Tooltip.forComponent(autoDetectFlashSize).setText("Set custom flash size to ALL");
        autoDetectFlashSize.setSizeUndefined();
        autoDetectFlashSize.addValueChangeListener(event -> {
            if (event.getValue()) {
                endAddress.setValue(0);
                endAddress.setEnabled(false);
            } else {
                endAddress.setValue(0);
                endAddress.setEnabled(true);
            }
        });

        final Div parent = new Div(formLayout);
        parent.setWidth("50%");
        parent.addClassNames(Display.FLEX, JustifyContent.CENTER, AlignItems.CENTER);

        return parent;
    }

    /**
     * Div for carousel
     *
     * @return A {@link Div}
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
     * @return A  {@link Div}
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
     * @return A {@link HorizontalLayout}
     */
    private HorizontalLayout getFooter() {
        final HorizontalLayout footer = new HorizontalLayout();
        footer.setWidthFull();
        footer.setHeight("40px");
        footer.getStyle().set("border-top", " 1px solid var(--lumo-contrast-10pct)");
        footer.addClassName(BOX_SHADOW_VAADIN_BUTTON);
        footer.setAlignItems(Alignment.CENTER);
        footer.setJustifyContentMode(JustifyContentMode.START);

        final Div divSpanTotalDevices = new Div(this.spanTotalDevices, this.spanTotalDevicesValue);

        this.divWithPortErrors.setVisible(false);

        spanPortFailure.addClassName(Left.SMALL);
        this.divWithPortErrors.add(spanPortFailure);
        divWithPortErrors.getStyle().set("color", "red");
        Stream.of(divSpanTotalDevices, divWithPortErrors)
                .forEach(divs -> {
                    divs.addClassName(Right.MEDIUM);
                    divs.getElement().setAttribute("theme", "badge");
                });

        final Div divForBadges = new Div(divSpanTotalDevices, divWithPortErrors);
        divForBadges.setId("divForBadges");
        divForBadges.setWidthFull();
        divForBadges.addClassName(Left.MEDIUM);
        /*Margin left to span values */
        Stream.of(spanTotalDevicesValue, spanPortFailure)
                .forEach(spans -> spans.addClassName(Left.SMALL));
        footer.add(divForBadges);
        Stream.of(this.spanTotalDevices, this.spanTotalDevicesValue, this.divWithPortErrors)
                .forEach(span -> {
                    span.getStyle().set("font-size", "var(--lumo-font-size-xs)");
                    span.addClassName("row-span-flash-size-footer");
                });
        return footer;
    }


    /**
     * This button triggers the scanning of the microcontrollers and creates the slides, it also updates the footer badges.
     * <p>
     * It is also disabled on the first click preventing the user from clicking and another scan is processed to avoid interfering with the previous one.
     *
     * @param ui
     */
    private void refreshDevices(final UI ui) {
        buttonRefreshDevices.addClassName(BOX_SHADOW_VAADIN_BUTTON);
        buttonRefreshDevices.setDisableOnClick(true);
        buttonRefreshDevices.addClickListener(event -> {
            final EspDevicesCarousel espDevicesCarousel = new EspDevicesCarousel(new ProgressBar());
            this.divCarousel.removeAll();
            this.divCarousel.add(espDevicesCarousel);
            this.showDetectedDevices(ui, espDevicesCarousel);
        });
    }

    /**
     * <p>This method is used to read the micros that are connected to the OS, in case of not being able to read any,
     * a red SPAN will be displayed with the name of the port with the failure, that failure could be, permissions, etc.</p>
     *
     * <p>The reading process is performed in a Scheduler of <strong>boundedElasctic</strong> type in order not to block the UI having
     * faster feedBack from the microcontrollers that are read, which is difficult with synchronous programming.</p>
     *
     * @param ui
     * @param {@link espDevicesCarousel}
     */
    private void showDetectedDevices(final UI ui, final EspDevicesCarousel espDevicesCarousel) {
        this.progressBar.setVisible(true);
        final List<Span> spansList = new CopyOnWriteArrayList<>();
        this.esptoolService.readAllDevices()
                .doOnError(onError -> this.onError(ui, onError))
                .flatMap(item -> this.esptoolService.countAllDevices()
                        .map(count -> EspDeviceWithTotalDevicesMapper.espDeviceWithTotalDevices(item, count)))
                .doOnComplete(() -> {
                    ui.access(() -> {
                        onComplete(spansList, espDevicesCarousel);
                    });
                })
                .subscribe(espDeviceWithTotalDevices -> {
                    ui.access(() -> {
                        this.subscribeThis(spansList, espDeviceWithTotalDevices, espDevicesCarousel, ui);
                    });
                });
    }

    /**
     * Process error, show notification
     *
     * @param ui The UI
     * @param onError CanNotBeReadDeviceException possibly empty ports
     */
    private void onError(final UI ui, Throwable onError) {
        ui.access(() -> {
            this.progressBar.setVisible(false);
            buttonRefreshDevices.setEnabled(true);
            ConfirmDialogBuilder.showWarning("Error with microcontroller " + onError.getMessage());
        });
    }

    /**
     * This piece of code is executed when the reactive stream is completed.
     *
     * @param spansList of badges to be updated in case of port error
     * @param espDevicesCarousel created to be set as visible
     */
    private void onComplete(final List<Span> spansList, final EspDevicesCarousel espDevicesCarousel) {
        this.progressBar.setVisible(false);
        this.buttonRefreshDevices.setEnabled(true);
        espDevicesCarousel.createSlides();
        espDevicesCarousel.setVisible(true);
        if (!spansList.isEmpty()) {
            /* The span is added with the text "Port Failure:" */
            this.divWithPortErrors.add(spanPortFailure);
            spansList.forEach((spanPortFailureValue) -> {
                /*Margin left and red color to span values */
                spanPortFailureValue.addClassName(Left.SMALL);
                spanPortFailureValue.getStyle().set("color", "red");
                this.divWithPortErrors.add(spanPortFailureValue);
                this.divWithPortErrors.setVisible(true);
            });
            ConfirmDialogBuilder.showWarning("Error with microcontroller!");
        }
    }

    /**
     * Simply to refactor something, and not to overload the reactive stream with extra lines.
     *
     * @param spansList
     * @param espDeviceWithTotalDevices
     * @param espDevicesCarousel
     * @param ui
     */
    private void subscribeThis(final List<Span> spansList,
                               EspDeviceWithTotalDevices espDeviceWithTotalDevices,
                               EspDevicesCarousel espDevicesCarousel,
                               final UI ui) {
        final var mac = espDeviceWithTotalDevices.espDeviceInfo().macAddress();
        var espDeviceInfo = espDeviceWithTotalDevices.espDeviceInfo();
        if (Objects.isNull(mac)) {
            final Span spanPortFailureValue = new Span();
            spanPortFailureValue.setText(espDeviceInfo.port());
            spansList.add(spanPortFailureValue);
        }
        this.spanTotalDevicesValue.setText("  " + espDeviceWithTotalDevices.totalDevices());
        if (Objects.nonNull(espDeviceInfo.macAddress())) {
            this.divWithPortErrors.setVisible(false);
            this.divWithPortErrors.removeAll();
            ShowDevices.builder()
                    .withEspDevicesCarousel(espDevicesCarousel)
                    .withEsptoolService(esptoolService)
                    .withEspDeviceInfo(espDeviceInfo)
                    .withConsoleOutStage(consoleOutPut)
                    .withUi(ui)
                    .withStartSizeAddress(this.startAddress)
                    .withCustomFlashSizeAddress(this.endAddress)
                    .withAutoDetectFlashSize(this.autoDetectFlashSize)
                    .make();
        }

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
