package com.esp.espflow.views.flashesp;

import com.esp.espflow.data.enums.BaudRates;
import com.esp.espflow.data.enums.EraseFlashEnum;
import com.esp.espflow.data.enums.FlashMode;
import com.esp.espflow.data.service.EsptoolService;
import com.esp.espflow.data.util.CommandsOnFirstLine;
import com.esp.espflow.data.util.ConfirmDialogBuilder;
import com.esp.espflow.data.util.EsptoolBundlePath;
import com.esp.espflow.data.util.IBuilder;
import com.esp.espflow.data.util.console.OutPutConsole;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;

import java.util.Objects;
import java.util.stream.Stream;

import static com.esp.espflow.data.util.EspFlowConstants.BAUD_RATE;
import static com.esp.espflow.data.util.EspFlowConstants.FLASH_MODE;
import static com.esp.espflow.data.util.EspFlowConstants.FLASH_SIZE;
import static com.esp.espflow.data.util.EspFlowConstants.PORT;
import static com.esp.espflow.data.util.EspFlowConstants.WRITE_FLASH;

/**
 *
 * <blockquote>
 *  <pre>
 *  WriteFlashBuilder.builder()
 *           .withEsptoolService(this.esptoolService)
 *           .withSerialPort(divHeaderPorts.getComboBoxSerialPort())
 *           .withFlashMode(this.flashModeRadioButtonGroup)
 *           .withBaudRate(this.baudRatesRadioButtonGroup)
 *           .withFlashSize(DEFAULT_INIT_ADDRESS_SIZE_TO_WRITE_0x_00000)
 *           .withUI(UI.getCurrent())
 *           .withEraseFlashOption(this.eraseRadioButtons)
 *           .withFlashFileName(this.flashFileName)
 *           .withOutPutConsole(this.outPutConsole)
 *           .make();
 *  </pre>
 * </blockquote>
 *
 * @author rubn
 */
public class WriteFlashBuilder {

    /**
     *
     */
    public static EsptoolServiceStage builder() {
        return new InnerBuilder();
    }

    /**
     * 1
     */
    public interface EsptoolServiceStage {
        ComboBoxSerialPortStage withEsptoolService(final EsptoolService esptoolService);
    }
    /**
     * 2
     */
    public interface ComboBoxSerialPortStage {
        FlashModeStage withSerialPort(ComboBox<String> comboBoxSerialPort);
    }

    /**
     * 3
     */
    public interface FlashModeStage {
        BaudRateStage withFlashMode(RadioButtonGroup<FlashMode> flashModeRadioButtonGroup);
    }

    /**
     * 4
     */
    public interface BaudRateStage {
        FlashSizeStage withBaudRate(RadioButtonGroup<BaudRates> baudRatesRadioButtonGroup);
    }

    /**
     * 5
     */
    public interface FlashSizeStage {
        UIStage withFlashSize(String flashSize);
    }

    /**
     *  6
     */
    public interface UIStage {
        EraseFlashStage withUI(final UI ui);
    }

    /**
     * 7
     */
    public interface EraseFlashStage {
        FlashFileNameStage withEraseFlashOption(RadioButtonGroup<EraseFlashEnum> eraseFlashOption);
    }

    /**
     * 8
     */
    public interface FlashFileNameStage {
        OutPutConsoleStage withFlashFileName(final String flashFileName);
    }

    /**
     *  9
     */
    public interface OutPutConsoleStage {
        Build withOutPutConsole(final OutPutConsole outPutConsole);
    }

    /**
     * 10
     */
    public interface Build extends IBuilder<WriteFlashBuilder> {
    }

    /**
     * The InnerBuilder
     */
    public static class InnerBuilder implements EsptoolServiceStage, ComboBoxSerialPortStage,
            BaudRateStage, FlashModeStage, FlashSizeStage, UIStage, EraseFlashStage,
            FlashFileNameStage, OutPutConsoleStage, Build {

        private EsptoolService esptoolService;
        private RadioButtonGroup<BaudRates> baudRate;
        private ComboBox<String> serialPort;
        private RadioButtonGroup<FlashMode> flashMode;
        private RadioButtonGroup<EraseFlashEnum> eraseFlashOption;
        private OutPutConsole outPutConsole;
        private UI ui;
        private String flashSize;
        private String flashFileName;

        @Override
        public ComboBoxSerialPortStage withEsptoolService(EsptoolService esptoolService) {
            this.esptoolService = esptoolService;
            return this;
        }

        @Override
        public FlashSizeStage withBaudRate(RadioButtonGroup<BaudRates> baudRatesRadioButtonGroup) {
            this.baudRate = baudRatesRadioButtonGroup;
            return this;
        }

        @Override
        public FlashModeStage withSerialPort(ComboBox<String> comboBoxSerialPort) {
            this.serialPort = comboBoxSerialPort;
            return this;
        }

        @Override
        public BaudRateStage withFlashMode(RadioButtonGroup<FlashMode> flashModeRadioButtonGroup) {
            this.flashMode = flashModeRadioButtonGroup;
            return this;
        }

        @Override
        public UIStage withFlashSize(String flashSize) {
            this.flashSize = flashSize;
            return this;
        }

        @Override
        public EraseFlashStage withUI(UI ui) {
            this.ui = ui;
            return this;
        }

        @Override
        public FlashFileNameStage withEraseFlashOption(RadioButtonGroup<EraseFlashEnum> eraseFlashOption) {
            this.eraseFlashOption = eraseFlashOption;
            return this;
        }

        @Override
        public OutPutConsoleStage withFlashFileName(final String flashFileName) {
            this.flashFileName = flashFileName;
            return this;
        }

        @Override
        public Build withOutPutConsole(OutPutConsole outPutConsole) {
            this.outPutConsole = outPutConsole;
            return this;
        }

        @Override
        public WriteFlashBuilder make() {

            final String[] commands = new String[]{
                    EsptoolBundlePath.esptoolBundlePath(),
                    PORT, serialPort.getValue(),
                    BAUD_RATE, baudRate.getValue().toString(),
                    WRITE_FLASH,
                    FLASH_MODE, flashMode.getValue().toString().toLowerCase(),
                    FLASH_SIZE, "detect", flashSize,
                    "src/main/resources/flashesdir/" + flashFileName
            };

            if(Stream.of(serialPort.getValue(), flashFileName).allMatch(Objects::nonNull)) {

                CommandsOnFirstLine.putCommansdOnFirstLine(commands, outPutConsole);

                this.esptoolService.writeFlash(commands)
                        .doOnError(onError -> {
                            ui.access(() -> {
                                this.outPutConsole.writeln(onError.getMessage());
                                ConfirmDialogBuilder.showInformation("Problem writing the flash!!!");
                            });
                        })
                        .doOnTerminate(() -> {
                            ui.access(() -> {
                                ConfirmDialogBuilder.showInformation("Flash writed successfully!!!");
                                this.outPutConsole.writePrompt();
                            });
                        })
                        .subscribe(line -> {
                            ui.access(() -> {
                                this.outPutConsole.writeFlash(line);
                            });
                        });
            } else {
                ConfirmDialogBuilder.showWarning("Incorrect data!!!");
            }
            return new WriteFlashBuilder();
        }

    }

}
