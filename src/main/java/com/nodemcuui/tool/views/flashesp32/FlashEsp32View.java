package com.nodemcuui.tool.views.flashesp32;

import com.nodemcuui.tool.data.enums.BaudRates;
import com.nodemcuui.tool.data.enums.FlashMode;
import com.nodemcuui.tool.data.service.CommandService;
import com.nodemcuui.tool.data.service.EsptoolService;
import com.nodemcuui.tool.data.util.console.ConsoleCommandOutPutArea;
import com.nodemcuui.tool.views.MainLayout;
import com.nodemcuui.tool.data.util.CommandsOnFirstLine;
import com.nodemcuui.tool.data.util.ResponsiveHeaderDiv;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout.Orientation;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ArrayUtils;
import reactor.core.publisher.Flux;

import java.util.Objects;

import static com.nodemcuui.tool.data.util.UiToolConstants.AUTO;
import static com.nodemcuui.tool.data.util.UiToolConstants.DISPLAY;
import static com.nodemcuui.tool.data.util.UiToolConstants.DMESG_GREP_TTY;
import static com.nodemcuui.tool.data.util.UiToolConstants.HIDDEN;
import static com.nodemcuui.tool.data.util.UiToolConstants.MARGIN_10_PX;
import static com.nodemcuui.tool.data.util.UiToolConstants.MARGIN_LEFT;
import static com.nodemcuui.tool.data.util.UiToolConstants.MARGIN_TOP;
import static com.nodemcuui.tool.data.util.UiToolConstants.OVERFLOW_X;
import static com.nodemcuui.tool.data.util.UiToolConstants.OVERFLOW_Y;

/**
 * @author rubn
 */
@Log4j2
@UIScope
@SpringComponent
@PageTitle("Flash Esp32+")
@Route(value = "flash-esp32", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@RequiredArgsConstructor
public class FlashEsp32View extends HorizontalLayout implements ResponsiveHeaderDiv {

    private final DivFlashUploader divFlashUploader;
    private final DivHeaderPorts divHeaderPorts;
    private final CommandService commandService;
    private final EsptoolService esptoolService;
    private final RadioButtonGroup<BaudRates> baudRatesRadioButtonGroup = new RadioButtonGroup<>();
    private final RadioButtonGroup<FlashMode> flashModeRadioButtonGroup = new RadioButtonGroup<>();
    private final RadioButtonGroup<String> eraseRadioButtons = new RadioButtonGroup<>();
    /**
     * Console output area
     */
    private TextArea textAreaConsoleOutput;

    private String[] commands;

    @PostConstruct
    public void constructEsp21View() {
        super.removeAll();
        super.setSizeFull();
        final var divRowPort = this.rowPorts();
        final var divRowBaudRate = this.rowBaudRates();
        final var divRowFlashMode = this.rowFlashMode();
        final var divRowEraseFlash = this.rowEraseFlash();
        final var divRowUploaderFlash = this.rowUploadingFlash();
        final var divRowConsole = this.rowConsole();

        final VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.add(divRowPort, divRowBaudRate, divRowFlashMode, divRowEraseFlash, divRowUploaderFlash,
                divRowConsole);

        final SplitLayout splitLayout = new SplitLayout();
        splitLayout.setOrientation(Orientation.VERTICAL);
        splitLayout.setSizeFull();
        splitLayout.getStyle().set(OVERFLOW_Y, HIDDEN);
        splitLayout.addToPrimary(verticalLayout);

        verticalLayout.addClassName("vertical-parent");

        splitLayout.addToSecondary(divRowConsole);
        splitLayout.setSplitterPosition(60);
        splitLayout.getPrimaryComponent().getElement().getStyle().set(OVERFLOW_X, HIDDEN);
        splitLayout.getSecondaryComponent().getElement().getStyle().set(OVERFLOW_X, HIDDEN);
//        splitLayout.getSecondaryComponent().getElement().getStyle().set("margin-bottom", "10px");
        splitLayout.getSecondaryComponent().getElement().getStyle().set("margin-right", "20px");
        splitLayout.getStyle().set(OVERFLOW_X, HIDDEN);

        super.add(splitLayout);
    }

    private Div rowPorts() {
        return this.divHeaderPorts;
    }

    private Div rowBaudRates() {
        baudRatesRadioButtonGroup.setItems(BaudRates.values());
        baudRatesRadioButtonGroup.setValue(BaudRates.BAUD_RATE_115200);

        final H3 h3 = new H3("Baud rate");
        h3.getStyle().set(MARGIN_TOP, AUTO);
        final Div divH2BaudRate = new Div(h3);
        divH2BaudRate.addClassName("baud-rate-h3-div");
        final Div divBaudRateRadioButton = this.createDiv(baudRatesRadioButtonGroup, MARGIN_LEFT, MARGIN_10_PX);
        divBaudRateRadioButton.addClassName("baud-rate-radio-button");

        final Div div = new Div(divH2BaudRate, divBaudRateRadioButton);
        div.setWidthFull();
        div.getStyle().set(DISPLAY, "flex");
        div.getStyle().set(MARGIN_LEFT, MARGIN_10_PX);
        div.addClassName("baud-rate-flex-wrap");
        return div;
    }

    private Div rowFlashMode() {
        final H3 h3 = new H3("Flash mode");
        h3.getStyle().set(MARGIN_TOP, AUTO);
        final Div divh3FlashMode = new Div(h3);

        this.flashModeRadioButtonGroup.setItems(FlashMode.values());
        this.flashModeRadioButtonGroup.setValue(FlashMode.DUAL_IO);
        this.flashModeRadioButtonGroup.setRequired(Boolean.TRUE);
        final Div divFlashRadioButton = this.createDiv(flashModeRadioButtonGroup, MARGIN_LEFT, MARGIN_10_PX);

        final Div div = new Div(divh3FlashMode, divFlashRadioButton);
        div.setWidthFull();
        div.getStyle().set(DISPLAY, "flex");
        div.getStyle().set(MARGIN_LEFT, MARGIN_10_PX);

        return div;
    }

    private Div rowEraseFlash() {
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

    private Div rowUploadingFlash() {
        return this.divFlashUploader;
    }

    /**
     * Show console output
     *
     * @return HorizontalLayout
     */
    private Div rowConsole() {
        final ConsoleCommandOutPutArea consoleCommandOutPutArea = new ConsoleCommandOutPutArea();
        this.textAreaConsoleOutput = consoleCommandOutPutArea.getTextArea();
        return consoleCommandOutPutArea;
    }

    @SneakyThrows
    public void consoleOutput(final UI ui) {
        this.divHeaderPorts.getValidateInput().addClickListener(e -> {
            final String command = this.divHeaderPorts.getInputCommand().getValue().trim();
            if (command.equals(DMESG_GREP_TTY)) {
                this.subscribeThis(this.commandService.executeDmesgForTtyPort(), ui);
                return;
            }
            if (!command.isEmpty()) {
                this.subscribeThis(this.commandService.processCommands(command.split(" ")), ui);
            }
        });
        //esptool.py -p /dev/ttyUSB0 flash_id
        this.divHeaderPorts.getComboBoxSerialPort().addValueChangeListener((event) -> {
            this.textAreaConsoleOutput.clear();
            if (Objects.nonNull(event.getValue())) {
                final String port = event.getValue();
                final int baudRate = baudRatesRadioButtonGroup.getValue().getBaudRate();
                this.commands = new String[]{
                        "esptool.py",
                        "--port", port,
                        "--baud",
                        String.valueOf(baudRate), "flash_id"};
                final String[] currentsCommands = ArrayUtils.addAll(this.commands);
                this.subscribeThis(
                        this.commandService.processCommands(currentsCommands), ui);
            }
        });

    }

    public void subscribeThis(Flux<String> flux, final UI ui) {
        flux.doOnError((Throwable error) ->
                        ui.access(() -> {
                            log.info("Error: {}", error);
                            this.textAreaConsoleOutput.setValue(textAreaConsoleOutput.getValue().concat(error.getMessage()));
                        })
                )
                .subscribe((String line) ->
                        ui.access(() -> {
                            log.info("Salida: subscribeThis {}", line);
                            this.textAreaConsoleOutput.setValue(textAreaConsoleOutput.getValue().concat(line).concat("\n"));
                        })
                );
        final String s = this.textAreaConsoleOutput.getValue();
        final String r = CommandsOnFirstLine.onFirstLine(s, this.commands);
        textAreaConsoleOutput.clear();
        textAreaConsoleOutput.setValue(r);

    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {

    }

    @SneakyThrows
    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        if (attachEvent.isInitialAttach()) {
            final UI ui = attachEvent.getUI();
            this.consoleOutput(ui);
        }
    }

}
