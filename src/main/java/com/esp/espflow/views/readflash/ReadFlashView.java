package com.esp.espflow.views.readflash;

import com.esp.espflow.entity.EspDeviceInfo;
import com.esp.espflow.entity.EspDeviceWithTotalDevices;
import com.esp.espflow.enums.BaudRatesEnum;
import com.esp.espflow.enums.RefreshDevicesEvent;
import com.esp.espflow.mappers.EspDeviceWithTotalDevicesMapper;
import com.esp.espflow.service.EsptoolPathService;
import com.esp.espflow.service.EsptoolService;
import com.esp.espflow.service.downloader.FlashDownloadButtonService;
import com.esp.espflow.util.ConfirmDialogBuilder;
import com.esp.espflow.util.ResponsiveHeaderDiv;
import com.esp.espflow.util.broadcaster.BroadcasterRefreshDevicesButton;
import com.esp.espflow.util.console.OutPutConsole;
import com.esp.espflow.util.svgfactory.SvgFactory;
import com.esp.espflow.views.MainLayout;
import com.esp.espflow.views.Sidebar;
import com.esp.espflow.views.flashesp.ChangeSerialPortPermissionDialog;
import com.infraleap.animatecss.Animated;
import com.infraleap.animatecss.Animated.Animation;
import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.UIDetachedException;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout.Orientation;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.Lumo;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignSelf;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.FlexDirection;
import com.vaadin.flow.theme.lumo.LumoUtility.JustifyContent;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin.Left;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin.Right;
import com.vaadin.flow.theme.lumo.LumoUtility.Overflow;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.esp.espflow.util.EspFlowConstants.BOX_SHADOW_VAADIN_BUTTON;
import static com.esp.espflow.util.EspFlowConstants.HIDDEN;
import static com.esp.espflow.util.EspFlowConstants.LOADING;
import static com.esp.espflow.util.EspFlowConstants.NO_DEVICES_SHOWN;
import static com.esp.espflow.util.EspFlowConstants.OVERFLOW_X;
import static com.esp.espflow.util.EspFlowConstants.OVERFLOW_Y;
import static com.esp.espflow.util.EspFlowConstants.PORT_FAILURE;

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
public class ReadFlashView extends Div implements ResponsiveHeaderDiv, BeforeEnterObserver {

    private final FlashDownloadButtonService flashDownloadButtonService;
    private final EsptoolService esptoolService;
    private final EsptoolPathService esptoolPathService;
    private final ProgressBar leftPrimarySectionProgressBar = new ProgressBar();
    //With default espcarousel div
    private final Div divCarousel = new Div(new EspDevicesCarousel(new ProgressBar(), NO_DEVICES_SHOWN));
    private final HorizontalLayout horizontalLayoutForPrimarySection = new HorizontalLayout();
    private final Button buttonRefreshDevices = new Button("Refresh devices", VaadinIcon.REFRESH.create());
    private final IntegerField startAddress = new IntegerField("Start address");
    private final IntegerField endAddress = new IntegerField("Set size address to read");
    private final ToggleButton autoDetectFlashSize = new ToggleButton();
    private final ComboBox<BaudRatesEnum> baudRatesComboBox = new ComboBox<>("Baud rate");
    private final Span spanAutoDetectFlashSize = new Span("Set size address to ALL");
    private final Div divWithPortErrors = new Div();
    /**
     * Console output
     */
    private final OutPutConsole outPutConsole = new OutPutConsole();
    private final Span spanTotalDevices = new Span("Total devices:");
    private final Span spanPortFailure = new Span(PORT_FAILURE);
    private final Span spanTotalDevicesValue = new Span();

    /**
     * Registration for button
     */
    private Registration broadcasterRefreshButton;
    /**
     * Change port permission
     */
    private final ChangeSerialPortPermissionDialog changeSerialPortPermissionDialog;
    private final Set<Span> spansList = new CopyOnWriteArraySet<>();
    /*
     * Publisher for MessageListItem
     */
    private final Sinks.Many<MessageListItem> publishMessageListItem;
    /*
     * For Custom colors
     */
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    /*
     * Show initial information
     */
    private final InitialInformationReadFlashViewDialog initialInformationReadFlashViewDialog;

    @PostConstruct
    public void init() {
        super.setSizeFull();
        super.addClassNames(Display.FLEX, FlexDirection.COLUMN);
        super.getStyle().set(OVERFLOW_X, HIDDEN);

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
        splitLayout.getPrimaryComponent().getElement().getStyle().set(
                "border-bottom", "1px solid var(--lumo-contrast-10pct)");
        splitLayout.getSecondaryComponent().getElement().getStyle().set(
                "background", "linear-gradient(var(--lumo-shade-5pct), var(--lumo-shade-5pct))");

        splitLayout.getSecondaryComponent().getElement().getStyle().set("scrollbar-width", "thin");
        splitLayout.getSecondaryComponent().getElement().getStyle().set("scrollbar-color", "#3b3b3b #202020");

        return splitLayout;
    }

    /**
     * @param rightFormForAddress The form in the right-hand corner with the textfields for the memory zones
     * @param divForLeftCarousel  The carousel with the scanned scans
     * @return A {@link HorizontalLayout} with the form on the right and the carousel on the left side
     */
    private HorizontalLayout horizontalLayoutToPrimarySection(Div rightFormForAddress, Div divForLeftCarousel) {
        this.leftPrimarySectionProgressBar.setVisible(false);
        this.leftPrimarySectionProgressBar.setIndeterminate(true);
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
        final Div formLayout = new Div(buttonRefreshDevices, startAddress, endAddress, baudRatesComboBox, rowAutoSize, leftPrimarySectionProgressBar);
        Stream.of(buttonRefreshDevices, startAddress, endAddress, rowAutoSize, baudRatesComboBox, leftPrimarySectionProgressBar)
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
                endAddress.setEnabled(true);
            }
        });
        this.baudRatesComboBox.setTooltipText("Serial port baud rate default 115200");
        this.baudRatesComboBox.setItems(BaudRatesEnum.values());
        this.baudRatesComboBox.setValue(BaudRatesEnum.BAUD_RATE_115200);
        this.baudRatesComboBox.setItemLabelGenerator(BaudRatesEnum::toString);
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
        final var divRowToSecondary = new Div();
        divRowToSecondary.addClassNames(Display.FLEX, FlexDirection.ROW);
        divRowToSecondary.getStyle().set(OVERFLOW_Y, HIDDEN);
        divRowToSecondary.getStyle().set("background", "linear-gradient(var(--lumo-shade-5pct), var(--lumo-shade-5pct))");

        outPutConsole.getStyle().set("overflow-x", "hidden");
        outPutConsole.getDivTextArea().removeClassNames(Left.LARGE, Right.LARGE);
        outPutConsole.getButtonClear().addClassName(BOX_SHADOW_VAADIN_BUTTON);
        outPutConsole.getButtonDownScroll().addClassName(BOX_SHADOW_VAADIN_BUTTON);

        Div divColumnItems = new Div(outPutConsole.getButtonDownScroll(),
                outPutConsole.getButtonClear());
        divColumnItems.setId("divColumnItems");

        divColumnItems.addClassNames(
                Display.FLEX,
                FlexDirection.COLUMN,
                JustifyContent.END,
                Overflow.HIDDEN);

        divColumnItems.getStyle().set("margin-left", "10px");
        divColumnItems.getStyle().set("margin-right", "10px");
        divColumnItems.getStyle().set("margin-bottom", "3px");
        divColumnItems.getStyle().set("max-width", "40px");
        divColumnItems.getStyle().set("width", "40px");
        divColumnItems.getStyle().set(OVERFLOW_Y, HIDDEN);

        divRowToSecondary.add(divColumnItems, outPutConsole);

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

        final ContextMenu contextMenuDivPortError = new ContextMenu();
        contextMenuDivPortError.setTarget(divWithPortErrors);

        final Div divWithIconAndText = new Div();
        divWithIconAndText.addClassNames(Display.FLEX, FlexDirection.ROW, AlignItems.CENTER, JustifyContent.START);
        final Text text = new Text("Change port permissions");
        divWithIconAndText.add(SvgFactory.createIconFromSvg("unlock-black.svg", "30px", null), text);
        contextMenuDivPortError.addItem(divWithIconAndText, event -> {
            var spanListWithError = this.spansList.stream()
                    .map(HasText::getText)
                    .distinct()
                    .toArray(String[]::new);
            this.changeSerialPortPermissionDialog.setPortErrors(spanListWithError);
            this.spansList.clear();
        }).setCheckable(true);

        spanPortFailure.addClassName(Left.SMALL);
        this.divWithPortErrors.add(spanPortFailure);
        divWithPortErrors.getStyle().set("color", "red");

        Stream.of(divSpanTotalDevices, divWithPortErrors)
                .forEach(div -> {
                    div.addClassNames(Right.MEDIUM, BOX_SHADOW_VAADIN_BUTTON);
                    div.getElement().setAttribute("theme", "badge");
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
            BroadcasterRefreshDevicesButton.INSTANCE.broadcast(RefreshDevicesEvent.DISABLE);
            final EspDevicesCarousel espDevicesCarousel = new EspDevicesCarousel(new ProgressBar(), LOADING);
            this.divCarousel.removeAll();
            this.divCarousel.add(espDevicesCarousel);
            this.showDetectedDevices(ui, espDevicesCarousel);
        });
    }

    /**
     * <p>This method is used to read the micros that are connected to the OS, in case of not being able to read any,
     * a red SPAN will be displayed with the name of the port with the failure, that failure could be, permissions, etc.</p>
     *
     * <p>The reading process is performed in a Scheduler of <strong>boundedElastic</strong> type in order not to block the UI having
     * faster feedBack from the microcontrollers that are read, which is difficult with synchronous programming.</p>
     *
     * @param ui                      the UI
     * @param paramEspDevicesCarousel the EspDevicesCarousel to process
     */
    private void showDetectedDevices(final UI ui, final EspDevicesCarousel paramEspDevicesCarousel) {

        this.leftPrimarySectionProgressBar.setVisible(true);

        this.esptoolService.readAllDevices()
                .flatMap(this.countAllDevices())
                .flatMap(this.configureSlides(ui, paramEspDevicesCarousel, spansList))
                .doOnError(canNotBeReadDevice -> this.onError(ui, canNotBeReadDevice))
                .doOnComplete(() -> this.onComplete(ui, paramEspDevicesCarousel, spansList))
                .subscribe(resultEspDevicesCarousel -> {
                    ui.access(() -> {
                        if (resultEspDevicesCarousel.getSlideList().isEmpty()) {
                            resultEspDevicesCarousel.showLoading(LOADING, true);
                        } else {
                            resultEspDevicesCarousel.createSlidesAndShow();
                            log.info("Slides created {}", resultEspDevicesCarousel.getSlideList().size());
                        }
                    });
                });

    }

    /**
     * We count the available devices and map them to an object. <strong>EspDeviceWithTotalDevices</strong>
     *
     * @return A {@link Function} <EspDeviceInfo, Mono<EspDeviceWithTotalDevices>>}
     */
    private Function<EspDeviceInfo, Mono<EspDeviceWithTotalDevices>> countAllDevices() {

        return item -> this.esptoolService.countAllDevices()
                .map(count ->
                        EspDeviceWithTotalDevicesMapper.INSTANCE.espDeviceWithTotalDevices(item, count)
                );
    }

    /**
     * We configure the slides for each type of device being read
     *
     * @param ui                      the UI
     * @param paramEspDevicesCarousel the EspDevicesCarousel
     * @param spansList               a Set of Span
     * @return A {@link Function}
     */
    private Function<EspDeviceWithTotalDevices, Mono<EspDevicesCarousel>> configureSlides(
            UI ui, EspDevicesCarousel paramEspDevicesCarousel, Set<Span> spansList) {

        return espDeviceWithTotalDevices ->
                this.configureSlides(spansList, espDeviceWithTotalDevices, paramEspDevicesCarousel, ui)
                        .switchIfEmpty(Mono.defer(() -> this.fallback(ui, paramEspDevicesCarousel, spansList)));
    }

    /**
     * <p>This is triggered before the subscription in case a device cannot be read and returns a null mac address.</p>
     *
     * <p>We are also going to iterate the spam Set with errors to update them in the div</p>
     *
     * @param ui                      the UI
     * @param paramEspDevicesCarousel the EspDevicesCarousel
     * @param spansList               Set with Port Span
     * @return A {@link Mono} with EspDevicesCarousel
     */
    public Mono<EspDevicesCarousel> fallback(final UI ui, EspDevicesCarousel paramEspDevicesCarousel, Set<Span> spansList) {
        ui.access(() -> {
            if (!spansList.isEmpty()) {
                /* The span is added with the text "Port Failure:" */
                this.divWithPortErrors.add(spanPortFailure);
                /* Iterate through the Span Set to get the String ordemos and pass to Strings*/
                final String newSpanErrors = spansList
                        .stream()
                        .map(HasText::getText)
                        .distinct()
                        .sorted(Comparator.comparing(Objects::toString))
                        .collect(Collectors.joining(" "));

                this.divWithPortErrors.removeAll();
                this.divWithPortErrors.add(spanPortFailure);
                /* We set the ports with error in this Span */
                this.spanPortFailure.setText(PORT_FAILURE + " " + newSpanErrors);
                this.divWithPortErrors.add(spanPortFailure);
                this.divWithPortErrors.setVisible(true);
                this.spanTotalDevicesValue.setText("  " + spansList.size());

            }
            log.info("fallback {}", paramEspDevicesCarousel);
        });


        return Mono.just(paramEspDevicesCarousel);
    }

    /**
     * If we cannot read we send a Mono.empty to trigger the fallback
     * {@link ReadFlashView#fallback(UI, EspDevicesCarousel, Set)}, otherwise we reconfigure the Slides and add them to the internal list of the object EspDevicesCarousel.
     *
     * <p>The ShowDevicesBuilder when it succeeds in creating a Slide correctly adds it right in the addSlide method, e.g. {@link ShowDevicesBuilder#make}
     *
     * <p>Each Slide at the end of this reactive stream, will have a button in the upper right corner, which will allow to execute the <strong>read_flash<strong>
     *     command and extract the firmware from the device.
     * </p>
     *
     * <ul>
     *     <li>showEsp01s();</li>
     *     <li>showEsp8266340G4MB();</li>
     *     <li>showEsp82664Cp201x4MB();</li>
     *     <li>showEsp8285();</li>
     *     <li>showEsp32S3();</li>
     * </ul>
     *
     *
     * </p>
     *
     * @param spansList
     * @param espDeviceWithTotalDevices
     * @param espDevicesCarousel
     * @param ui                        the UI
     * @return A {@link Mono} with EspDevicesCarousel with more configuration
     */
    private Mono<EspDevicesCarousel> configureSlides(final Set<Span> spansList,
                                                     EspDeviceWithTotalDevices espDeviceWithTotalDevices,
                                                     EspDevicesCarousel espDevicesCarousel,
                                                     final UI ui) {

        final var mac = espDeviceWithTotalDevices.espDeviceInfo().macAddress();
        var espDeviceInfo = espDeviceWithTotalDevices.espDeviceInfo();

        if (Objects.isNull(mac)) {
            ui.access(() -> {
                final Span spanPortFailureValue = new Span();
                spanPortFailureValue.setText(StringUtils.EMPTY);
                spanPortFailureValue.setText(espDeviceInfo.port());
                spansList.add(spanPortFailureValue);
                this.spanTotalDevicesValue.setText("  " + spansList.size());
            });
            return Mono.empty();
        }

        //Fixme ? update using onComplete ?
        try {
            ui.access(() -> this.spanTotalDevicesValue.setText("  " + espDeviceWithTotalDevices.totalDevices()));
        } catch (UIDetachedException ex) {
            //Do nothing
        }

        try {
            ui.access(() -> this.emitNextEvent(espDeviceWithTotalDevices.espDeviceInfo()));
        } catch (UIDetachedException ex) {
          // Do nothing
        }

        var resultCarousel = ShowDevicesBuilder.builder()
                .withEspDevicesCarousel(espDevicesCarousel)
                .withEsptoolService(this.esptoolService)
                .withEspDeviceInfo(espDeviceInfo)
                .withOutPutConsole(this.outPutConsole)
                .withUi(ui)
                .withStartSizeAddress(this.startAddress)
                .withCustomFlashSizeAddress(this.endAddress)
                .withAutoDetectFlashSize(this.autoDetectFlashSize)
                .withBaudRatesComboBox(this.baudRatesComboBox)
                .withEsptoolPathService(this.esptoolPathService)
                .withFlashDownloadButton(this.flashDownloadButtonService)
                .withPublisher(this.publishMessageListItem)
                .applyStrategiesWithCustomContentCreationPerSlide()
                .make();

        return Mono.just(resultCarousel);

    }

    /**
     * En caso de no poder leer ningun puerto serie, es decir el dispositivo no esta conectado, se deberia
     * revisar la conexion con el puerto USB
     *
     * <p>new CanNotBeReadDeviceException("Possibly empty ports")</`p>
     *
     * @param ui                 the UI
     * @param canNotBeReadDevice Possibly empty ports
     */
    private void onError(final UI ui, Throwable canNotBeReadDevice) {
        try {
            ui.access(() -> {
                //In case we cannot read any port from Java /dev/tty* /dev/cua*
                ConfirmDialogBuilder.showWarning(canNotBeReadDevice.getMessage());
                this.setDivCarouselNoDevicesShown();
                this.leftPrimarySectionProgressBar.setVisible(false);
                this.buttonRefreshDevices.setEnabled(true);
                BroadcasterRefreshDevicesButton.INSTANCE.broadcast(RefreshDevicesEvent.ENABLE);
            });
        } catch (UIDetachedException ex) {
            //Do nothing
        }

    }

    /**
     * <p>This piece of code is executed when the reactive stream is completed.</p>
     * <p>In case the Slides are empty, we display the initial text in the DIV "No devices shown!"; </p>
     *
     * @param ui                      the UI
     * @param paramEspDevicesCarousel the EspDevicesCarousel
     * @param spanErrorPortList       Span set with errors
     */
    private void onComplete(final UI ui, final EspDevicesCarousel paramEspDevicesCarousel, Set<Span> spanErrorPortList) {
        ui.access(() -> {
            this.leftPrimarySectionProgressBar.setVisible(false);
            this.buttonRefreshDevices.setEnabled(true);
            BroadcasterRefreshDevicesButton.INSTANCE.broadcast(RefreshDevicesEvent.ENABLE);
            if (spanErrorPortList.isEmpty()) {
                this.divWithPortErrors.setVisible(false);
                this.divWithPortErrors.removeAll();
            } else {
                ConfirmDialogBuilder.showWarning("Port`s with error: " +
                        spanErrorPortList.stream()
                                .map(HasText::getText)
                                .distinct()
                                .collect(Collectors.joining(",")));
            }
            if (paramEspDevicesCarousel.getSlideList().isEmpty()) {
                this.setDivCarouselNoDevicesShown();
            }
        });
    }

    /**
     * It is invoked when the reactive stream ends, in the {@link ReadFlashView#onComplete(UI, EspDevicesCarousel, Set)}
     */
    private void setDivCarouselNoDevicesShown() {
        final EspDevicesCarousel resetEspDevicesCarousel = new EspDevicesCarousel(new ProgressBar(), NO_DEVICES_SHOWN);
        this.divCarousel.removeAll();
        this.divCarousel.add(resetEspDevicesCarousel);
    }

    /**
     *
     * Emit event to bell
     *
     * @param espDeviceInfo
     */
    public void emitNextEvent(final EspDeviceInfo espDeviceInfo) {

        final MessageListItem messageListItem = new MessageListItem(espDeviceInfo.chipIs().concat(" Executed flash_id successfully!!!"),
                LocalDateTime.now().toInstant(ZoneOffset.UTC),
                espDeviceInfo.port());

        var colors = Arrays.asList(0,1,2,3,4,5,6);
        messageListItem.setUserColorIndex(colors.get(SECURE_RANDOM.nextInt(colors.size())));

        this.publishMessageListItem.tryEmitNext(messageListItem);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        //Disabled event for buttonRefreshDevices while in use
        this.broadcasterRefreshButton = BroadcasterRefreshDevicesButton.INSTANCE.register(value -> {
            try {
                event.getUI().access(() -> this.buttonRefreshDevices.setEnabled(value));
            } catch (UIDetachedException ex) {
                //Do nothing,  It is thrown when you attempt to access closed UI.
                //https://stackoverflow.com/a/73885127/7267818
            }
        });
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        broadcasterRefreshButton.remove();
        broadcasterRefreshButton = null;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        if (attachEvent.isInitialAttach()) {
            super.onAttach(attachEvent);
            final UI ui = attachEvent.getUI();
            this.refreshDevices(ui);
            //Disabled event for buttonRefreshDevices while in use
            this.broadcasterRefreshButton = BroadcasterRefreshDevicesButton.INSTANCE.register(value ->
                    ui.access(() -> this.buttonRefreshDevices.setEnabled(value))
            );
            super.add(this.initialInformationReadFlashViewDialog);

            RadioButtonGroup<String> mode = new RadioButtonGroup<>("Header theme");
            mode.setItems("Light", "Dark");
            mode.addValueChangeListener(e -> getChildren().forEach(component -> {
                if (component instanceof Sidebar) {
                    if (e.getValue().equals("Dark")) {
                        ((Sidebar) component).addHeaderThemeName(Lumo.DARK);
                    } else {
                        ((Sidebar) component).removeHeaderThemeName(Lumo.DARK);
                    }
                }
            }));
            addComponentAsFirst(mode);

            Sidebar sidebar = new Sidebar(
                    "New event",
                    "Fill in the blibber-blabber below to create a sensational event that will leave everyone flibber-gasted!",
                    createForm()
            );
            add(sidebar);

            Button button = new Button("Open sidebar", e -> {
                sidebar.open();
                Animated.animate(sidebar, Animation.FADE_IN);
            });
            addComponentAtIndex(1, button);
            sidebar.getCancelButton().addClickListener(event -> {
               sidebar.close();
               Animated.animate(sidebar, Animation.FADE_OUT);
            });

        }

    }

    private Component[] createForm() {
        TextField title = new TextField("Title");

        TextArea description = new TextArea("Description");

        MultiSelectComboBox<String> guests = new MultiSelectComboBox<>("Guests");
        guests.setItems(
                "JOHN_SMITH", "EMILY_JOHNSON", "MICHAEL_DAVIS", "SOPHIA_BROWN",
                "DANIEL_WILSON", "OLIVIA_MARTINEZ", "DAVID_THOMPSON"
        );

        RadioButtonGroup<String> visibility = new RadioButtonGroup<>("Visibility");
        visibility.setItems("Private", "Public");
        visibility.setValue("Private");

        return new Component[]{title, description, guests, visibility};
    }

}
