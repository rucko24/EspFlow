package com.esp.espflow.views.flashesp;

import com.esp.espflow.enums.BaudRatesEnum;
import com.esp.espflow.enums.EraseFlashEnum;
import com.esp.espflow.enums.FlashModeEnum;
import com.esp.espflow.service.EsptoolPathService;
import com.esp.espflow.service.EsptoolService;
import com.esp.espflow.util.CommandsOnFirstLine;
import com.esp.espflow.util.ConfirmDialogBuilder;
import com.esp.espflow.util.IBuilder;
import com.esp.espflow.util.console.OutPutConsole;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.esp.espflow.util.EspFlowConstants.BAUD_RATE;
import static com.esp.espflow.util.EspFlowConstants.FLASH_MODE;
import static com.esp.espflow.util.EspFlowConstants.FLASH_SIZE;
import static com.esp.espflow.util.EspFlowConstants.JAVA_IO_TEMPORAL_DIR_OS;
import static com.esp.espflow.util.EspFlowConstants.PORT;
import static com.esp.espflow.util.EspFlowConstants.WRITE_FLASH;

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
 *           .withEsptoolPathService(this.esptoolPathService)
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
        BaudRateStage withFlashMode(RadioButtonGroup<FlashModeEnum> flashModeRadioButtonGroup);
    }

    /**
     * 4
     */
    public interface BaudRateStage {
        FlashSizeStage withBaudRate(RadioButtonGroup<BaudRatesEnum> baudRatesRadioButtonGroup);
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
        EsptoolPathServiceStage withOutPutConsole(final OutPutConsole outPutConsole);
    }

    /**
     * 10
     */
    public interface EsptoolPathServiceStage {
        Build withEsptoolPathService(EsptoolPathService esptoolPathService);
    }

    /**
     * 11
     */
    public interface Build extends IBuilder<WriteFlashBuilder> {
    }

    /**
     * The InnerBuilder
     */
    public static class InnerBuilder implements EsptoolServiceStage, ComboBoxSerialPortStage,
            BaudRateStage, FlashModeStage, FlashSizeStage, UIStage, EraseFlashStage,
            FlashFileNameStage, OutPutConsoleStage, EsptoolPathServiceStage, Build {

        private EsptoolService esptoolService;
        private RadioButtonGroup<BaudRatesEnum> baudRate;
        private ComboBox<String> serialPort;
        private RadioButtonGroup<FlashModeEnum> flashMode;
        private RadioButtonGroup<EraseFlashEnum> eraseFlashOption;
        private OutPutConsole outPutConsole;
        private UI ui;
        private String flashSize;
        private String flashFileName;
        private EsptoolPathService esptoolPathService;

        @Override
        public ComboBoxSerialPortStage withEsptoolService(EsptoolService esptoolService) {
            this.esptoolService = esptoolService;
            return this;
        }

        @Override
        public FlashSizeStage withBaudRate(RadioButtonGroup<BaudRatesEnum> baudRatesRadioButtonGroup) {
            this.baudRate = baudRatesRadioButtonGroup;
            return this;
        }

        @Override
        public FlashModeStage withSerialPort(ComboBox<String> comboBoxSerialPort) {
            this.serialPort = comboBoxSerialPort;
            return this;
        }

        @Override
        public BaudRateStage withFlashMode(RadioButtonGroup<FlashModeEnum> flashModeRadioButtonGroup) {
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
        public EsptoolPathServiceStage withOutPutConsole(OutPutConsole outPutConsole) {
            this.outPutConsole = outPutConsole;
            return this;
        }

        @Override
        public Build withEsptoolPathService(EsptoolPathService esptoolPathService) {
            this.esptoolPathService = esptoolPathService;
            return this;
        }

        @Override
        public WriteFlashBuilder make() {

            final String[] preCommands = new String[]{
                    esptoolPathService.esptoolPath(),
                    PORT, serialPort.getValue(),
                    BAUD_RATE, baudRate.getValue().toString().split(" ")[0],
                    WRITE_FLASH, eraseFlashOption.getValue().getValue(),
                    FLASH_MODE, flashMode.getValue().toString().toLowerCase(),
                    FLASH_SIZE, "detect", flashSize,
                    JAVA_IO_TEMPORAL_DIR_OS + "/flash-esptool-write-dir/" + flashFileName
            };

            final String[] commands = Arrays.stream(preCommands)
                    .filter(item -> item != null && !item.isEmpty()) //remove empty Strings
                    .toArray(String[]::new);

            final List<String> errorFields = new CopyOnWriteArrayList<>();

            if(serialPort.getValue() == null){
                errorFields.add("Serial port is empty");
            }

            if(flashFileName == null) {
                errorFields.add("Firmware does not exist");
            }

            if(errorFields.isEmpty()) {

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
                ConfirmDialogBuilder.showWarning("Incorrect data!!!" + errorFields);
            }
            return new WriteFlashBuilder();
        }

    }

}
