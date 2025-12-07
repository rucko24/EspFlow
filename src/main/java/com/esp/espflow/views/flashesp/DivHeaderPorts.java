package com.esp.espflow.views.flashesp;

import com.esp.espflow.enums.GetOsName;
import com.esp.espflow.event.EspflowMessageListItemEvent;
import com.esp.espflow.event.EsptoolVersionMessageListItemEvent;
import com.esp.espflow.service.ComPortService;
import com.esp.espflow.service.CommandService;
import com.esp.espflow.service.EsptoolService;
import com.esp.espflow.service.respository.impl.EsptoolExecutableService;
import com.esp.espflow.util.ConfirmDialogBuilder;
import com.esp.espflow.util.ResponsiveHeaderDiv;
import com.esp.espflow.util.svgfactory.SvgFactory;
import com.esp.espflow.views.dialog.ChangeSerialPortPermissionDialog;
import com.esp.espflow.views.settings.SettingsDialog;
import com.infraleap.animatecss.Animated;
import com.infraleap.animatecss.Animated.Animation;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.UIDetachedException;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import static com.esp.espflow.util.EspFlowConstants.AUTO;
import static com.esp.espflow.util.EspFlowConstants.BLACK_TO_WHITE_ICON;
import static com.esp.espflow.util.EspFlowConstants.BOX_SHADOW_PROPERTY;
import static com.esp.espflow.util.EspFlowConstants.BOX_SHADOW_VAADIN_BUTTON;
import static com.esp.espflow.util.EspFlowConstants.BOX_SHADOW_VALUE;
import static com.esp.espflow.util.EspFlowConstants.CHANGE_SERIAL_PORT_PERMISSIONS;
import static com.esp.espflow.util.EspFlowConstants.CONTEXT_MENU_ITEM_NO_CHECKMARK;
import static com.esp.espflow.util.EspFlowConstants.CONTEXT_MENU_ITEM_NO_CHECKMARK_BOTTOM;
import static com.esp.espflow.util.EspFlowConstants.CURSOR_POINTER;
import static com.esp.espflow.util.EspFlowConstants.DISPLAY;
import static com.esp.espflow.util.EspFlowConstants.ERROR_NOT_FOUND;
import static com.esp.espflow.util.EspFlowConstants.ESPTOOL_PY_NOT_FOUND;
import static com.esp.espflow.util.EspFlowConstants.ESPTOOL_PY_V;
import static com.esp.espflow.util.EspFlowConstants.EXECUTABLE_ICON;
import static com.esp.espflow.util.EspFlowConstants.ICONS_RESPONSIVE_SIZE;
import static com.esp.espflow.util.EspFlowConstants.MARGIN;
import static com.esp.espflow.util.EspFlowConstants.MARGIN_10_PX;
import static com.esp.espflow.util.EspFlowConstants.MARGIN_TOP;
import static com.esp.espflow.util.EspFlowConstants.PORT_FOUND;
import static com.esp.espflow.util.EspFlowConstants.PORT_NOT_FOUND;
import static com.esp.espflow.util.EspFlowConstants.SETTINGS;
import static com.esp.espflow.util.EspFlowConstants.SETTINGS_SHARP;
import static com.esp.espflow.util.EspFlowConstants.SIZE_20_PX;
import static com.esp.espflow.util.EspFlowConstants.SIZE_25_PX;
import static com.esp.espflow.util.EspFlowConstants.SIZE_30_PX;
import static com.esp.espflow.util.EspFlowConstants.UNLOCK_BLACK_SVG;
import static com.esp.espflow.util.EspFlowConstants.UPDATE_ICON;

/**
 * @author rubn
 */
@Log4j2
@Getter
@RequiredArgsConstructor
@UIScope
@SpringComponent
public class DivHeaderPorts extends Div implements ResponsiveHeaderDiv {

    public static final String VISIBILITY = "visibility";
    public static final String HIDDEN = "hidden";
    public static final String DISPLAY1 = "display";
    private final H2 h2EsptoolVersion = new H2();
    private final ComboBox<String> comboBoxSerialPort = new ComboBox<>();
    private final Button buttonDebugPort = new Button(VaadinIcon.SEARCH.create());
    private final Button buttonStopDebugPort = new Button(VaadinIcon.STOP.create());
    private final Button scanPort = new Button(VaadinIcon.REFRESH.create());
    private final Button buttonExecuteFlashId = new Button(VaadinIcon.PLAY.create());
    private final SvgIcon svgIconUnlock = SvgFactory.createIconFromSvg("unlock-gray.svg", SIZE_30_PX, null);
    private final Button unlockPort = new Button(svgIconUnlock);
    private final ContextMenu contextMenu = new ContextMenu(h2EsptoolVersion);
    private final ProgressBar progressBarForShowEsptoolVersion = new ProgressBar();
    private final AtomicBoolean esptoolVersionCounter = new AtomicBoolean(Boolean.FALSE);
    /**
     * Services
     */
    private final Flux<EsptoolVersionMessageListItemEvent> subscriberEsptoolVersionEvent;
    private final Sinks.Many<EsptoolVersionMessageListItemEvent> publishEstoolVersionEvent;
    private final Sinks.Many<EspflowMessageListItemEvent> publishEspflowMessageListItemEvent;
    private final EsptoolExecutableService esptoolExecutableService;
    private final ComPortService comPortService;
    private final CommandService commandService;
    private final EsptoolService esptoolService;
    private final ChangeSerialPortPermissionDialog changeSerialPortPermissionDialog;
    private final SettingsDialog settingsDialog;
    /**
     * Mutable fields
     */
    private Disposable disposableSubscriberEsptoolVersionEvent;

    @PostConstruct
    public void constructDiv() {
        removeAll();
        final var divh3SerialPort = this.createH3SerialPort();
        final var divCombo = this.createDivComboBox();

        scanPort.setTooltipText("Refresh/Scan ports! - ENTER");
        scanPort.addClassName(BOX_SHADOW_VAADIN_BUTTON);
        scanPort.addClickShortcut(Key.ENTER);
        svgIconUnlock.addClassName(ICONS_RESPONSIVE_SIZE);
        unlockPort.setTooltipText(CHANGE_SERIAL_PORT_PERMISSIONS.concat(" - SPACE"));
        unlockPort.addClickShortcut(Key.SPACE);
        unlockPort.addClassName(BOX_SHADOW_VAADIN_BUTTON);
        buttonDebugPort.setTooltipText("Debug serial port");
        buttonDebugPort.addClassName(BOX_SHADOW_VAADIN_BUTTON);

        buttonStopDebugPort.setTooltipText("Stop debug serial por");
        buttonStopDebugPort.addClassName(BOX_SHADOW_VAADIN_BUTTON);

        buttonExecuteFlashId.setEnabled(false);
        buttonExecuteFlashId.addClassName(BOX_SHADOW_VAADIN_BUTTON);
        buttonExecuteFlashId.setTooltipText("Execute flash_id");
        this.buttonDebugPort.setEnabled(false);
        this.buttonStopDebugPort.setEnabled(false);
        this.initListeners();

        final Div divHeader = new Div(divh3SerialPort, divCombo);
        divHeader.setWidthFull();
        divHeader.getStyle().set(DISPLAY, "flex");
        divHeader.getStyle().set(MARGIN, "10px 10px");
        divHeader.addClassName(LumoUtility.AlignItems.BASELINE);
        final HorizontalLayout horizontalLayout = new HorizontalLayout();
        final Div div1 = createDiv(buttonExecuteFlashId, MARGIN,"0px");
        final Span spanRefresh = new Span("Refresh");
        spanRefresh.getStyle().set(VISIBILITY, HIDDEN);
        spanRefresh.getStyle().set(DISPLAY1,"none");
        div1.add(spanRefresh);
        final Div div2 = createDiv(scanPort, MARGIN,"0px");
        final Span spanScanPorts = new Span("Scan ports");
        spanScanPorts.getStyle().set(VISIBILITY, HIDDEN);
        spanScanPorts.getStyle().set(DISPLAY1,"none");
        div2.add(spanScanPorts);
        final Div div3 = createDiv(unlockPort, MARGIN,"0px");
        final Span spanUnlockPorts = new Span("Unlock ports");
        spanUnlockPorts.getStyle().set(VISIBILITY, HIDDEN);
        spanUnlockPorts.getStyle().set(DISPLAY1,"none");
        div3.add(spanUnlockPorts);
        final Div div4 = createDiv(buttonDebugPort, MARGIN,"0px");
        final Span spanDebugPort = new Span("Debug port");
        spanDebugPort.getStyle().set(VISIBILITY, HIDDEN);
        spanDebugPort.getStyle().set(DISPLAY1,"none");
        final Div div5 = createDiv(buttonStopDebugPort, MARGIN,"0px");
        final Span spanStopDebugPort = new Span("Stop Debug port");
        spanStopDebugPort.getStyle().set(VISIBILITY, HIDDEN);
        spanStopDebugPort.getStyle().set(DISPLAY1,"none");

        Stream.of(spanRefresh,spanUnlockPorts,spanScanPorts,spanDebugPort, spanStopDebugPort).forEach(span -> {
            span.addClassName("span-text");
            span.addClassNames(LumoUtility.TextColor.SECONDARY,LumoUtility.FontSize.SMALL);
        });
        horizontalLayout.add(div1,div2,div3,div4,div5);
        Stream.of(div1,div2,div3,div4,div5).forEach(div -> div.addClassName("expand-buttons"));
        horizontalLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        horizontalLayout.addClassName("row-buttons");

        divHeader.add(horizontalLayout);

        final Hr hr = new Hr();
        hr.setHeight("6px");
        hr.getStyle().set("background-image", "linear-gradient(#e0260b, #e0260b), linear-gradient(#e0260b, #e0260b)");
        hr.getStyle().set("background-size", "100% 3px, 100% 1px");
        hr.getStyle().set(BOX_SHADOW_PROPERTY, BOX_SHADOW_VALUE);

        this.progressBarForShowEsptoolVersion.setVisible(false);
        this.progressBarForShowEsptoolVersion.setIndeterminate(true);
        this.progressBarForShowEsptoolVersion.setWidth("200px");
        this.h2EsptoolVersion.getStyle().setCursor(CURSOR_POINTER);
        final Div divH2espToolVersion = new Div(h2EsptoolVersion, this.progressBarForShowEsptoolVersion, hr);
        divH2espToolVersion.getStyle().set(MARGIN_TOP, AUTO);

        final Div divEndForH2EspToolVersion = new Div(divH2espToolVersion);
        divEndForH2EspToolVersion.setWidthFull();
        divEndForH2EspToolVersion.getStyle().set(DISPLAY, "flex");
        divEndForH2EspToolVersion.getStyle().set("justify-content", "center");

        divHeader.addClassName("header-flex-wrap");

        super.add(divHeader, divEndForH2EspToolVersion);
        super.setWidthFull();
        super.getStyle().set(DISPLAY, "flex");
        super.getStyle().set("justify-content", "space-around");
        super.addClassName("vertical-flex-wrap");

    }

    private Div createDivComboBox() {
        comboBoxSerialPort.setClearButtonVisible(true);
        comboBoxSerialPort.setWidthFull();
        comboBoxSerialPort.setPlaceholder("port");
        comboBoxSerialPort.setPrefixComponent(SvgFactory.osIcon(SIZE_30_PX, null));
        comboBoxSerialPort.setRenderer(this.rendererIconUsbForEachItem());
        var divCombo = this.createDiv(this.comboBoxSerialPort, MARGIN, MARGIN_10_PX);
        divCombo.addClassName("div-combo-serial-port");
        return divCombo;
    }

    private ComponentRenderer<Div, String> rendererIconUsbForEachItem() {
        final SerializableBiConsumer<Div, String> serializableBiConsumer = (div, itemName) -> {
            div.addClassNames(LumoUtility.Display.FLEX, LumoUtility.AlignItems.CENTER);
            final SvgIcon icon = SvgFactory.createIconFromSvg("usb-port-black.svg", SIZE_25_PX, null);
            icon.addClassName(BLACK_TO_WHITE_ICON);
            final Span span = new Span(itemName);
            span.addClassNames(LumoUtility.Padding.Left.SMALL);
            div.add(icon, span);
        };
        return new ComponentRenderer<>(Div::new, serializableBiConsumer);
    }

    private Div createH3SerialPort() {
        final Div divH3 = new Div(new H3("Serial port"));
        divH3.getStyle().set("white-space", "pre");
        divH3.addClassName("serial-port-h3-div");
        return divH3;
    }

    /**
     * <p>Check if there is a version of <strong>esptool.py</strong> to show it in an H2</p>
     *
     * @param ui which comes from onAttach
     */
    public void updateH2WithEsptoolVersion(final UI ui) {
        this.putItemEsptool();
        this.progressBarForShowEsptoolVersion.setVisible(true);
        this.esptoolService.showEsptoolVersion()
                .doOnError(error -> ui.access(() -> {
                    log.error("doOnError: {}", error.getCause());
                    h2EsptoolVersion.setText(ESPTOOL_PY_NOT_FOUND);
                    this.progressBarForShowEsptoolVersion.setVisible(false);
                    h2EsptoolVersion.addClassNames("pulse", "pulse-dark");
                }))
                .doOnComplete(() -> {
                    try {
                        ui.access(() -> {
                            this.progressBarForShowEsptoolVersion.setVisible(false);
                            h2EsptoolVersion.addClassNames("pulse", "pulse-dark");
                        });
                    } catch (UIDetachedException ex){}
                })
                .subscribe(espToolVersion -> {
                    try {
                        ui.access(() -> {
                            if (espToolVersion.contains(ESPTOOL_PY_V)) {
                                esptoolVersionCounter.set(true);
                            }
                            h2EsptoolVersion.setText(espToolVersion);
                            Animated.animate(h2EsptoolVersion, Animation.FADE_IN);
                        });
                    } catch (UIDetachedException ex) {}

                });
    }

    private void putItemEsptool() {
        contextMenu.removeAll();
        esptoolExecutableService.findByIsSelectedToTrue()
                .ifPresent(esptoolExecutableDto -> {
                    final EsptoolVersionMessageListItemEvent.EsptoolVersionEventEnum loadedType = esptoolExecutableDto.isBundled()
                            ? EsptoolVersionMessageListItemEvent.EsptoolVersionEventEnum.BUNDLED
                            : EsptoolVersionMessageListItemEvent.EsptoolVersionEventEnum.CUSTOM;
                    this.createToolTip(h2EsptoolVersion, loadedType.getExecutableType());
                });

        esptoolExecutableService.findAll()
                .forEach(esptoolExecutableDto -> {
                    final MenuItem item = contextMenu.addItem(this.createIconItemEsptoolVersionContext(esptoolExecutableDto.esptoolVersion()), menuItemClickEvent -> {
                        //This version of esptool.py will be used and selected via context
                        this.esptoolExecutableService.selectThisEsptoolExecutableVersion(esptoolExecutableDto);
                        this.h2EsptoolVersion.setText(esptoolExecutableDto.esptoolVersion());
                        final EsptoolVersionMessageListItemEvent.EsptoolVersionEventEnum loadedType = esptoolExecutableDto.isBundled()
                                ? EsptoolVersionMessageListItemEvent.EsptoolVersionEventEnum.BUNDLED
                                : EsptoolVersionMessageListItemEvent.EsptoolVersionEventEnum.CUSTOM;
                        final EsptoolVersionMessageListItemEvent event = new EsptoolVersionMessageListItemEvent(loadedType,
                                esptoolExecutableDto.esptoolVersion(), esptoolExecutableDto.absolutePathEsptool());
                        this.publishEstoolVersionEvent.tryEmitNext(event);
                    });
                    item.addClassName(CONTEXT_MENU_ITEM_NO_CHECKMARK);
                    String isBundled = esptoolExecutableDto.isBundled() ? "Bundled" : "Custom";
                    this.createToolTip(item, isBundled);
                });

        contextMenu.addItem(this.createIconItemSettingsContext(), menuItemClickEvent -> {
                    menuItemClickEvent.getSource().getUI().ifPresent(ui -> {
                        ui.getPage().fetchCurrentURL(url -> {
                            String urlWithParameters = url.getPath().concat(SETTINGS_SHARP);
                            ui.getPage().getHistory().replaceState(null, urlWithParameters);
                            settingsDialog.open(SETTINGS);
                        });
                    });
                })
                .addClassName(CONTEXT_MENU_ITEM_NO_CHECKMARK_BOTTOM);
    }

    private Div createIconItemEsptoolVersionContext(String esptoolVersion) {
        final Div div = new Div();
        div.addClassNames(LumoUtility.Display.FLEX, LumoUtility.AlignItems.CENTER);
        final SvgIcon icon = SvgFactory.createIconFromSvg(EXECUTABLE_ICON, SIZE_20_PX, SIZE_20_PX);
        icon.addClassName(BLACK_TO_WHITE_ICON);
        final Span span = new Span(esptoolVersion);
        span.addClassNames(LumoUtility.Padding.Left.SMALL);
        div.add(icon, span);
        return div;
    }

    private Div createIconItemSettingsContext() {
        final Div div = new Div();
        div.addClassNames(LumoUtility.Display.FLEX, LumoUtility.AlignItems.CENTER);
        final SvgIcon icon = SvgFactory.createIconFromSvg(UPDATE_ICON, SIZE_20_PX, SIZE_20_PX);
        icon.addClassName(BLACK_TO_WHITE_ICON);
        final Span span = new Span("Upload a custom esptool");
        Tooltip.forComponent(span).setText("Ctrl+Alt+S");
        span.addClassNames(LumoUtility.Padding.Left.SMALL);
        div.add(icon, span);
        return div;
    }

    /**
     * This creates a tooltip to show the version of esptool.py, at the top of the tooltip
     *
     * @param component
     * @param text
     */
    private void createToolTip(Component component, String text) {
        Tooltip tooltip = Tooltip.forComponent(component);
        tooltip.setText(text);
        tooltip.setPosition(Tooltip.TooltipPosition.TOP);
    }

    /**
     * Listener for scanPort and unlockPort
     */
    private void initListeners() {
        this.scanPort.addClickListener(e -> showPortIsEsptoolVersionExists());
        this.unlockPort.addClickListener(event -> this.changeSerialPortPermissionDialog.setComboBoxWithErrorsInPorts(this.comboBoxSerialPort));
    }

    /**
     * Fills in the ports detected in the ComboBox in case of successful scan by the
     * {@link com.fazecast.jSerialComm.SerialPort}
     */
    private void showPortIsEsptoolVersionExists() {
        if (esptoolVersionCounter.get()) {

            final List<String> ports = this.comPortService.getOnlyPortsList()
                    .stream()
                    .sorted(Comparator.comparing(Objects::toString))
                    .toList();

            if (!ports.isEmpty()) {
                if (GetOsName.getOsName() != GetOsName.WINDOWS) {
                    unlockPort.setEnabled(true);
                    unlockPort.getIcon().addClassName(BLACK_TO_WHITE_ICON);
                } else {
                    unlockPort.setTooltipText("Not available for Windows");
                    unlockPort.addClickListener(event -> {
                        Notification.show("Not available for Windows", 2000, Notification.Position.MIDDLE);
                    });
                }
                comboBoxSerialPort.setItems(ports); //set port items to combo
                scanPort.getUI().ifPresent(ui -> {
                   ui.access(() -> ConfirmDialogBuilder.showInformationUI(PORT_FOUND, ui));
                });
                this.unlockPort.setIcon(SvgFactory.createIconFromSvg(UNLOCK_BLACK_SVG, SIZE_30_PX, null));
                this.unlockPort.getIcon().addClassName(BLACK_TO_WHITE_ICON);
                this.buttonExecuteFlashId.setEnabled(true);
                this.buttonDebugPort.setEnabled(true);
                this.buttonStopDebugPort.setEnabled(true);
                Animated.animate(buttonExecuteFlashId, Animation.FADE_IN);
                Animated.animate(unlockPort, Animation.FADE_IN);
                Animated.animate(buttonDebugPort, Animation.FADE_IN);
            } else {
                this.buttonDebugPort.setEnabled(true);
                this.buttonStopDebugPort.setEnabled(true);
                this.buttonExecuteFlashId.setEnabled(false);
                this.comboBoxSerialPort.setItems(List.of());
                scanPort.getUI().ifPresent(ui -> {
                    ui.getPage().fetchCurrentURL(url -> {
                        if (url.getRef() != null) {
                            if (!url.getRef().contains(SETTINGS)) {
                                ConfirmDialogBuilder.showWarningUI(PORT_NOT_FOUND, ui);
                                this.publishEspflowMessageListItemEvent.tryEmitNext(new EspflowMessageListItemEvent(PORT_NOT_FOUND, "Flash-View", ERROR_NOT_FOUND));
                            }
                        } else {
                            ConfirmDialogBuilder.showWarningUI(PORT_NOT_FOUND, ui);
                            this.publishEspflowMessageListItemEvent.tryEmitNext(new EspflowMessageListItemEvent(PORT_NOT_FOUND, "Flash-View", ERROR_NOT_FOUND));
                        }
                    });
                });
            }
        } else {
            ConfirmDialogBuilder.showWarning("Verify if esptool.py is installed!!!");
        }
    }

    private void closeSubscribers() {
        if (this.disposableSubscriberEsptoolVersionEvent != null) {
            this.disposableSubscriberEsptoolVersionEvent.dispose();
            this.disposableSubscriberEsptoolVersionEvent = null;
        }
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        this.closeSubscribers();
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        this.closeSubscribers();
        final UI ui = attachEvent.getUI();
        this.updateH2WithEsptoolVersion(ui);
        this.disposableSubscriberEsptoolVersionEvent = this.subscriberEsptoolVersionEvent
                .subscribe(esptoolVersionMessageListItemEvent -> {
                    try {
                        ui.access(() -> {
                            h2EsptoolVersion.setText(esptoolVersionMessageListItemEvent.getEsptoolVersion());
                            this.putItemEsptool();
                            this.createToolTip(h2EsptoolVersion, esptoolVersionMessageListItemEvent.getEsptoolVersionEventEnum().getExecutableType());
                            log.info("Subscribe EsptoolVersionEvent: {}", esptoolVersionMessageListItemEvent.getEsptoolVersion());
                        });
                    } catch (UIDetachedException ex) {
                    }
                });
    }

}
