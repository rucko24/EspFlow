package com.esp.espflow.views.flashesp;

import com.esp.espflow.service.ComPortService;
import com.esp.espflow.service.CommandService;
import com.esp.espflow.service.EsptoolService;
import com.esp.espflow.util.ConfirmDialogBuilder;
import com.esp.espflow.util.GetOsName;
import com.esp.espflow.util.ResponsiveHeaderDiv;
import com.esp.espflow.util.svgfactory.SvgFactory;
import com.infraleap.animatecss.Animated;
import com.infraleap.animatecss.Animated.Animation;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.esp.espflow.util.EspFlowConstants.AUTO;
import static com.esp.espflow.util.EspFlowConstants.BOX_SHADOW_PROPERTY;
import static com.esp.espflow.util.EspFlowConstants.BOX_SHADOW_VAADIN_BUTTON;
import static com.esp.espflow.util.EspFlowConstants.BOX_SHADOW_VALUE;
import static com.esp.espflow.util.EspFlowConstants.CHANGE_SERIAL_PORT_PERMISSIONS;
import static com.esp.espflow.util.EspFlowConstants.DISPLAY;
import static com.esp.espflow.util.EspFlowConstants.ESPTOOL_PY_NOT_FOUND;
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
            SvgFactory.createIconFromSvg("unlock-gray.svg","30px",null));
    private final ComboBox<String> comboBoxSerialPort = new ComboBox<>();
    private final TextField inputCommand = new TextField();
    private final Button validateInput = new Button(VaadinIcon.PLAY.create());
    private final Button killProcess = new Button(VaadinIcon.STOP.create());
    private final ComPortService comPortService;
    private final CommandService commandService;
    private final EsptoolService esptoolService;
    private final ChangeSerialPortPermissionDialog changeSerialPortPermissionDialog;
    private final H2 h2EsptoolVersion = new H2();
    private AtomicBoolean esptoolVersionCounter = new AtomicBoolean(Boolean.FALSE);

    @PostConstruct
    public void constructDiv() {
        removeAll();
        final var divh3SerialPort = this.createH3SerialPort();
        final var divCombo = this.createDivComboBox();

        scanPort.setTooltipText("Refresh/Scan ports!");
        scanPort.addClassName(BOX_SHADOW_VAADIN_BUTTON);
        unlockPort.setEnabled(false);
        unlockPort.setTooltipText(CHANGE_SERIAL_PORT_PERMISSIONS);
        unlockPort.addClassName(BOX_SHADOW_VAADIN_BUTTON);
        validateInput.addClassName(BOX_SHADOW_VAADIN_BUTTON);
        validateInput.setTooltipText("Execute flash_id");

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
        final Div divInputCommand = this.createDiv(inputCommand, MARGIN, MARGIN_10_PX);
        final Div divReadCommand = this.createDiv(validateInput, MARGIN, MARGIN_10_PX);
        final Div divKillProcess = this.createDiv(killProcess, MARGIN, MARGIN_10_PX);

        divHeader.add(divReadCommand,divScanPort, divUnlockPort);

        final Hr hr = new Hr();
        hr.setHeight("6px");
        hr.getStyle().set("background-image", "linear-gradient(#e0260b, #e0260b),\n" +
                "        linear-gradient(#e0260b, #e0260b)");
        hr.getStyle().set("background-size", "100% 3px, 100% 1px");
        hr.getStyle().set(BOX_SHADOW_PROPERTY, BOX_SHADOW_VALUE);

        final Div divH2espToolVersion = new Div(h2EsptoolVersion, hr);
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
        comboBoxSerialPort.setRenderer(new ComponentRenderer<>(this::getIconItemSubMenu));
        return this.createDiv(this.comboBoxSerialPort, MARGIN, MARGIN_10_PX);
    }

    private Div getIconItemSubMenu(String serialPortName) {
        final Div div = new Div();
        div.addClassNames(LumoUtility.Display.FLEX, LumoUtility.AlignItems.CENTER);
        final SvgIcon icon = SvgFactory.OsIcon("25px", null);
        final Span span = new Span(serialPortName);
        span.addClassNames(LumoUtility.Padding.Left.SMALL);
        div.add(icon, span);
        return div;
    }


    private Div createH3SerialPort() {
        final Div divH3 = new Div(new H3("Serial port"));
        divH3.getStyle().set("white-space", "pre");
        divH3.addClassName("serial-port-h3-div");
        return divH3;
    }

    /**
     * Check if there is a version of esptool.py y la muestra en el H2
     *
     * @param ui
     */
    public void getEspToolVersion(final UI ui) {
        h2EsptoolVersion.addClassName("pulse");

        this.esptoolService.showEsptoolVersion()
                .doOnError(error -> ui.access(() -> {
                    log.error("doOnError: {}", error.getCause());
                    h2EsptoolVersion.setText(ESPTOOL_PY_NOT_FOUND);
                }))
                .subscribe(espToolVersion -> ui.access(() -> {
                    if(espToolVersion.contains("esptool.py v")) {
                        esptoolVersionCounter.set(true);
                    }
                    h2EsptoolVersion.setText(espToolVersion);
                }));
    }

    /**
     *
     * Listener for scanPort and unlockPort
     */
    private void initListeners() {
        this.scanPort.addClickListener(e -> showPortIsEsptoolVersionExists());
        this.unlockPort.addClickListener(event -> this.changeSerialPortPermissionDialog.open(this.comboBoxSerialPort));
    }

    /**
     * Fills in the ports detected in the ComboBox in case of successful scan by the
     * {@link com.fazecast.jSerialComm.SerialPort}
     */
    private void showPortIsEsptoolVersionExists() {
        if(esptoolVersionCounter.get()) {
            final List<String> ports = this.comPortService.getOnlyPortsList();
            if (!ports.isEmpty()) {
                if(GetOsName.getOsName() != GetOsName.WINDOWS) {
                    unlockPort.setEnabled(true);
                } else {
                    unlockPort.setEnabled(false);
                }
                comboBoxSerialPort.setItems(ports); //set port items to combo
                comboBoxSerialPort.setValue(ports.getFirst());
                ConfirmDialogBuilder.showInformation("Port found!");
                this.unlockPort.setIcon(SvgFactory.createIconFromSvg(
                        "unlock-black.svg","30px",null));
                Animated.animate(unlockPort, Animation.FADE_IN);
            } else {
                comboBoxSerialPort.setItems(List.of());
                ConfirmDialogBuilder.showWarning("Port not found!");
            }
        } else {
            ConfirmDialogBuilder.showWarning("Verify if esptool.py is installed!!!");
        }
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        if (attachEvent.isInitialAttach()) {
            final UI ui = attachEvent.getUI();
            this.initListeners();
            this.getEspToolVersion(ui);
        }
    }

}
