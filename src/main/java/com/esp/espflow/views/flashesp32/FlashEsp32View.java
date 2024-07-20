package com.esp.espflow.views.flashesp32;

import com.esp.espflow.data.enums.BaudRates;
import com.esp.espflow.data.enums.FlashMode;
import com.esp.espflow.data.service.EsptoolService;
import com.esp.espflow.data.util.CommandsOnFirstLine;
import com.esp.espflow.data.util.ConfirmDialogBuilder;
import com.esp.espflow.data.util.GetOsName;
import com.esp.espflow.data.util.ResponsiveHeaderDiv;
import com.esp.espflow.data.util.console.ConsoleOutPut;
import com.esp.espflow.views.MainLayout;
import com.infraleap.animatecss.Animated;
import com.infraleap.animatecss.Animated.Animation;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout.Orientation;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.FlexDirection;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ArrayUtils;
import reactor.core.publisher.Flux;

import java.util.Objects;

import static com.esp.espflow.data.util.EspFlowConstants.*;

/**
 * @author rubn
 */
@Log4j2
@UIScope
@SpringComponent
@PageTitle("Flash Esp32-ESP8266")
@Route(value = "flash-esp32", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@RequiredArgsConstructor
public class FlashEsp32View extends Div implements ResponsiveHeaderDiv {

    private final DivFlashUploader divFlashUploader;
    private final DivHeaderPorts divHeaderPorts;
    private final EsptoolService esptoolService;
    private final RadioButtonGroup<BaudRates> baudRatesRadioButtonGroup = new RadioButtonGroup<>();
    private final RadioButtonGroup<FlashMode> flashModeRadioButtonGroup = new RadioButtonGroup<>();
    private final RadioButtonGroup<String> eraseRadioButtons = new RadioButtonGroup<>();
    private final VerticalLayout contentForPrimary = new VerticalLayout();

    /**
     * Console output area
     */
    private final ConsoleOutPut consoleOutPut = new ConsoleOutPut();

    private String[] commands;

    @PostConstruct
    public void init() {
        super.setSizeFull();
        super.getStyle().set("display", "flex");
        super.getStyle().set("flex-direction", "row");

        final var divRowPort = this.rowPorts();
        final var divRowBaudRate = this.rowBaudRates();
        final var divRowFlashMode = this.rowFlashMode();
        final var divRowEraseFlash = this.rowEraseFlash();
        final var divRowUploaderFlash = this.rowUploadingFlash();
        final var buttonFlash = this.buttonFlash();

        contentForPrimary.add(divRowPort, divRowBaudRate, divRowFlashMode, divRowEraseFlash, divRowUploaderFlash,
                buttonFlash);

        final SplitLayout splitLayout = new SplitLayout(Orientation.VERTICAL);
        splitLayout.setSplitterPosition(60);
        splitLayout.setSizeFull();
        splitLayout.getStyle().set(OVERFLOW_Y, HIDDEN);
        splitLayout.addToPrimary(contentForPrimary);
        Animated.animate(splitLayout, Animation.FADE_IN);

        splitLayout.getStyle().set(OVERFLOW_Y, HIDDEN);

        contentForPrimary.addClassName("vertical-parent");

        final var divRowToSecondary = new Div(consoleOutPut);
        divRowToSecondary.addClassNames(Display.FLEX, FlexDirection.ROW);
        divRowToSecondary.getStyle().set(OVERFLOW_Y, HIDDEN);

        splitLayout.addToSecondary(divRowToSecondary);
        splitLayout.getPrimaryComponent().getElement().getStyle().set(OVERFLOW_X, HIDDEN);
        splitLayout.getSecondaryComponent().getElement().getStyle().set(OVERFLOW_X, HIDDEN);
//        splitLayout.getSecondaryComponent().getElement().getStyle().set("margin-bottom", "10px");
        splitLayout.getSecondaryComponent().getElement().getStyle().set("margin-right", "20px");
        splitLayout.getStyle().set(OVERFLOW_X, HIDDEN);

        super.add(splitLayout);
        Animated.animate(splitLayout, Animation.FADE_IN);
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

    private Div buttonFlash() {
        final Div divButtonFlash = new Div();
        divButtonFlash.getStyle().set(MARGIN_LEFT, MARGIN_10_PX);
        final Button button = new Button(new Span("⚡"));
        button.addClassName(BOX_SHADOW_VAADIN_BUTTON);
        button.setTooltipText("flash me!");
        //button.setDisableOnClick(true);
        button.addClickListener(event -> {
            //execute flash
            ConfirmDialogBuilder.showInformation(THIS_FEATURE_HAS_NOT_BEEN_IMPLEMENTED_YET);
        });
        divButtonFlash.add(button);
        return divButtonFlash;
    }

    /**
     *
     * @param ui
     */
    private void consoleOutput(final UI ui) {
        this.divHeaderPorts.getValidateInput().addClickListener(e -> {
            ConfirmDialogBuilder.showInformation(THIS_FEATURE_HAS_NOT_BEEN_IMPLEMENTED_YET);
        });

        this.divHeaderPorts.getComboBoxSerialPort().addValueChangeListener((event) -> {
            this.consoleOutPut.clear();
            if (Objects.nonNull(event.getValue())) {
                final String port = event.getValue();

                this.commands = ArrayUtils.addAll(
                        GetOsName.shellOsName(),
                        ESPTOOL_PY,
                        PORT, port,
                        BAUD_RATE, String.valueOf(BaudRates.BAUD_RATE_115200.getBaudRate()),
                        FLASH_ID);


                this.subscribeThis(this.esptoolService.readRawFlashIdFromPort(commands), ui);
            }
        });

    }

    /**
     *
     * @param reactiveLines
     * @param ui
     */
    private void subscribeThis(final Flux<String> reactiveLines, final UI ui) {

        CommandsOnFirstLine.putCommansdOnFirstLine(this.commands, consoleOutPut);

        reactiveLines.doOnError((Throwable error) ->
                        ui.access(() -> {
                            log.info("Error: {}", error);
                            this.consoleOutPut.writeln(error.getMessage());
                        })
                )
                .doOnTerminate(() -> {
                    ui.access(this.consoleOutPut::writePrompt);
                })
                .subscribe(line ->
                        ui.access(() -> {
                            log.info("Salida: subscribeThis {}", line);
                            this.consoleOutPut.writeln(line);
                        })
                );


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
            this.consoleOutput(ui);
        }
    }

}
