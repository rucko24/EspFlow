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
import java.util.stream.Stream;

@Log4j2
@UIScope
@PageTitle("Flash Esp32+")
@Route(value = "flash-esp32+", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@RequiredArgsConstructor
public class FlashEsp32View extends HorizontalLayout implements ResponsiveHeaderDiv {

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
        final var headerPorts = this.getPorts();
//        final var baudRate = this.getBaudRates();
//        final var flashMode = this.getFlashMode();
//        final var rowConsole = this.rowConsole();

//        this.add(headerPorts, baudRate, flashMode, this.rowEraseFlash(), rowConsole);

//        this.setFlexGrow(1, rowConsole);
//        final VerticalLayout verticalLayout = new VerticalLayout();
//        verticalLayout.add(headerPorts);
        super.add(headerPorts);
    }

    /**
     * Show console output
     *
     * @return HorizontalLayout
     */
    public HorizontalLayout rowConsole() {
        final HorizontalLayout row = new HorizontalLayout();
        row.setSizeFull();
        textArea.setSizeFull();
        textArea.setClearButtonVisible(Boolean.TRUE);
        textArea.getStyle().set("overflow-y", "auto");
        row.getStyle().set("overflow-y", "hidden");
        final Label labelConsole = new Label("Console");
        row.add(labelConsole, textArea);
        return row;
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

    public HorizontalLayout getBaudRates() {
        final HorizontalLayout row = new HorizontalLayout();
        final Label label = new Label("Baud rate");
        baudRatesRadioButtonGroup.setItems(BaudRates.values());
        baudRatesRadioButtonGroup.setValue(BaudRates.BAUD_RATE_9600);
        row.add(label, this.baudRatesRadioButtonGroup);
        Stream.of(label, this.baudRatesRadioButtonGroup)
                .forEach((component) -> row.setVerticalComponentAlignment(Alignment.CENTER, component));
        return row;
    }

    public HorizontalLayout getFlashMode() {
        final HorizontalLayout row = new HorizontalLayout();
        final Label label = new Label("Flash mode");
        this.flashModeRadioButtonGroup.setItems(FlashMode.values());
        this.flashModeRadioButtonGroup.setValue(FlashMode.DUAL_IO);
        row.add(label, flashModeRadioButtonGroup);
        Stream.of(label, flashModeRadioButtonGroup)
                .forEach((component) -> row.setVerticalComponentAlignment(Alignment.CENTER, component));
        return row;
    }

    public Div getPorts() {
        final HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();

        final Label label = new Label("Serial port");
        label.setWidth("30px");
        comboBox.setClearButtonVisible(true);
        comboBox.setPlaceholder("com port");

        this.scanPort.addClickListener(e -> {
            textArea.getElement().getChildren()
                    .forEach(System.out::println);
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
        divLabel.getStyle().set("margin","10px 10px");
        divLabel.getStyle().set("white-space","pre");
        final Div divCombo = new Div(this.comboBox);
        divCombo.getStyle().set("margin","10px 10px");

        final Div divHeader = new Div(divLabel, divCombo);
        divHeader.setWidthFull();
        divHeader.getStyle().set("display","flex");
        divHeader.getStyle().set("margin","10px 10px");
        divHeader.getStyle().set("align-items","baseline");

        final Div divScanPort = new Div(scanPort);
        divScanPort.getStyle().set("margin","10px 10px");
        final Div divInputCommand = new Div(inputCommand);
        divInputCommand.getStyle().set("margin","10px 10px");
        divHeader.add(divScanPort, divInputCommand);

        final Div divReadCommand = new Div(validateInput);
        divReadCommand.getStyle().set("margin", "10px, 10px");
        final Div divKillProcess = new Div(killProcess);
        divKillProcess.getStyle().set("margin", "10px 10px");
        divHeader.add(divReadCommand, divKillProcess);

        final Hr hr = new Hr();
        hr.setHeight("6px");
        hr.getStyle().set("background-image","linear-gradient(#e0260b, #e0260b),\n" +
                "        linear-gradient(#e0260b, #e0260b)");
        hr.getStyle().set("background-size","100% 3px, 100% 1px");
        final Div divH2espToolVersion = new Div(h2EspToolVersion, hr);
        divH2espToolVersion.getStyle().set("margin", "10px 10px");
        final Div divEndForH2EspToolVersion = new Div(divH2espToolVersion);
        divEndForH2EspToolVersion.setWidthFull();
        divEndForH2EspToolVersion.getStyle().set("display","flex");
        divEndForH2EspToolVersion.getStyle().set("justify-content","center");

        final Div divSpaceAround = new Div(divHeader, divEndForH2EspToolVersion);
        divSpaceAround.setWidthFull();
        divSpaceAround.getStyle().set("display","flex");
        divSpaceAround.getStyle().set("justify-content","space-around");

//        header.add(label, this.comboBox, scanPort, inputCommand, validateInput, killProcess, esptoolVersion);
//        header.setAlignItems(Alignment.CENTER);
//        header.setFlexGrow(1, esptoolVerion);

        return divSpaceAround;
    }

    public HorizontalLayout rowEraseFlash() {
        final HorizontalLayout row = new HorizontalLayout();
        final Label label = new Label("Erase flash");
        this.eraseRadioButtons.setItems("no", "yes, wipes all data");
        this.eraseRadioButtons.setValue("no");
        row.add(label, this.eraseRadioButtons);
        Stream.of(label, this.eraseRadioButtons)
                .forEach((component) -> row.setVerticalComponentAlignment(Alignment.CENTER, component));
        row.setFlexGrow(1, flashModeRadioButtonGroup);
        return row;
    }

    @SneakyThrows
    public void consoleOutput(final UI ui) {
        this.labelPin.setTitle("Ping to google");
        this.killProcess.addClickListener(e -> {
            textArea.getElement().getChildren()
                    .forEach(System.out::println);

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
