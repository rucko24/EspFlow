package com.esp.espflow.views.flashesp;

import com.esp.espflow.data.enums.BaudRates;
import com.esp.espflow.data.enums.EraseFlashEnum;
import com.esp.espflow.data.enums.FlashMode;
import com.esp.espflow.data.service.EsptoolPathService;
import com.esp.espflow.data.service.EsptoolService;
import com.esp.espflow.data.util.CommandsOnFirstLine;
import com.esp.espflow.data.util.ConfirmDialogBuilder;
import com.esp.espflow.data.util.ResponsiveHeaderDiv;
import com.esp.espflow.data.util.console.OutPutConsole;
import com.esp.espflow.data.util.svgfactory.SvgFactory;
import com.esp.espflow.views.MainLayout;
import com.infraleap.animatecss.Animated;
import com.infraleap.animatecss.Animated.Animation;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout.Orientation;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.FlexDirection;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import reactor.core.publisher.Flux;

import java.util.Objects;

import static com.esp.espflow.data.util.EspFlowConstants.AUTO;
import static com.esp.espflow.data.util.EspFlowConstants.BAUD_RATE;
import static com.esp.espflow.data.util.EspFlowConstants.BOX_SHADOW_VAADIN_BUTTON;
import static com.esp.espflow.data.util.EspFlowConstants.DEFAULT_INIT_ADDRESS_SIZE_TO_WRITE_0x_00000;
import static com.esp.espflow.data.util.EspFlowConstants.FLASH_ID;
import static com.esp.espflow.data.util.EspFlowConstants.FLASH_OFF_SVG;
import static com.esp.espflow.data.util.EspFlowConstants.HIDDEN;
import static com.esp.espflow.data.util.EspFlowConstants.MARGIN_10_PX;
import static com.esp.espflow.data.util.EspFlowConstants.MARGIN_LEFT;
import static com.esp.espflow.data.util.EspFlowConstants.MARGIN_TOP;
import static com.esp.espflow.data.util.EspFlowConstants.OVERFLOW_X;
import static com.esp.espflow.data.util.EspFlowConstants.OVERFLOW_Y;
import static com.esp.espflow.data.util.EspFlowConstants.PORT;
import static com.esp.espflow.data.util.EspFlowConstants.SIZE_25_PX;
import static com.esp.espflow.data.util.EspFlowConstants.THIS_FEATURE_HAS_NOT_BEEN_IMPLEMENTED_YET;

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
public class FlashEspView extends Div implements ResponsiveHeaderDiv {

    private final DivFlashUploader divFlashUploader;
    private final DivHeaderPorts divHeaderPorts;
    private final EsptoolService esptoolService;
    private final EsptoolPathService esptoolPathService;
    private final RadioButtonGroup<BaudRates> baudRatesRadioButtonGroup = new RadioButtonGroup<>();
    private final RadioButtonGroup<FlashMode> flashModeRadioButtonGroup = new RadioButtonGroup<>();
    private final RadioButtonGroup<EraseFlashEnum> eraseRadioButtons = new RadioButtonGroup<>();
    private final Button flashButton = new Button(SvgFactory.createIconFromSvg(FLASH_OFF_SVG, SIZE_25_PX, null));
    private final VerticalLayout contentForPrimary = new VerticalLayout();
    private String flashFileName;
    /**
     * OutputConsole
     */
    private final OutPutConsole outPutConsole = new OutPutConsole();

    private String[] commands;

    @PostConstruct
    public void init() {
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
        final var divRowToSecondary = new Div(outPutConsole);
        divRowToSecondary.addClassNames(Display.FLEX, FlexDirection.ROW);
        divRowToSecondary.getStyle().set(OVERFLOW_Y, HIDDEN);

        splitLayout.addToSecondary(divRowToSecondary);
        /*
         * Invoked after to prevent NPE
         */
        splitLayout.getPrimaryComponent().getElement().getStyle().set(OVERFLOW_X, HIDDEN);
        splitLayout.getSecondaryComponent().getElement().getStyle().set(OVERFLOW_X, HIDDEN);
        splitLayout.getSecondaryComponent().getElement().getStyle().set("margin-right", "20px");
        return splitLayout;
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
        div.addClassNames(Display.FLEX, LumoUtility.Width.FULL);
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
        div.addClassNames(Display.FLEX, LumoUtility.Width.FULL);
        div.getStyle().set(MARGIN_LEFT, MARGIN_10_PX);

        return div;
    }

    private Div rowEraseFlash() {
        final H3 h3 = new H3("Erase flash");
        h3.getStyle().set(MARGIN_TOP, AUTO);
        final Div divh3EraseFlash = new Div(h3);

        this.eraseRadioButtons.setItems(EraseFlashEnum.values());
        this.eraseRadioButtons.setValue(EraseFlashEnum.NO);
        final Div divEraseRadioButton = this.createDiv(eraseRadioButtons, MARGIN_LEFT, MARGIN_10_PX);

        final Div div = new Div(divh3EraseFlash, divEraseRadioButton);
        div.addClassNames(Display.FLEX, LumoUtility.Width.FULL);
        div.getStyle().set(MARGIN_LEFT, MARGIN_10_PX);

        return div;
    }

    private Div rowUploadingFlash() {
        return this.divFlashUploader;
    }

    private Div buttonFlash() {
        final Div divButtonFlash = new Div();
        divButtonFlash.setSizeFull();
        divButtonFlash.getStyle().set(MARGIN_LEFT, MARGIN_10_PX);
        flashButton.addClassName(BOX_SHADOW_VAADIN_BUTTON);
        flashButton.setTooltipText("flash me!");
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
                    .make();
        });

        divButtonFlash.add(flashButton);
        return divButtonFlash;
    }

    /**
     * @param ui
     */
    private void outputConsole(final UI ui) {
        this.divHeaderPorts.getValidateInput().addClickListener(e -> {
            ConfirmDialogBuilder.showInformation(THIS_FEATURE_HAS_NOT_BEEN_IMPLEMENTED_YET);
        });

        this.divHeaderPorts.getComboBoxSerialPort().addValueChangeListener((event) -> {
            this.outPutConsole.clear();
            if (Objects.nonNull(event.getValue())) {
                final String port = event.getValue();

                this.commands = new String[]{
                        esptoolPathService.esptoolPath(),
                        PORT, port,
                        BAUD_RATE, String.valueOf(BaudRates.BAUD_RATE_115200.getBaudRate()),
                        FLASH_ID
                };

                this.subscribeThis(this.esptoolService.readRawFlashIdFromPort(commands), ui);
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
                .doOnTerminate(() -> {
                    ui.access(this.outPutConsole::writePrompt);
                })
                .subscribe(line ->
                        ui.access(() -> {
                            this.outPutConsole.writeln(line);
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
            this.outputConsole(ui);
        }
    }

    @EventListener
    public void fileNameFlashListener(String flashFileName) {
        this.flashFileName = flashFileName;
    }

}
