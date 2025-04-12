package com.esp.espflow.views.readflash;

import com.esp.espflow.entity.EspDeviceInfoRecord;
import com.esp.espflow.entity.EspDeviceWithTotalDevicesRecord;
import com.esp.espflow.entity.event.EsptoolFRWMessageListItemEvent;
import com.esp.espflow.enums.BaudRatesEnum;
import com.esp.espflow.enums.RefreshDevicesEvent;
import com.esp.espflow.mappers.EspDeviceWithTotalDevicesMapper;
import com.esp.espflow.service.EsptoolPathService;
import com.esp.espflow.service.EsptoolService;
import com.esp.espflow.service.downloader.FlashDownloadButtonService;
import com.esp.espflow.service.respository.impl.WizardEspService;
import com.esp.espflow.util.ConfirmDialogBuilder;
import com.esp.espflow.util.ResponsiveHeaderDiv;
import com.esp.espflow.util.console.OutPutConsole;
import com.esp.espflow.util.svgfactory.SvgFactory;
import com.esp.espflow.views.MainLayout;
import com.esp.espflow.views.dialog.ChangeSerialPortPermissionDialog;
import com.esp.espflow.views.readflash.wizard.WizardReadFlashView;
import com.infraleap.animatecss.Animated;
import com.infraleap.animatecss.Animated.Animation;
import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.UIDetachedException;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout.Orientation;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility;
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
import org.springframework.context.event.EventListener;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.Comparator;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.esp.espflow.util.EspFlowConstants.BLACK_TO_WHITE_ICON;
import static com.esp.espflow.util.EspFlowConstants.BOX_SHADOW_VAADIN_BUTTON;
import static com.esp.espflow.util.EspFlowConstants.CONTEXT_MENU_ITEM_NO_CHECKMARK;
import static com.esp.espflow.util.EspFlowConstants.CURSOR_POINTER;
import static com.esp.espflow.util.EspFlowConstants.HIDDEN;
import static com.esp.espflow.util.EspFlowConstants.LOADING;
import static com.esp.espflow.util.EspFlowConstants.NO_DEVICES_SHOWN;
import static com.esp.espflow.util.EspFlowConstants.OVERFLOW_X;
import static com.esp.espflow.util.EspFlowConstants.OVERFLOW_Y;
import static com.esp.espflow.util.EspFlowConstants.PORT_FAILURE;
import static com.esp.espflow.util.EspFlowConstants.SETTINGS;
import static com.esp.espflow.util.EspFlowConstants.WIZARD_READ_FLASH_ESP_VIEW;

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
    //private final Button buttonRefreshDevices = new Button("Refresh devices", VaadinIcon.REFRESH.create());
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
    private final Sinks.Many<EsptoolFRWMessageListItemEvent> publishMessageListItem;
    /**
     * Publisher for RefreshDevicesEvent
     */
    private final Sinks.Many<RefreshDevicesEvent> publisherRefreshEvent;

    /*
     * Show initial wizard
     */
    private final WizardReadFlashView wizardReadFlashView;
    /*
     * To save the status of the wizards
     */
    private final WizardEspService wizardEspService;

    private final SidebarReadFlash sidebarReadFlash;

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
        splitLayout.setSplitterPosition(70);
        splitLayout.getStyle().set(OVERFLOW_Y, HIDDEN);
        splitLayout.getStyle().set(OVERFLOW_X, HIDDEN);
        /*
         * Primary section
         */
        final Div divForRightCarousel = this.divForRightCarousel();
        final var horizontalLayoutToPrimarySection = this.horizontalLayoutToPrimarySection(divForRightCarousel);

        this.sidebarReadFlash.createSection(this.leftFormForAddress());

        final VerticalLayout verticalLayoutPrimarySecction = new VerticalLayout(this.sidebarReadFlash, horizontalLayoutToPrimarySection);
        verticalLayoutPrimarySecction.setSizeFull();
        splitLayout.addToPrimary(verticalLayoutPrimarySecction);
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

        splitLayout.getSecondaryComponent().getElement().getStyle().set("scrollbar-color", "#3b3b3b #202020");

        splitLayout.addClickListener(event -> this.outPutConsole.fitAndScrollToBottom());

        return splitLayout;
    }

    /**
     * @param divForRightCarousel The carousel with the scanned scans
     * @return A {@link HorizontalLayout} with the form on the right and the carousel on the left side
     */
    private HorizontalLayout horizontalLayoutToPrimarySection(Div divForRightCarousel) {
        this.leftPrimarySectionProgressBar.setVisible(false);
        this.leftPrimarySectionProgressBar.setIndeterminate(true);
        this.horizontalLayoutForPrimarySection.setId("div-for-primary");
        this.horizontalLayoutForPrimarySection.setSizeFull();
        this.horizontalLayoutForPrimarySection.setJustifyContentMode(JustifyContentMode.CENTER);
        this.horizontalLayoutForPrimarySection.add(divForRightCarousel);
        return horizontalLayoutForPrimarySection;
    }

    /**
     * This part contains the form for entering the memory addresses to be read.
     *
     * @return A {@link FormLayout}
     */
    private Div leftFormForAddress() {
        var rowAutoSize = new HorizontalLayout(autoDetectFlashSize, spanAutoDetectFlashSize);
        rowAutoSize.setWidthFull();
        rowAutoSize.addClassNames(LumoUtility.Margin.Top.MEDIUM);
        final Div formLayout = new Div(startAddress, endAddress, baudRatesComboBox, rowAutoSize, leftPrimarySectionProgressBar);
        Stream.of(startAddress, endAddress, rowAutoSize, baudRatesComboBox, leftPrimarySectionProgressBar)
                .forEach(items -> {
                    items.addClassName(AlignSelf.BASELINE);
                    items.setWidthFull();
                });
        formLayout.addClassNames(Display.FLEX, FlexDirection.COLUMN, LumoUtility.Width.FULL,
                AlignItems.START, JustifyContent.CENTER);

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
        parent.setId("parent-sidebar-content-div");
        parent.addClassNames(Display.FLEX, AlignItems.CENTER, Padding.End.LARGE, Padding.Start.LARGE, Padding.Vertical.SMALL);
        return parent;
    }

    /**
     * Div for carousel
     *
     * @return A {@link Div}
     */
    private Div divForRightCarousel() {
        divCarousel.setId("div-carousel");
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
        final HorizontalLayout horizontalLayoutFooter = new HorizontalLayout();
        horizontalLayoutFooter.setWidthFull();
        horizontalLayoutFooter.setHeight("40px");
        horizontalLayoutFooter.getStyle().set("border-top", " 1px solid var(--lumo-contrast-10pct)");
        horizontalLayoutFooter.addClassName(BOX_SHADOW_VAADIN_BUTTON);
        horizontalLayoutFooter.setAlignItems(Alignment.CENTER);
        horizontalLayoutFooter.setJustifyContentMode(JustifyContentMode.START);

        final Div divSpanTotalDevices = new Div(this.spanTotalDevices, this.spanTotalDevicesValue);

        this.divWithPortErrors.setVisible(false);
        this.divWithPortErrors.getStyle().setCursor(CURSOR_POINTER);
        Tooltip.forComponent(divWithPortErrors).setText("Change port permissions - SPACE");
        this.divWithPortErrors.addClickShortcut(Key.SPACE);
        this.divWithPortErrors.addClickListener(event -> this.showErroneousPortsInDialog());

        final ContextMenu contextMenuDivPortError = new ContextMenu();
        contextMenuDivPortError.setTarget(divWithPortErrors);

        final Div divWithIconAndText = new Div();
        divWithIconAndText.addClassNames(Display.FLEX, FlexDirection.ROW, AlignItems.CENTER, JustifyContent.START);
        final Text text = new Text("Change port permissions");
        var iconBlackLock = SvgFactory.createIconFromSvg("unlock-black.svg", "30px", null);
        iconBlackLock.addClassName(BLACK_TO_WHITE_ICON);
        divWithIconAndText.add(iconBlackLock, text);
        contextMenuDivPortError.addItem(divWithIconAndText, event -> this.showErroneousPortsInDialog())
                .addClassName(CONTEXT_MENU_ITEM_NO_CHECKMARK);

        spanPortFailure.addClassName(Left.SMALL);
        spanPortFailure.getStyle().setCursor(CURSOR_POINTER);
        this.divWithPortErrors.add(spanPortFailure);
        this.divWithPortErrors.getElement().getThemeList().add("badge error primary");
        divSpanTotalDevices.getElement().getThemeList().add("badge");

        Stream.of(divSpanTotalDevices, divWithPortErrors).forEach(div -> {
            div.addClassNames(Right.MEDIUM, BOX_SHADOW_VAADIN_BUTTON);
        });

        final Div divForBadges = new Div(divSpanTotalDevices, divWithPortErrors);
        divForBadges.setId("divForBadges");
        divForBadges.setWidthFull();
        divForBadges.addClassNames(Display.FLEX, FlexDirection.ROW, Left.MEDIUM);
        /*Margin left to span values */
        Stream.of(spanTotalDevicesValue, spanPortFailure)
                .forEach(spans -> spans.addClassName(Left.SMALL));
        horizontalLayoutFooter.add(divForBadges);

        Stream.of(this.spanTotalDevices, this.spanTotalDevicesValue, this.divWithPortErrors)
                .forEach(span -> {
                    span.getStyle().set("font-size", "var(--lumo-font-size-xs)");
                    span.addClassName("row-span-flash-size-footer");
                });

        return horizontalLayoutFooter;
    }

    /**
     * Construct a Span array with the erroneous ports and build a new stream array to pass to the Dialog.
     */
    private void showErroneousPortsInDialog() {
        var spanListWithError = this.spansList.stream()
                .map(HasText::getText)
                .distinct()
                .toArray(String[]::new);
        this.changeSerialPortPermissionDialog.setPortErrors(spanListWithError);
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
    private Function<EspDeviceInfoRecord, Mono<EspDeviceWithTotalDevicesRecord>> countAllDevices() {

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
    private Function<EspDeviceWithTotalDevicesRecord, Mono<EspDevicesCarousel>> configureSlides(
            UI ui, EspDevicesCarousel paramEspDevicesCarousel, Set<Span> spansList) {

        return espDeviceWithTotalDevicesRecord ->
                this.configureSlides(spansList, espDeviceWithTotalDevicesRecord, paramEspDevicesCarousel, ui)
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
     * command and extract the firmware from the device.
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
     * @param espDeviceWithTotalDevicesRecord
     * @param espDevicesCarousel
     * @param ui                              the UI
     * @return A {@link Mono} with EspDevicesCarousel with more configuration
     */
    private Mono<EspDevicesCarousel> configureSlides(final Set<Span> spansList,
                                                     EspDeviceWithTotalDevicesRecord espDeviceWithTotalDevicesRecord,
                                                     EspDevicesCarousel espDevicesCarousel,
                                                     final UI ui) {

        final var mac = espDeviceWithTotalDevicesRecord.espDeviceInfoRecord().macAddress();
        var espDeviceInfo = espDeviceWithTotalDevicesRecord.espDeviceInfoRecord();

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
            ui.access(() -> this.spanTotalDevicesValue.setText("  " + espDeviceWithTotalDevicesRecord.totalDevices()));
        } catch (UIDetachedException ex) {
            //Do nothing
        }

        try {
            ui.access(() -> this.emitNextEvent(espDeviceWithTotalDevicesRecord.espDeviceInfoRecord()
                    , " Executed flash_id successfully!!!"));
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
     * If no serial port can be read, i.e. the device is not connected, the connection to the USB port should be checked.
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
                this.updateButtonState(true);
                publisherRefreshEvent.tryEmitNext(RefreshDevicesEvent.ENABLE);
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
            //this.buttonRefreshDevices.setEnabled(true);
            //this.updateButtonState(true);
            //Enviar evento aqui con Sinks.Many de project reactor
            //BroadcasterRefreshDevicesButton.INSTANCE.broadcast(RefreshDevicesEvent.ENABLE);
            publisherRefreshEvent.tryEmitNext(RefreshDevicesEvent.ENABLE);
            log.info("Subscribers count {}", publisherRefreshEvent.currentSubscriberCount());
            if (spanErrorPortList.isEmpty()) {
                this.divWithPortErrors.setVisible(false);
                this.divWithPortErrors.removeAll();
            } else {

                final String errorPorts = spanErrorPortList.stream()
                        .map(HasText::getText)
                        .distinct()
                        .collect(Collectors.joining(","));

                ConfirmDialogBuilder.showWarning("Port`s with error: " + errorPorts);

                spanErrorPortList
                        .stream()
                        .map(HasText::getText)
                        .forEach(port -> {

                            final EsptoolFRWMessageListItemEvent esptoolFRWMessageListItemEvent = new EsptoolFRWMessageListItemEvent(
                                    "This chip cannot be parsed executed flash_id failed.", port);
                            this.publishMessageListItem.tryEmitNext(esptoolFRWMessageListItemEvent);

                        });

            }
            if (paramEspDevicesCarousel.getSlideList().isEmpty()) {
                this.setDivCarouselNoDevicesShown();
            }
        });
    }

    public void updateButtonState(boolean enabled) {
        this.getElement().executeJs("document.getElementById('button-refresh-device').disabled = $0", !enabled);
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
     * Emit event to bell
     *
     * @param espDeviceInfoRecord
     */
    public void emitNextEvent(final EspDeviceInfoRecord espDeviceInfoRecord, String concatResultMessage) {

        EsptoolFRWMessageListItemEvent esptoolFRWMessageListItemEvent = new EsptoolFRWMessageListItemEvent(espDeviceInfoRecord.chipIs()
                .concat(concatResultMessage),
                espDeviceInfoRecord.port());

        this.publishMessageListItem.tryEmitNext(esptoolFRWMessageListItemEvent);
    }

    /**
     * This method triggers the scanning of the microcontrollers and creates the slides, it also updates the footer badges.
     * <p>
     * It is also disabled on the first click preventing the user from clicking and another scan is processed to avoid interfering with the previous one.
     *
     * @param refreshDevicesEvent events to process them in this view
     */
    @EventListener
    public void refreshDevice(RefreshDevicesEvent refreshDevicesEvent) {
        //Evento de scan, solo uno a la vez
        if (refreshDevicesEvent == RefreshDevicesEvent.SCAN) {
            getUI().ifPresent(ui -> {
                //Evento de disable para los subscriptores
                publisherRefreshEvent.tryEmitNext(RefreshDevicesEvent.DISABLE);
                log.info("Send disable event");
                final EspDevicesCarousel espDevicesCarousel = new EspDevicesCarousel(new ProgressBar(), LOADING);
                this.divCarousel.removeAll();
                this.divCarousel.add(espDevicesCarousel);
                this.showDetectedDevices(ui, espDevicesCarousel);
            });
        }
        if (refreshDevicesEvent == RefreshDevicesEvent.OPEN_SIDEBAR) {
            this.sidebarReadFlash.toggleSidebar();
        }
        if (refreshDevicesEvent == RefreshDevicesEvent.OPEN_READ_FLASH_WIZARD) {
            this.add(this.wizardReadFlashView);
            this.wizardReadFlashView.openAndDisableModeless();
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        //Disabled event for buttonRefreshDevices while in use
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        final UI ui = attachEvent.getUI();
        ui.getPage().executeJs(
                """
                        if (window.location.hash) {
                          var hash = window.location.hash.substring(1); // Delete the '#'
                          return hash;"
                        }
                        """
        ).then(String.class, hash -> {
            if (Objects.nonNull(hash) && !hash.contains(SETTINGS)) {
                this.add(this.wizardReadFlashView);
                this.wizardReadFlashView.openAndDisableModeless();
            } else {
                ui.getPage().fetchCurrentURL(url -> {
                    final String ref = StringUtils.defaultIfEmpty(url.getRef(), StringUtils.EMPTY);
                    if (!ref.contains(SETTINGS)) {
                        this.wizardEspService.findByName(WIZARD_READ_FLASH_ESP_VIEW)
                                .ifPresent(hide -> {
                                    if (hide.isWizardEnabled()) {
                                        this.add(this.wizardReadFlashView);
                                        this.wizardReadFlashView.openAndDisableModeless();
                                    }
                                });
                    }
                });
            }
        });

    }

}
