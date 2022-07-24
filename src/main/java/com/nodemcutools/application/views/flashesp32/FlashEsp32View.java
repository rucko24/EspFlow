package com.nodemcutools.application.views.flashesp32;

import com.nodemcutools.application.data.BaudRates;
import com.nodemcutools.application.data.FlashMode;
import com.nodemcutools.application.data.service.CommandService;
import com.nodemcutools.application.data.util.ResponsiveHeaderDiv;
import com.nodemcutools.application.views.MainLayout;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;

@Log4j2
@UIScope
@PageTitle("Flash Esp32+")
@Route(value = "flash-esp32", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@RequiredArgsConstructor
public class FlashEsp32View extends HorizontalLayout implements ResponsiveHeaderDiv {

    public static final String MARGIN_TOP = "margin-top";
    public static final String MARGIN_LEFT = "margin-left";
    public static final String MARGIN = "margin";
    public static final String MARGIN_10_PX = "10px";
    public static final String AUTO = "auto";
    public static final String DISPLAY = "display";
    public static final String BOX_SHADOW_PROPERTY = "box-shadow";
    public static final String BOX_SHADOW_VALUE = "0 4px 8px 0 rgba(0, 0, 0, 0.2), 0 6px 20px 0 rgba(0, 0, 0, 0.19)";
    private final DivFlashUploader divFlashUploader;
    private final DivHeaderPorts divHeaderPorts;

    private final CommandService commandService;
    private final RadioButtonGroup<BaudRates> baudRatesRadioButtonGroup = new RadioButtonGroup<>();
    private final RadioButtonGroup<FlashMode> flashModeRadioButtonGroup = new RadioButtonGroup<>();
    private final RadioButtonGroup<String> eraseRadioButtons = new RadioButtonGroup<>();
    private final TextArea textArea = new TextArea();

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

        verticalLayout.setFlexGrow(1, divRowConsole);
        super.add(verticalLayout);
    }

    public Div rowPorts() {
        return this.divHeaderPorts;
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

    private Div rowUploadingFlash() {
        return this.divFlashUploader;
    }

    /**
     * Show console output
     *
     * @return HorizontalLayout
     */
    public Div rowConsole() {
        final H3 h3 = new H3("Console");
        h3.getStyle().set(MARGIN_TOP, AUTO);
        final Div divH3 = new Div(h3);

        textArea.setClearButtonVisible(Boolean.TRUE);
        textArea.setSizeFull();
        textArea.getStyle().set("overflow-y", AUTO);
//        textArea.getStyle().set(BOX_SHADOW_PROPERTY, BOX_SHADOW_VALUE);

        final Div divTextArea = new Div(textArea);
        divTextArea.setSizeFull();
        divTextArea.getStyle().set(MARGIN_LEFT, MARGIN_10_PX);
        divTextArea.getStyle().set("margin-right", MARGIN_10_PX);

        final Div div = new Div(divH3, divTextArea);
        div.setWidthFull();
        div.getStyle().set(DISPLAY, "flex");
        div.getStyle().set(MARGIN_LEFT, MARGIN_10_PX);
        div.getStyle().set("overflow-y", "hidden");

        return div;
    }
    @SneakyThrows
    public void consoleOutput(final UI ui) {
        this.divHeaderPorts.getValidateInput().addClickListener(e -> {
            if (!this.divHeaderPorts.getInputCommand().getValue().isEmpty()) {
                final String command = this.divHeaderPorts.getInputCommand().getValue();
                this.commandService.processInputStream(command.split(" "))
                        .doOnError((Throwable error) -> {
                            ui.access(() -> {
                                log.info("Salida: {}", error);
                                this.textArea.setValue(textArea.getValue().concat(error.getMessage()));
                            });
                        })
                        .subscribe((String line) -> {
                            ui.access(() -> {
                                log.info("Salida: {}", "");
                                this.textArea.setValue(textArea.getValue().concat("line"));
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
