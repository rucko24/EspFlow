package com.nodemcutools.application.views.flashesp32;

import com.nodemcutools.application.data.BaudRates;
import com.nodemcutools.application.data.FlashMode;
import com.nodemcutools.application.data.service.ComPortService;
import com.nodemcutools.application.data.service.CommandService;
import com.nodemcutools.application.data.util.ResponsiveHeaderDiv;
import com.nodemcutools.application.views.MainLayout;
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
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import java.util.stream.Collectors;

@Log4j2
@UIScope
@PageTitle("Flash Esp32+")
@Route(value = "flash-esp32", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@RequiredArgsConstructor
public class FlashEsp32View extends HorizontalLayout implements ResponsiveHeaderDiv {

    private static final String MARGIN_TOP = "margin-top";
    private static final String MARGIN_LEFT = "margin-left";
    private static final String MARGIN = "margin";
    private static final String MARGIN_10_PX = "10px";
    private static final String AUTO = "auto";
    private static final String DISPLAY = "display";
    public static final String BOX_SHADOW_PROPERTY = "box-shadow";
    public static final String BOX_SHADOW_VALUE = "0 4px 8px 0 rgba(0, 0, 0, 0.2), 0 6px 20px 0 rgba(0, 0, 0, 0.19)";
    private final CommandService commandService;
    private final ComPortService comPortService;
    private final Button scanPort = new Button(VaadinIcon.REFRESH.create());
    private final Button validateInput = new Button(VaadinIcon.CHECK.create());
    private final Button killProcess = new Button(VaadinIcon.STOP.create());
    private final ComboBox<String> comboBox = new ComboBox<>();
    private final Label labelPin = new Label("Ping to google");
    private final RadioButtonGroup<BaudRates> baudRatesRadioButtonGroup = new RadioButtonGroup<>();
    private final RadioButtonGroup<FlashMode> flashModeRadioButtonGroup = new RadioButtonGroup<>();
    private final RadioButtonGroup<String> eraseRadioButtons = new RadioButtonGroup<>();

    private final TextArea textArea = new TextArea();
    private final TextField inputCommand = new TextField();

    @PostConstruct
    public void init() {
        super.setSizeFull();
        final var divRowPort = this.rowPorts();
        final var divRowBaudRate = this.rowBaudRates();
        final var divRowFlashMode = this.rowFlashMode();
        final var divRowEraseFlash = this.rowEraseFlash();
        final var divRowConsole = this.rowConsole();

        final VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.add(divRowPort, divRowBaudRate, divRowFlashMode, divRowEraseFlash, divRowConsole);
        verticalLayout.setFlexGrow(1, divRowConsole);
        super.add(verticalLayout);
    }

    public H2 getEspToolVersion() {
        final H2 h2 = new H2();
        this.commandService.esptoolVersion()
                .subscribe((String esptoolVersion) -> {
                    if (getUI().isPresent()) {
                        getUI().get().access(() -> h2.setText(esptoolVersion));
                    }
                });
        return h2;
    }

    public Div rowPorts() {
        final HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();

        final Label label = new Label("Serial port");
        label.setWidth("30px");
        comboBox.setClearButtonVisible(true);
        comboBox.setPlaceholder("com port");

        this.scanPort.addClickListener(e -> {
            textArea.getElement().getChildren()
                    .forEach(log::info);
            comboBox.setItems(this.comPortService.getPortsList());
        });

        final H2 h2EspToolVersion = getEspToolVersion();
        h2EspToolVersion.addClassName("pulse");
        final HorizontalLayout esptoolVersion = new HorizontalLayout(h2EspToolVersion);
        esptoolVersion.setAlignItems(Alignment.END);
        esptoolVersion.setJustifyContentMode(JustifyContentMode.END);

        inputCommand.setPlaceholder("input command");
        inputCommand.setClearButtonVisible(Boolean.TRUE);
        killProcess.addThemeVariants(ButtonVariant.LUMO_ERROR);

        final Div divLabel = new Div(new H3("Serial port"));
        divLabel.getStyle().set("white-space", "pre");
        divLabel.getStyle().set("white-space", "pre");
        final Div divCombo = this.createDiv(this.comboBox, MARGIN, MARGIN_10_PX);

        final Div divHeader = new Div(divLabel, divCombo);
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
        final Div divH2espToolVersion = new Div(h2EspToolVersion, hr);
        divH2espToolVersion.getStyle().set(MARGIN_TOP, AUTO);
        final Div divEndForH2EspToolVersion = new Div(divH2espToolVersion);
        divEndForH2EspToolVersion.setWidthFull();
        divEndForH2EspToolVersion.getStyle().set(DISPLAY, "flex");
        divEndForH2EspToolVersion.getStyle().set("justify-content", "center");

        final Div divSpaceAround = new Div(divHeader, divEndForH2EspToolVersion);
        divSpaceAround.setWidthFull();
        divSpaceAround.getStyle().set(DISPLAY, "flex");
        divSpaceAround.getStyle().set("justify-content", "space-around");

        return divSpaceAround;
    }

    public Div rowBaudRates() {
        baudRatesRadioButtonGroup.setItems(BaudRates.values());
        baudRatesRadioButtonGroup.setValue(BaudRates.BAUD_RATE_9600);

        final H3 h3 = new H3("Baud rate");
        h3.getStyle().set(MARGIN_TOP, AUTO);
        final Div divH2BaudRate = new Div(h3);
        final Div divBaudRateRadioButton = this.createDiv(baudRatesRadioButtonGroup, MARGIN_LEFT, MARGIN_10_PX);

        final Div div = new Div(divH2BaudRate, divBaudRateRadioButton);
        div.setWidthFull();
        div.getStyle().set(DISPLAY, "flex");
        div.getStyle().set(MARGIN_LEFT, MARGIN_10_PX);

        return div;
    }

    public Div rowFlashMode() {
        final H3 h3 = new H3("Flash mode");
        h3.getStyle().set(MARGIN_TOP, AUTO);
        final Div divh3FlashMode = new Div(h3);

        this.flashModeRadioButtonGroup.setItems(FlashMode.values());
        this.flashModeRadioButtonGroup.setValue(FlashMode.DUAL_IO);

        final Div divFlashRadioButton = this.createDiv(flashModeRadioButtonGroup, MARGIN_LEFT, MARGIN_10_PX);

        final Div div = new Div(divh3FlashMode, divFlashRadioButton);
        div.setWidthFull();
        div.getStyle().set(DISPLAY, "flex");
        div.getStyle().set(MARGIN_LEFT, MARGIN_10_PX);

        return div;
    }

    public Div rowEraseFlash() {
        final H3 h3 = new H3("Erase flash");
        h3.getStyle().set(MARGIN_TOP, AUTO);
        final Div divh3EraseFlash = new Div(h3);

        this.eraseRadioButtons.setItems("no", "yes, wipes all data");
        this.eraseRadioButtons.setValue("no");
        final Div divEraseRadioButton = this.createDiv(eraseRadioButtons, MARGIN_LEFT, MARGIN_10_PX);

        final Div div = new Div(divh3EraseFlash, divEraseRadioButton);
        div.setWidthFull();
        div.getStyle().set(DISPLAY, "flex");
        div.getStyle().set(MARGIN_LEFT, MARGIN_10_PX);

        return div;
    }

    /**
     * Show console output
     *
     * @return HorizontalLayout
     */
    public Div rowConsole() {
        final H3 h3 = new H3("Console");
        h3.getStyle().set(MARGIN_TOP,AUTO);
        final Div divH3 = new Div(h3);

        textArea.setClearButtonVisible(Boolean.TRUE);
        textArea.setSizeFull();
        textArea.getStyle().set("overflow-y", AUTO);
        textArea.getStyle().set(BOX_SHADOW_PROPERTY, BOX_SHADOW_VALUE);

        final Div divTextArea = new Div(textArea);
        divTextArea.setSizeFull();
        divTextArea.getStyle().set(MARGIN_LEFT, MARGIN_10_PX);
        divTextArea.getStyle().set("margin-right", MARGIN_10_PX);
        divTextArea.getStyle().set("overflow-y", "hidden");

        final Div div = new Div(divH3, divTextArea);
        div.setWidthFull();
        div.getStyle().set(DISPLAY, "flex");
        div.getStyle().set(MARGIN_LEFT, MARGIN_10_PX);

        return div;
    }

    @SneakyThrows
    public void consoleOutput(final UI ui) {
        this.labelPin.setTitle("Ping to google");
        this.killProcess.addClickListener(e -> {
            textArea.getElement().getChildren()
                    .forEach(log::info);

            ui.access(() -> Notification.show("Pid ".concat(this.commandService.getListProcess()
                    .stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(" ")))));

            //this.commandService.killProcess();
        });

        this.validateInput.addClickListener(e -> {
            if (!inputCommand.getValue().isEmpty()) {
                final String command = inputCommand.getValue();
                this.commandService.processInputStream(command.split(" "))
                        .doOnError((Throwable error) -> {
                            ui.access(() -> {
                                log.info("Salida: {}", error);
                                this.textArea.setValue(textArea.getValue().concat(error.getMessage()));
                            });
                        })
                        .subscribe((String line) -> {
                            ui.access(() -> {
                                log.info("Salida: {}", line);
                                this.textArea.setValue(textArea.getValue().concat(line));
                            });
                        });
            }
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
            this.consoleOutput(ui);
        }
    }

}
