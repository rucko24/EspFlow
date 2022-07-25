package com.nodemcutools.application.views.flashesp32;

import com.nodemcutools.application.data.service.ComPortService;
import com.nodemcutools.application.data.service.CommandService;
import com.nodemcutools.application.data.util.NotificationBuilder;
import com.nodemcutools.application.data.util.ResponsiveHeaderDiv;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import javax.annotation.PostConstruct;
import java.util.Set;
import java.util.stream.Collectors;

import static com.nodemcutools.application.data.util.UiToolConstants.AUTO;
import static com.nodemcutools.application.data.util.UiToolConstants.BOX_SHADOW_PROPERTY;
import static com.nodemcutools.application.data.util.UiToolConstants.BOX_SHADOW_VALUE;
import static com.nodemcutools.application.data.util.UiToolConstants.DISPLAY;
import static com.nodemcutools.application.data.util.UiToolConstants.MARGIN;
import static com.nodemcutools.application.data.util.UiToolConstants.MARGIN_10_PX;
import static com.nodemcutools.application.data.util.UiToolConstants.MARGIN_TOP;

@Log4j2
@Getter
@RequiredArgsConstructor
@UIScope
@SpringComponent
public class DivHeaderPorts extends Div implements ResponsiveHeaderDiv {

    private final Button scanPort = new Button(VaadinIcon.REFRESH.create());
    private final ComboBox<String> comboBoxSerialPort = new ComboBox<>();
    private final TextField inputCommand = new TextField();
    private final Button validateInput = new Button(VaadinIcon.CHECK.create());
    private final Button killProcess = new Button(VaadinIcon.STOP.create());
    private final ComPortService comPortService;
    private final CommandService commandService;

    @PostConstruct
    public void constructDiv() {
        removeAll();
        final var divh3SerialPort = this.createH3SerialPort();
        final var divCombo = this.createDivComboBox();

        inputCommand.setPlaceholder("input command");
        inputCommand.setClearButtonVisible(Boolean.TRUE);
        killProcess.addThemeVariants(ButtonVariant.LUMO_ERROR);

        final Div divHeader = new Div(divh3SerialPort, divCombo);
        divHeader.setWidthFull();
        divHeader.getStyle().set(DISPLAY, "flex");
        divHeader.getStyle().set(MARGIN, "10px 10px");
        divHeader.getStyle().set("align-items", "baseline");

        final Div divScanPort = this.createDiv(scanPort, MARGIN, MARGIN_10_PX);
        final Div divInputCommand = this.createDiv(inputCommand, MARGIN, MARGIN_10_PX);
        divHeader.add(divScanPort, divInputCommand);

        final Div divReadCommand = this.createDiv(validateInput, MARGIN, MARGIN_10_PX);
        final Div divKillProcess = this.createDiv(killProcess, MARGIN, MARGIN_10_PX);
        divHeader.add(divReadCommand, divKillProcess);

        final Hr hr = new Hr();
        hr.setHeight("6px");
        hr.getStyle().set("background-image", "linear-gradient(#e0260b, #e0260b),\n" +
                "        linear-gradient(#e0260b, #e0260b)");
        hr.getStyle().set("background-size", "100% 3px, 100% 1px");
        hr.getStyle().set(BOX_SHADOW_PROPERTY, BOX_SHADOW_VALUE);

        final H2 h2EspToolVersion = getEspToolVersion();
        final Div divH2espToolVersion = new Div(h2EspToolVersion, hr);
        divH2espToolVersion.getStyle().set(MARGIN_TOP, AUTO);

        final Div divEndForH2EspToolVersion = new Div(divH2espToolVersion);
        divEndForH2EspToolVersion.setWidthFull();
        divEndForH2EspToolVersion.getStyle().set(DISPLAY, "flex");
        divEndForH2EspToolVersion.getStyle().set("justify-content", "center");

        super.add(divHeader, divEndForH2EspToolVersion);
        super.setWidthFull();
        super.getStyle().set(DISPLAY, "flex");
        super.getStyle().set("justify-content", "space-around");

    }

    private Div createDivComboBox() {
        comboBoxSerialPort.setClearButtonVisible(true);
        comboBoxSerialPort.setPlaceholder("com port");
        return this.createDiv(this.comboBoxSerialPort, MARGIN, MARGIN_10_PX);
    }

    private Div createH3SerialPort() {
        final Div divH3 = new Div(new H3("Serial port"));
        divH3.getStyle().set("white-space", "pre");
        return divH3;
    }

    public H2 getEspToolVersion() {
        final H2 h2 = new H2();
        h2.addClassName("pulse");
        this.commandService.esptoolVersion()
                .subscribe((String esptoolVersion) -> {
                    if (getUI().isPresent()) {
                        getUI().get().access(() -> h2.setText(esptoolVersion));
                    }
                });
        return h2;
    }

    private void initListeners(final UI ui) {
        this.scanPort.addClickListener(e -> {
            final Set<String> ports = this.comPortService.getPortsList();
            if (!ports.isEmpty()) {
                comboBoxSerialPort.setItems(ports); //set port items to combo
                NotificationBuilder.builder()
                        .withText("Port found!")
                        .withPosition(Position.MIDDLE)
                        .withDuration(3000)
                        .withIcon(VaadinIcon.INFO_CIRCLE)
                        .withThemeVariant(NotificationVariant.LUMO_PRIMARY)
                        .make();
            } else {
                NotificationBuilder.builder()
                        .withText("Port not found!")
                        .withPosition(Position.MIDDLE)
                        .withDuration(3000)
                        .withIcon(VaadinIcon.WARNING)
                        .withThemeVariant(NotificationVariant.LUMO_ERROR)
                        .make();
            }
        });

        this.killProcess.addClickListener(e -> {
            ui.access(() -> Notification.show("Pid ".concat(this.commandService.getListProcess()
                    .stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(" ")))));

            this.commandService.killProcess(Long.valueOf(this.inputCommand.getValue()));
        });

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
            this.initListeners(ui);
        }
    }
}
