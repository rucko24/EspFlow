package com.esp.espflow.views.flashesp;

import com.esp.espflow.enums.BaudRatesEnum;
import com.esp.espflow.enums.EraseFlashEnum;
import com.esp.espflow.enums.FlashModeEnum;
import com.esp.espflow.event.EsptoolFRWMessageListItemEvent;
import com.esp.espflow.mappers.ExtractChipIsFromStringMapper;
import com.esp.espflow.service.DebugSerialPortService;
import com.esp.espflow.service.EsptoolPathService;
import com.esp.espflow.service.EsptoolService;
import com.esp.espflow.service.respository.impl.WizardEspService;
import com.esp.espflow.util.CommandsOnFirstLine;
import com.esp.espflow.util.ConfirmDialogBuilder;
import com.esp.espflow.util.ResponsiveHeaderDiv;
import com.esp.espflow.util.console.OutPutConsole;
import com.esp.espflow.util.svgfactory.SvgFactory;
import com.esp.espflow.views.MainLayout;
import com.esp.espflow.views.flashesp.wizards.WizardFlashEspDialog;
import com.infraleap.animatecss.Animated;
import com.infraleap.animatecss.Animated.Animation;
import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout.Orientation;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.FlexDirection;
import com.vaadin.flow.theme.lumo.LumoUtility.JustifyContent;
import com.vaadin.flow.theme.lumo.LumoUtility.Overflow;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.Objects;
import java.util.concurrent.Executor;

import static com.esp.espflow.util.EspFlowConstants.AUTO;
import static com.esp.espflow.util.EspFlowConstants.BAUD_RATE;
import static com.esp.espflow.util.EspFlowConstants.BOX_SHADOW_VAADIN_BUTTON;
import static com.esp.espflow.util.EspFlowConstants.CURSOR_POINTER;
import static com.esp.espflow.util.EspFlowConstants.DEFAULT_INIT_ADDRESS_SIZE_TO_WRITE_0x_00000;
import static com.esp.espflow.util.EspFlowConstants.FLASH_ID;
import static com.esp.espflow.util.EspFlowConstants.FLASH_OFF_SVG;
import static com.esp.espflow.util.EspFlowConstants.HIDDEN;
import static com.esp.espflow.util.EspFlowConstants.MARGIN_10_PX;
import static com.esp.espflow.util.EspFlowConstants.MARGIN_LEFT;
import static com.esp.espflow.util.EspFlowConstants.MARGIN_TOP;
import static com.esp.espflow.util.EspFlowConstants.OVERFLOW_X;
import static com.esp.espflow.util.EspFlowConstants.OVERFLOW_Y;
import static com.esp.espflow.util.EspFlowConstants.PORT;
import static com.esp.espflow.util.EspFlowConstants.SETTINGS;
import static com.esp.espflow.util.EspFlowConstants.SIZE_25_PX;
import static com.esp.espflow.util.EspFlowConstants.SIZE_30_PX;
import static com.esp.espflow.util.EspFlowConstants.TOGGLE_BADGE_PILL_CONTRAST;
import static com.esp.espflow.util.EspFlowConstants.WEB_SERIAL_ICON_SVG;
import static com.esp.espflow.util.EspFlowConstants.WINDOWS_LOCATION_REMOVE_HASH;
import static com.esp.espflow.util.EspFlowConstants.WIZARD_FLASH_ESP_VIEW;

/**
 * @author rubn
 */
@Log4j2
@UIScope
@SpringComponent
@PageTitle("Flash Esp32-ESP8266")
@Route(value = "flash-esp", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@RequiredArgsConstructor
public class FlashEspView extends Div implements ResponsiveHeaderDiv, BeforeLeaveObserver, BeforeEnterObserver {

    private final RadioButtonGroup<BaudRatesEnum> baudRatesRadioButtonGroup = new RadioButtonGroup<>();
    private final RadioButtonGroup<FlashModeEnum> flashModeRadioButtonGroup = new RadioButtonGroup<>();
    private final RadioButtonGroup<EraseFlashEnum> eraseRadioButtons = new RadioButtonGroup<>();
    private final SvgIcon svgIconFlashMe = SvgFactory.createIconFromSvg(FLASH_OFF_SVG, SIZE_25_PX, null);
    private final Button flashButton = new Button(svgIconFlashMe);
    private final VerticalLayout contentForPrimary = new VerticalLayout();
    private final OutPutConsole outPutConsole = new OutPutConsole();
    private final Icon shoWizardIcon = VaadinIcon.INFO_CIRCLE.create();
    private final ToggleButton toggleButtonEnableWebSerial = new ToggleButton();
    private final SvgIcon iconWebSerial = SvgFactory.createIconFromSvg(WEB_SERIAL_ICON_SVG, SIZE_30_PX, null);
    private final HorizontalLayout rowForWebSerialIcon = new HorizontalLayout();
    /**
     * Services
     */
    private final Sinks.Many<EsptoolFRWMessageListItemEvent> publishMessageListItem;
    private final WizardFlashEspDialog wizardFlashEspDialog;
    private final WizardEspService wizardFlashEspRepository;
    private final DivFlashUploader divFlashUploader;
    private final DivHeaderPorts divHeaderPorts;
    private final EsptoolService esptoolService;
    private final EsptoolPathService esptoolPathService;
    private final Executor eventTaskExecutor;
    private final DebugSerialPortService debugPortService;
    /**
     * Mutable fields
     */
    private String flashFileName;
    private String[] commands;
    private HorizontalLayout rowMainHeader;

    @PostConstruct
    public void init() {
        this.configureHeaderComponents();
        this.showContent();
    }

    private void showContent() {

        super.addClassNames(Display.FLEX, FlexDirection.ROW,
                LumoUtility.Width.FULL,
                LumoUtility.Height.FULL);
        super.getStyle().set(OVERFLOW_X, HIDDEN);

        final SplitLayout splitLayout = getSplitLayout();
        super.add(splitLayout);
        Animated.animate(splitLayout, Animation.FADE_IN);
    }

    /**
     * The SplitLayout
     *
     * @return A configured {@link SplitLayout}
     */
    private SplitLayout getSplitLayout() {
        final SplitLayout splitLayout = new SplitLayout(Orientation.VERTICAL);
        splitLayout.setSizeFull();
        splitLayout.setSplitterPosition(65);
        splitLayout.getStyle().set(OVERFLOW_Y, HIDDEN);
        splitLayout.getStyle().set(OVERFLOW_X, HIDDEN);
        /*
         * Primary section
         */
        final var divRowPort = this.rowPorts();
        final var divRowBaudRate = this.rowBaudRates();
        final var divRowFlashMode = this.rowFlashMode();
        final var divRowEraseFlash = this.rowEraseFlash();
        final var divRowUploaderFlash = this.rowUploadingFlash();
        final var buttonFlash = this.buttonFlash();

        contentForPrimary.add(divRowPort, divRowBaudRate,
                divRowFlashMode, divRowEraseFlash, divRowUploaderFlash,
                buttonFlash);
        contentForPrimary.addClassName("vertical-parent");
        splitLayout.addToPrimary(contentForPrimary);
        /*
         * Secondary section
         */
        final var divRowToSecondary = new Div();
        divRowToSecondary.addClassNames(Display.FLEX, FlexDirection.ROW);
        divRowToSecondary.getStyle().set(OVERFLOW_Y, HIDDEN);

        outPutConsole.getDivTextArea().removeClassNames(LumoUtility.Margin.Left.LARGE, LumoUtility.Margin.Minus.Right.LARGE);
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

        splitLayout.addToSecondary(divRowToSecondary);
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

    private Div rowPorts() {
        return this.divHeaderPorts;
    }

    private Div rowBaudRates() {
        final H3 h3 = new H3("Baud rate");
        h3.getStyle().set(MARGIN_TOP, AUTO);
        final Div divH2BaudRate = new Div(h3);
        divH2BaudRate.addClassName("div-baud-rate-h3-div");
        baudRatesRadioButtonGroup.setItems(BaudRatesEnum.values());
        baudRatesRadioButtonGroup.setValue(BaudRatesEnum.BAUD_RATE_115200);
        baudRatesRadioButtonGroup.setRenderer(BaudRatesEnum.rendererWithTooltip());
        baudRatesRadioButtonGroup.addClassName("baud-radio-group");
        this.applyRadioButtomTheme(this.baudRatesRadioButtonGroup, LumoUtility.Margin.Right.MEDIUM, TOGGLE_BADGE_PILL_CONTRAST);
        final Div divBaudRateRadioButton = this.createDiv(baudRatesRadioButtonGroup, MARGIN_LEFT, MARGIN_10_PX);
        divBaudRateRadioButton.addClassName("div-baud-rate-radio-button");
        final Div div = new Div(divH2BaudRate, divBaudRateRadioButton);
        div.addClassNames(Display.FLEX, LumoUtility.Width.FULL);
        div.getStyle().set(MARGIN_LEFT, MARGIN_10_PX);
        div.addClassName("div-baud-rate-flex-wrap");
        return div;
    }

    private Div rowFlashMode() {
        final H3 h3 = new H3("Flash mode");
        h3.getStyle().set(MARGIN_TOP, AUTO);
        final Div divh3FlashMode = new Div(h3);
        divh3FlashMode.addClassName("div-flash-mode-h3-div");
        this.flashModeRadioButtonGroup.setItems(FlashModeEnum.values());
        this.flashModeRadioButtonGroup.setValue(FlashModeEnum.DUAL_IO);
        this.flashModeRadioButtonGroup.setRequired(Boolean.TRUE);
        this.flashModeRadioButtonGroup.setRenderer(FlashModeEnum.rendererWithTooltip());
        this.flashModeRadioButtonGroup.addClassName("flash-mode-radio-group");
        this.applyRadioButtomTheme(this.flashModeRadioButtonGroup, LumoUtility.Margin.Right.MEDIUM, TOGGLE_BADGE_PILL_CONTRAST);
        final Div divFlashRadioButton = this.createDiv(flashModeRadioButtonGroup, MARGIN_LEFT, MARGIN_10_PX);
        divFlashRadioButton.addClassName("div-flash-mode-radio-button");
        final Div div = new Div(divh3FlashMode, divFlashRadioButton);
        div.addClassNames(Display.FLEX, LumoUtility.Width.FULL);
        div.getStyle().set(MARGIN_LEFT, MARGIN_10_PX);
        div.addClassName("div-flash-mode-flex-wrap");
        return div;
    }

    private Div rowEraseFlash() {
        final H3 h3 = new H3("Erase flash");
        h3.getStyle().set(MARGIN_TOP, AUTO);
        final Div divh3EraseFlash = new Div(h3);
        divh3EraseFlash.addClassName("div-erase-flash-h3-div");
        this.eraseRadioButtons.setRenderer(EraseFlashEnum.rendererWithTooltip());
        this.eraseRadioButtons.setItems(EraseFlashEnum.values());
        this.eraseRadioButtons.setValue(EraseFlashEnum.NO);
        this.eraseRadioButtons.addClassName("erase-flash-radio-group");
        this.applyRadioButtomTheme(this.eraseRadioButtons, LumoUtility.Margin.Right.MEDIUM, TOGGLE_BADGE_PILL_CONTRAST);
        final Div divEraseRadioButton = this.createDiv(eraseRadioButtons, MARGIN_LEFT, MARGIN_10_PX);
        divEraseRadioButton.addClassName("div-erase-radio-group");
        final Div div = new Div(divh3EraseFlash, divEraseRadioButton);
        div.addClassNames(Display.FLEX, LumoUtility.Width.FULL);
        div.getStyle().set(MARGIN_LEFT, MARGIN_10_PX);
        div.addClassName("div-erase-flash-flex-wrap");
        return div;
    }

    public <T> void applyRadioButtomTheme(RadioButtonGroup<T> radioButtonGroup, String marginRight, String theme) {
        radioButtonGroup.getChildren().forEach(component -> {
            component.getElement().getThemeList().add(theme);
            component.addClassName(marginRight);
        });
    }

    private Div rowUploadingFlash() {
        return this.divFlashUploader;
    }

    private Div buttonFlash() {
        final Div divButtonFlash = new Div();
        divButtonFlash.setSizeFull();
        divButtonFlash.getStyle().set(MARGIN_LEFT, MARGIN_10_PX);
        flashButton.addClassName(BOX_SHADOW_VAADIN_BUTTON);
        flashButton.setTooltipText("flash me! -> Execute write_flash");

        flashButton.addClickListener(event -> {
            this.outPutConsole.clear();
            //write flash
            WriteFlashBuilder.builder()
                    .withEsptoolService(this.esptoolService)
                    .withSerialPort(this.divHeaderPorts.getComboBoxSerialPort())
                    .withFlashMode(this.flashModeRadioButtonGroup)
                    .withBaudRate(this.baudRatesRadioButtonGroup)
                    .withFlashSize(DEFAULT_INIT_ADDRESS_SIZE_TO_WRITE_0x_00000)
                    .withUI(UI.getCurrent())
                    .withEraseFlashOption(this.eraseRadioButtons)
                    .withFlashFileName(this.flashFileName)
                    .withOutPutConsole(this.outPutConsole)
                    .withEsptoolPathService(this.esptoolPathService)
                    .withPublisher(this.publishMessageListItem)
                    .make();
        });

        divButtonFlash.add(flashButton);
        return divButtonFlash;
    }

    /**
     * @param ui
     */
    private void outputConsole(final UI ui) {

        this.divHeaderPorts.getComboBoxSerialPort().addValueChangeListener(event -> {
            this.outPutConsole.clear();
            final String valuePort = event.getValue();

            if (Objects.nonNull(event.getValue())) {

                this.commands = new String[]{
                        esptoolPathService.esptoolPath(),
                        PORT, valuePort,
                        BAUD_RATE, String.valueOf(BaudRatesEnum.BAUD_RATE_115200.getBaudRate()),
                        FLASH_ID
                };

                this.subscribeThis(this.esptoolService.readRawFlashIdFromPort(commands), ui);
            }
        });

        this.divHeaderPorts.getButtonExecuteFlashId().addClickListener(event -> {
            this.outPutConsole.clear();
            final String valuePort = this.divHeaderPorts.getComboBoxSerialPort().getValue();

            if (Objects.nonNull(valuePort)) {

                this.commands = new String[]{
                        esptoolPathService.esptoolPath(),
                        PORT, valuePort,
                        BAUD_RATE, String.valueOf(BaudRatesEnum.BAUD_RATE_115200.getBaudRate()),
                        FLASH_ID
                };

                this.subscribeThis(this.esptoolService.readRawFlashIdFromPort(commands), ui);
            }
        });

        this.divHeaderPorts.getComboBoxSerialPort().addValueChangeListener((event) -> {
            this.outPutConsole.clear();
        });

        this.divHeaderPorts.getButtonDebugPort().addClickListener(event -> {
            if (event.isFromClient() && this.divHeaderPorts.getComboBoxSerialPort().getValue() != null) {
                final String port = this.divHeaderPorts.getComboBoxSerialPort().getValue().trim();
                this.outPutConsole.clear();
                this.debugPortService.debugSerialPort(ui, outPutConsole, port);
            } else {
                ConfirmDialogBuilder.showWarningUI("Selecciona un puerto", ui);
            }
        });

    }


    /**
     * @param reactiveLines
     * @param ui
     */
    private void subscribeThis(final Flux<String> reactiveLines, final UI ui) {

        CommandsOnFirstLine.putCommansdOnFirstLine(this.commands, outPutConsole);

        reactiveLines.doOnError((Throwable error) ->
                        ui.access(() -> {
                            log.info("Error: {}", error);
                            this.outPutConsole.writeln(error.getMessage());
                        })
                )
                .doOnComplete(() -> {
                    ui.access(() -> {
                        this.outPutConsole.writePrompt();
                        String chipIs = ExtractChipIsFromStringMapper.INSTANCE.getChipIsFromThisString(this.outPutConsole.scrollBarBuffer());
                        String port = commands[2];

                        final String finalTextNotification = chipIs.contains("This chip cannot be parsed")
                                ? "This chip cannot be parsed executed flash_id failed."
                                : chipIs + " executed flash_id successfully.";

                        final EsptoolFRWMessageListItemEvent esptoolFRWMessageListItemEvent = new EsptoolFRWMessageListItemEvent(finalTextNotification, port);
                        publishMessageListItem.tryEmitNext(esptoolFRWMessageListItemEvent);
                        log.info("publish event {}", esptoolFRWMessageListItemEvent.getText());
                    });
                })
                .subscribe(line ->
                        ui.access(() -> {
                            this.outPutConsole.writeln(line);
                        })
                );

    }

    private void configureHeaderComponents() {
        this.shoWizardIcon.getStyle().setCursor(CURSOR_POINTER);
        this.shoWizardIcon.getStyle().setColor("var(--lumo-contrast-60pct)");
        this.shoWizardIcon.setTooltipText("Show dialog");
        this.toggleButtonEnableWebSerial.setTooltipText("Enable WebSerial");
        this.iconWebSerial.setTooltipText("WebSerial API");
        this.rowForWebSerialIcon.add(toggleButtonEnableWebSerial, iconWebSerial);
        this.rowForWebSerialIcon.getStyle().setBorder("1px solid lightgray");
        this.rowForWebSerialIcon.setAlignItems(FlexComponent.Alignment.CENTER);
        this.rowForWebSerialIcon.getStyle().setBorderRadius("10px");
        this.rowForWebSerialIcon.getStyle().setPadding("5px");

        this.shoWizardIcon.addClickListener(event -> {
            this.add(this.wizardFlashEspDialog);
            this.wizardFlashEspDialog.openAndDisableModeless();
        });
    }

    private void animatedHeaderComponents() {
        Animated.animate(this.shoWizardIcon, Animation.FADE_IN);
        Animated.animate(this.iconWebSerial, Animation.FADE_IN);
        Animated.animate(this.toggleButtonEnableWebSerial, Animation.FADE_IN);
    }

    private void refreshHeaderComponents() {
        this.rowMainHeader.removeAll();
        this.animatedHeaderComponents();
        this.rowMainHeader.add(this.rowForWebSerialIcon, this.shoWizardIcon);
    }

    /**
     * This listener is used to receive the HorizontalLayout that is in the <strong>navbar<strong> and to be
     * able to use it to show in it, components of this view, like the buttons ********
     *
     * @param rowMainHeader from the navbar
     */
    @EventListener
    public void listenerFromMainHeader(final HorizontalLayout rowMainHeader) {
        this.rowMainHeader = rowMainHeader;
    }

    @EventListener
    public void updateFlashFileName(String flashFileName) {
        this.flashFileName = flashFileName;
    }

    /**
     *
     * @param event before navigation event with event details
     */
    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
        this.shoWizardIcon.setVisible(false);
        this.toggleButtonEnableWebSerial.setValue(false);
        this.iconWebSerial.setVisible(false);
        this.rowForWebSerialIcon.setVisible(false);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        this.shoWizardIcon.setVisible(true);
        this.iconWebSerial.setVisible(true);
        this.rowForWebSerialIcon.setVisible(true);
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
            this.outputConsole(ui);
        }
        this.refreshHeaderComponents();
        final UI ui = attachEvent.getUI();
        ui.getPage().executeJs(WINDOWS_LOCATION_REMOVE_HASH)
                .toCompletableFuture()
                .whenCompleteAsync((hash, error) -> {
                    ui.access(() -> {
                        if (Objects.nonNull(hash) && !hash.asString().contains(SETTINGS)) {
                            this.add(this.wizardFlashEspDialog);
                            this.wizardFlashEspDialog.openAndDisableModeless();
                        } else {
                            ui.getPage().fetchCurrentURL(url -> {
                                final String ref = StringUtils.defaultIfEmpty(url.getRef(), StringUtils.EMPTY);
                                if (!ref.contains(SETTINGS)) {
                                    this.wizardFlashEspRepository.findByName(WIZARD_FLASH_ESP_VIEW)
                                            .ifPresent(hide -> {
                                                if (hide.isWizardEnabled()) {
                                                    this.add(this.wizardFlashEspDialog);
                                                    this.wizardFlashEspDialog.openAndDisableModeless();
                                                }
                                            });
                                }
                            });
                        }
                    });
                }, eventTaskExecutor);
    }

}
