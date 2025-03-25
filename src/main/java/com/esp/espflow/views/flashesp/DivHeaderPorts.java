package com.esp.espflow.views.flashesp;

import com.esp.espflow.entity.event.EsptoolVersionMessageListItemEvent;
import com.esp.espflow.enums.GetOsName;
import com.esp.espflow.service.ComPortService;
import com.esp.espflow.service.CommandService;
import com.esp.espflow.service.EsptoolService;
import com.esp.espflow.service.respository.impl.EsptoolExecutableServiceImpl;
import com.esp.espflow.util.ConfirmDialogBuilder;
import com.esp.espflow.util.ResponsiveHeaderDiv;
import com.esp.espflow.util.svgfactory.SvgFactory;
import com.esp.espflow.views.dialog.ChangeSerialPortPermissionDialog;
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
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.textfield.TextField;
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

import static com.esp.espflow.util.EspFlowConstants.AUTO;
import static com.esp.espflow.util.EspFlowConstants.BLACK_TO_WHITE_ICON;
import static com.esp.espflow.util.EspFlowConstants.BOX_SHADOW_PROPERTY;
import static com.esp.espflow.util.EspFlowConstants.BOX_SHADOW_VAADIN_BUTTON;
import static com.esp.espflow.util.EspFlowConstants.BOX_SHADOW_VALUE;
import static com.esp.espflow.util.EspFlowConstants.CHANGE_SERIAL_PORT_PERMISSIONS;
import static com.esp.espflow.util.EspFlowConstants.CONTEXT_MENU_ITEM_NO_CHECKMARK;
import static com.esp.espflow.util.EspFlowConstants.DISPLAY;
import static com.esp.espflow.util.EspFlowConstants.ESPTOOL_PY_NOT_FOUND;
import static com.esp.espflow.util.EspFlowConstants.ESPTOOL_PY_V;
import static com.esp.espflow.util.EspFlowConstants.EXECUTABLE_ICON;
import static com.esp.espflow.util.EspFlowConstants.MARGIN;
import static com.esp.espflow.util.EspFlowConstants.MARGIN_10_PX;
import static com.esp.espflow.util.EspFlowConstants.MARGIN_TOP;

@Log4j2
@Getter
@RequiredArgsConstructor
@UIScope
@SpringComponent
public class DivHeaderPorts extends Div implements ResponsiveHeaderDiv {

    private final Button scanPort = new Button(VaadinIcon.REFRESH.create());
    private final Button unlockPort = new Button(
            SvgFactory.createIconFromSvg("unlock-gray.svg", "30px", null));
    private final ComboBox<String> comboBoxSerialPort = new ComboBox<>();
    private final TextField inputCommand = new TextField();
    private final Button buttonExecuteFlashId = new Button(VaadinIcon.PLAY.create());
    private final Button killProcess = new Button(VaadinIcon.STOP.create());
    private final ComPortService comPortService;
    private final CommandService commandService;
    private final EsptoolService esptoolService;
    private final ChangeSerialPortPermissionDialog changeSerialPortPermissionDialog;
    private final H2 h2EsptoolVersion = new H2();
    private final ProgressBar progressBarForShowEsptoolVersion = new ProgressBar();
    private AtomicBoolean esptoolVersionCounter = new AtomicBoolean(Boolean.FALSE);
    private final Flux<EsptoolVersionMessageListItemEvent> subscriberEsptoolVersionEvent;
    private final Sinks.Many<EsptoolVersionMessageListItemEvent> publishEstoolVersionEvent;
    private final EsptoolExecutableServiceImpl esptoolExecutableServiceImpl;
    private final ContextMenu contextMenu = new ContextMenu(h2EsptoolVersion);
    private Disposable disposableSubscriberEsptoolVersionEvent;

    @PostConstruct
    public void constructDiv() {
        removeAll();
        final var divh3SerialPort = this.createH3SerialPort();
        final var divCombo = this.createDivComboBox();

        scanPort.setTooltipText("Refresh/Scan ports! - ENTER");
        scanPort.addClassName(BOX_SHADOW_VAADIN_BUTTON);
        scanPort.addClickShortcut(Key.ENTER);
        unlockPort.setEnabled(false);
        unlockPort.setTooltipText(CHANGE_SERIAL_PORT_PERMISSIONS.concat(" - SPACE"));
        unlockPort.addClickShortcut(Key.SPACE);
        unlockPort.addClassName(BOX_SHADOW_VAADIN_BUTTON);
        buttonExecuteFlashId.setEnabled(false);
        buttonExecuteFlashId.addClassName(BOX_SHADOW_VAADIN_BUTTON);
        buttonExecuteFlashId.setTooltipText("Execute flash_id");

        //inputCommand.setPlaceholder("input command");
        //inputCommand.setClearButtonVisible(Boolean.TRUE);
        //killProcess.setEnabled(false);
        //killProcess.addThemeVariants(ButtonVariant.LUMO_ERROR);

        final Div divHeader = new Div(divh3SerialPort, divCombo);
        divHeader.setWidthFull();
        divHeader.getStyle().set(DISPLAY, "flex");
        divHeader.getStyle().set(MARGIN, "10px 10px");
        divHeader.getStyle().set("align-items", "baseline");

        final Div divScanPort = this.createDiv(scanPort, MARGIN, MARGIN_10_PX);
        final Div divUnlockPort = this.createDiv(unlockPort, MARGIN, MARGIN_10_PX);
        //final Div divInputCommand = this.createDiv(inputCommand, MARGIN, MARGIN_10_PX);
        final Div divReadCommand = this.createDiv(buttonExecuteFlashId, MARGIN, MARGIN_10_PX);
        //final Div divKillProcess = this.createDiv(killProcess, MARGIN, MARGIN_10_PX);

        divHeader.add(divReadCommand, divScanPort, divUnlockPort);

        final Hr hr = new Hr();
        hr.setHeight("6px");
        hr.getStyle().set("background-image", "linear-gradient(#e0260b, #e0260b), linear-gradient(#e0260b, #e0260b)");
        hr.getStyle().set("background-size", "100% 3px, 100% 1px");
        hr.getStyle().set(BOX_SHADOW_PROPERTY, BOX_SHADOW_VALUE);

        this.progressBarForShowEsptoolVersion.setVisible(false);
        this.progressBarForShowEsptoolVersion.setIndeterminate(true);
        this.progressBarForShowEsptoolVersion.setWidth("200px");
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
        comboBoxSerialPort.setPrefixComponent(SvgFactory.OsIcon("30px", null));
        comboBoxSerialPort.setRenderer(rendererIconUsbForEachItem());
        return this.createDiv(this.comboBoxSerialPort, MARGIN, MARGIN_10_PX);
    }

    private ComponentRenderer<Div, String> rendererIconUsbForEachItem() {
        final SerializableBiConsumer<Div, String> serializableBiConsumer = (div, itemName) -> {
            div.addClassNames(LumoUtility.Display.FLEX, LumoUtility.AlignItems.CENTER);
            final SvgIcon icon = SvgFactory.createIconFromSvg("usb-port-black.svg", "25px", null);
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
                    ui.access(() -> {
                        this.progressBarForShowEsptoolVersion.setVisible(false);
                        h2EsptoolVersion.addClassNames("pulse", "pulse-dark");
                    });
                })
                .subscribe(espToolVersion -> {
                    ui.access(() -> {
                        if (espToolVersion.contains(ESPTOOL_PY_V)) {
                            esptoolVersionCounter.set(true);
                        }
                        h2EsptoolVersion.setText(espToolVersion);
                        Animated.animate(h2EsptoolVersion, Animation.FADE_IN);
                    });
                });
    }

    private void putItemEsptool() {
        contextMenu.removeAll();
        esptoolExecutableServiceImpl.findByIsSelectedToTrue()
                .ifPresent(esptoolExecutableDto -> {
                    final EsptoolVersionMessageListItemEvent.EsptoolVersionEventEnum loadedType = esptoolExecutableDto.isBundled()
                            ? EsptoolVersionMessageListItemEvent.EsptoolVersionEventEnum.BUNDLED
                            : EsptoolVersionMessageListItemEvent.EsptoolVersionEventEnum.CUSTOM;
                    this.createToolTip(h2EsptoolVersion, loadedType.getExecutableType());
                });

        esptoolExecutableServiceImpl.findAll()
                .forEach(esptoolExecutableDto -> {
                    final MenuItem item = contextMenu.addItem(this.createIconItemEsptoolVersionContext(esptoolExecutableDto.esptoolVersion()), menuItemClickEvent -> {
                        //This version of esptool.py will be used and selected via context
                        this.esptoolExecutableServiceImpl.selectThisEsptoolExecutableVersion(esptoolExecutableDto);
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
    }

    private Div createIconItemEsptoolVersionContext(String esptoolVersion) {
        final Div div = new Div();
        div.addClassNames(LumoUtility.Display.FLEX, LumoUtility.AlignItems.CENTER);
        final SvgIcon icon = SvgFactory.createIconFromSvg(EXECUTABLE_ICON, "20px", "20px");
        icon.addClassName(BLACK_TO_WHITE_ICON);
        final Span span = new Span(esptoolVersion);
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
                ConfirmDialogBuilder.showInformation("Port found!");
                this.unlockPort.setIcon(SvgFactory.createIconFromSvg("unlock-black.svg", "30px", null));
                this.unlockPort.getIcon().addClassName(BLACK_TO_WHITE_ICON);
                buttonExecuteFlashId.setEnabled(true);
                Animated.animate(buttonExecuteFlashId, Animation.FADE_IN);
                Animated.animate(unlockPort, Animation.FADE_IN);
            } else {
                buttonExecuteFlashId.setEnabled(false);
                comboBoxSerialPort.setItems(List.of());
                ConfirmDialogBuilder.showWarning("Port not found!");
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
        this.initListeners();
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
