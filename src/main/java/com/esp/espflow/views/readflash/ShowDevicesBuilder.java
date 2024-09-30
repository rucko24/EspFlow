package com.esp.espflow.views.readflash;

import com.esp.espflow.data.entity.EspDeviceInfo;
import com.esp.espflow.data.enums.BaudRates;
import com.esp.espflow.data.service.EsptoolService;
import com.esp.espflow.data.util.CommandsOnFirstLine;
import com.esp.espflow.data.util.ConfirmDialogBuilder;
import com.esp.espflow.data.util.EsptoolPath;
import com.esp.espflow.data.util.IBuilder;
import com.esp.espflow.data.util.console.OutPutConsole;
import com.esp.espflow.data.util.downloader.FlashButtonWrapper;
import com.flowingcode.vaadin.addons.carousel.Slide;
import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.binder.Binder;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static com.esp.espflow.data.util.EspFlowConstants.*;
import static com.esp.espflow.views.readflash.EspDevicesCarousel.createSlideContent;

/**
 * <p><strong>ShowDevices</strong>  this class serves as an aid to display in the <strong>ReadFirmwareView</strong> the microcontrollers available in the OS.</p>
 *
 * <p>With this StepBuilder we can set it up avoiding the nulls, and with obligatory steps </p>
 *
 *  <blockquote>
 * <pre>
 *     ShowDevices.builder()
 *             .withEspDevicesCarousel(espDevicesCarousel)
 *             .withEsptoolService(esptoolService)
 *             .withEspDeviceInfo(espDeviceInfo)
 *             .withOutPutConsole(outPutConsole)
 *             .withUi(ui)
 *             .withStartSizeAddress(this.startAddress)
 *             .withCustomFlashSizeAddress(this.endAddress)
 *             .withAutoDetectFlashSize(this.autoDetectFlashSize)
 *             .withBaudRatesComboBox(this.baudRatesComboBox)
 *             .make();
 * </pre>
 * </blockquote>
 */
@Log4j2
@Getter
public class ShowDevicesBuilder {

    public static EspDevicesCarouselStage builder() {
        return new InnerBuilder();
    }

    /**
     * 1
     */
    public interface EspDevicesCarouselStage {
        EsptoolServiceStage withEspDevicesCarousel(EspDevicesCarousel espDevicesCarousel);
    }

    /**
     * 2
     */
    public interface EsptoolServiceStage {
        EspDeviceInfoStage withEsptoolService(EsptoolService esptoolService);
    }

    /**
     * 3
     */
    public interface EspDeviceInfoStage {
        ConsoleOutPutStage withEspDeviceInfo(EspDeviceInfo espDeviceInfo);
    }

    /**
     * 4
     */
    public interface ConsoleOutPutStage {
        UIStage withOutPutConsole(OutPutConsole outPutConsole);
    }

    /**
     * 5
     */
    public interface UIStage {
        StartAddressStage withUi(final UI ui);
    }

    /**
     * 6
     */
    public interface StartAddressStage {
        EndAddressSizeStage withStartSizeAddress(IntegerField startSizeAddress);
    }

    /**
     * 7
     */
    public interface EndAddressSizeStage {
        AllAddressSizeStage withCustomFlashSizeAddress(IntegerField customFlashSizeAddress);
    }

    /**
     * 8
     */
    public interface AllAddressSizeStage {
        BaudRateStage withAutoDetectFlashSize(final ToggleButton autoDetectFlashSize);
    }

    /**
     * 9
     */
    public interface BaudRateStage {
        Build withBaudRatesComboBox(final ComboBox<BaudRates> baudRatesComboBox);
    }

    /**
     * 10
     */
    public interface Build extends IBuilder<ShowDevicesBuilder> {
    }

    public static class InnerBuilder implements EspDeviceInfoStage, EspDevicesCarouselStage,
            EsptoolServiceStage, UIStage, ConsoleOutPutStage, StartAddressStage,
            EndAddressSizeStage, AllAddressSizeStage, BaudRateStage, Build {
        private EspDevicesCarousel espDevicesCarousel;
        private EsptoolService esptoolService;
        private EspDeviceInfo espDeviceInfo;
        private OutPutConsole outPutConsole;
        private UI ui;
        private IntegerField startAddressSize;
        private IntegerField customSizeToRead;
        private ToggleButton autoDetectFlashSize;
        private ComboBox<BaudRates> baudRatesComboBox;
        /**
         * To bind {@link AddressRecordBinder}
         */
        private Binder<AddressRecordBinder> addressRecordBinderBinder = new Binder<>();

        @Override
        public UIStage withOutPutConsole(OutPutConsole outPutConsole) {
            this.outPutConsole = outPutConsole;
            return this;
        }

        @Override
        public ConsoleOutPutStage withEspDeviceInfo(EspDeviceInfo espDeviceInfo) {
            this.espDeviceInfo = espDeviceInfo;
            return this;
        }

        @Override
        public EsptoolServiceStage withEspDevicesCarousel(EspDevicesCarousel espDevicesCarousel) {
            this.espDevicesCarousel = espDevicesCarousel;
            return this;
        }

        @Override
        public EspDeviceInfoStage withEsptoolService(EsptoolService esptoolService) {
            this.esptoolService = esptoolService;
            return this;
        }

        @Override
        public StartAddressStage withUi(UI ui) {
            this.ui = ui;
            return this;
        }

        @Override
        public AllAddressSizeStage withCustomFlashSizeAddress(IntegerField customFlashSizeAddress) {
            var customSize  = customFlashSizeAddress.getValue().toString();
            if(customSize.matches("\\d+") || customSize.isEmpty()) {
                this.customSizeToRead = customFlashSizeAddress;
            } else {
                customFlashSizeAddress.clear();  // delete if it contains foreign characters and set again.
                this.customSizeToRead = customFlashSizeAddress;
            }
            return this;
        }

        @Override
        public EndAddressSizeStage withStartSizeAddress(IntegerField startSizeAddress) {
            var startSize  = startSizeAddress.getValue().toString();
            if(startSize.matches("\\d+") || startSize.isEmpty()) {
                this.startAddressSize = startSizeAddress;
            } else {
                startSizeAddress.clear(); // delete if it contains foreign characters and set again.
                this.startAddressSize = startSizeAddress;
            }
            return this;
        }

        @Override
        public BaudRateStage withAutoDetectFlashSize(ToggleButton autoDetectFlashSize) {
            this.autoDetectFlashSize = autoDetectFlashSize;
            return this;
        }

        @Override
        public Build withBaudRatesComboBox(ComboBox<BaudRates> baudRatesComboBox) {
            this.baudRatesComboBox = baudRatesComboBox;
            return this;
        }

        @Override
        public ShowDevicesBuilder make() {
            var macAddress = espDeviceInfo.macAddress();
            if (ifItContainsMacAddressShowMeTheSlides(macAddress)) {
                showEsp01s();
                showEso82664MB();
                showEsp82664Cp201x4MB();
                showEsp8285();
                showEsp32S3();
            }
            return new ShowDevicesBuilder();
        }

        private boolean ifItContainsMacAddressShowMeTheSlides(String macAddress) {
            return Objects.nonNull(macAddress);
        }

        /**
         * Show the ESP8285H16 slide
         */
        private void showEsp8285() {
            final String chipIp = espDeviceInfo.chipIs();
            final String flashSize = espDeviceInfo.detectedFlashSize();
            if (chipIp.contains("ESP8285H") && flashSize.equals("2MB")) {

                final FlashButtonWrapper flashButtonWrapper = new FlashButtonWrapper();

                var downloadTest = buttonForReadFlash(ui, flashButtonWrapper);

                Slide esp8285H16Slide = new Slide(createSlideContent(
                        FRONTEND_IMAGES_ESPDEVICES + "ESP8285H08-2MB.jpeg",
                        espDeviceInfo, downloadTest, flashButtonWrapper));

                espDevicesCarousel.addSlide(esp8285H16Slide);

            }
        }

        /**
         * Show the esp01s slide
         *
         */
        private void showEsp01s() {
            final String chipType = espDeviceInfo.chipType();
            final String flashSize = espDeviceInfo.detectedFlashSize();
            if (chipType.endsWith("8266") && flashSize.equals("1MB")) {

                final FlashButtonWrapper flashButtonWrapper = new FlashButtonWrapper();

                var downloadTest = buttonForReadFlash(ui, flashButtonWrapper);

                Slide esp01sSlide = new Slide(createSlideContent(
                        FRONTEND_IMAGES_ESPDEVICES + "esp01s-1MB.jpg",
                        espDeviceInfo, downloadTest, flashButtonWrapper));

                espDevicesCarousel.addSlide(esp01sSlide);
            }
        }

        /**
         *   Show the esp8266 slide
         **/
        private void showEso82664MB() {
            if (espDeviceInfo.chipType().endsWith("8266") && espDeviceInfo.detectedFlashSize().equals("4MB")
            && espDeviceInfo.descriptivePortName().contains("USB Serial")) {

                final FlashButtonWrapper flashButtonWrapper = new FlashButtonWrapper();

                var downFlashButton = buttonForReadFlash(ui, flashButtonWrapper);

                Slide esp8266Slide = new Slide(createSlideContent(
                        FRONTEND_IMAGES_ESPDEVICES + "esp8266-4MB.png",
                        espDeviceInfo, downFlashButton, flashButtonWrapper));

                espDevicesCarousel.addSlide(esp8266Slide);

            }
        }

        private void showEsp82664Cp201x4MB() {
            if (espDeviceInfo.chipType().endsWith("8266") && espDeviceInfo.detectedFlashSize().equals("4MB")
            && espDeviceInfo.descriptivePortName().contains("CP21")) {

                final FlashButtonWrapper flashButtonWrapper = new FlashButtonWrapper();

                var downFlashButton = buttonForReadFlash(ui, flashButtonWrapper);

                Slide esp8266Slide = new Slide(createSlideContent(
                        FRONTEND_IMAGES_ESPDEVICES + "esp8266-cp201x.png",
                        espDeviceInfo, downFlashButton, flashButtonWrapper));

                espDevicesCarousel.addSlide(esp8266Slide);

            }
        }

        /**
         *
         *   Show the ESP32-s3 slide
         */
        private void showEsp32S3() {
            if (espDeviceInfo.chipType().endsWith("-S3")) {
                final FlashButtonWrapper flashButtonWrapper = new FlashButtonWrapper();

                var downFlashButton = buttonForReadFlash(ui, flashButtonWrapper);

                Slide esp32s3Slide = new Slide(createSlideContent(
                        FRONTEND_IMAGES_ESPDEVICES + "ESP32-S3-DEVKITC-1-N8_SPL.webp",
                        espDeviceInfo, downFlashButton, flashButtonWrapper));

                espDevicesCarousel.addSlide(esp32s3Slide);
            }
        }

        /**
         * This button contains the listener that will do the reading to create the carousel with the detected ports.
         *
         * @param ui
         * @param flashButtonWrapper
         * @return A {@link Button} con el listener para la lectura
         */
        private Button buttonForReadFlash(final UI ui, final FlashButtonWrapper flashButtonWrapper) {
            final Button downloadFlashButton = new Button("Read flash");
            downloadFlashButton.setTooltipText("Read flash");
            downloadFlashButton.addClassName(BOX_SHADOW_VAADIN_BUTTON);
            /*
             * Execute the Binder 🔥🚒
             */
            addressRecordBinderBinder.forField(startAddressSize)
                    .withValidator(text -> text.toString().matches("\\d+")
                            || text.toString().isEmpty(), "Invalid input, set only numbers")
                    .bind(AddressRecordBinder::startAddressSize, (addressRecordBinder, value) -> {});
            addressRecordBinderBinder.forField(customSizeToRead)
                    .withValidator(text -> text.toString().matches("\\d+")
                            || text.toString().isEmpty(), "Invalid input, set only numbers")
                    .bind(AddressRecordBinder::customAddresSize, (addressRecordBinder, value) -> {});

            downloadFlashButton.addClickListener(event -> {
                ui.access(() -> {
                    var newRecord = new AddressRecordBinder(startAddressSize.getValue(), customSizeToRead.getValue());
                    if(addressRecordBinderBinder.writeBeanIfValid(newRecord)) {
                        validate(flashButtonWrapper);
                    } else {
                        ConfirmDialogBuilder.showWarning("Invalid input, set only numbers!");
                    }
                });
            });
            return downloadFlashButton;
        }

        /**
         *  This will validate the textfield with the final address and the checkbox
         * @param flashButtonWrapper
         */
        private void validate(final FlashButtonWrapper flashButtonWrapper) {
            String processAutoDetectFlashSize = "";
            if (autoDetectFlashSize.getValue()) {
                processAutoDetectFlashSize = "ALL";
                readFlash(flashButtonWrapper, processAutoDetectFlashSize);
            } else {
                if(!customSizeToRead.isEmpty()) {
                    processAutoDetectFlashSize = "0x".concat(String.valueOf(customSizeToRead.getValue()));
                    readFlash(flashButtonWrapper, processAutoDetectFlashSize);
                } else {
                    ConfirmDialogBuilder.showWarning("Please set the custom size or enable the button for full readability.");
                    customSizeToRead.focus();
                }

            }
        }

        /**
         *
         * @param flashButtonWrapper
         * @param processAutoDetectFlashSize
         */
        private void readFlash(final FlashButtonWrapper flashButtonWrapper, String processAutoDetectFlashSize) {
            esptoolService.createEspBackUpFlashDirIfNotExists();
            final String currentTimeMillis = String.valueOf(System.currentTimeMillis());
            final String fileNameResult = espDeviceInfo.chipIs().concat("-")
                    .concat(currentTimeMillis).concat("-backup.bin");
            final String writFileToTempDir = System.getProperty("java.io.tmpdir")
                    .concat("/esp-backup-flash-dir/")
                    .concat(fileNameResult);
            this.readFlash(ui, writFileToTempDir, espDeviceInfo, flashButtonWrapper, processAutoDetectFlashSize);
        }

        /**
         *
         * <blockquote>
         *     <pre>esptool.py --port /dev/ttyUSB1 read_flash 0 ALL /tmp/esp-backup-flash-dir/ESP8266EX-1720865320370-backup.bin</pre>
         * </blockquote><p>
         *
         * @param ui  the {@link UI} instance
         * @param writFileToTempDir
         * @param espDeviceInfo
         * @param flashButtonWrapper
         * @param processAutoDetectFlashSize
         */
        private void readFlash(final UI ui, final String writFileToTempDir,
                               final EspDeviceInfo espDeviceInfo,
                               final FlashButtonWrapper flashButtonWrapper,
                               final String processAutoDetectFlashSize) {

            final String[] commands = {
                    EsptoolPath.esptoolPath(),
                    PORT, espDeviceInfo.port(),
                    BAUD_RATE, this.baudRatesComboBox.getValue().toString(),
                    READ_FLASH,
                    startAddressSize.getValue().toString().isEmpty() ? "0" : startAddressSize.getValue().toString().trim(),
                    processAutoDetectFlashSize,
                    writFileToTempDir
            };

            CommandsOnFirstLine.putCommansdOnFirstLine(commands, outPutConsole);

            esptoolService.downloadFlash(commands)
                    .doOnComplete(() -> {
                        ui.access(() -> {
                            outPutConsole.writePrompt();
                            if (Files.exists(Path.of(writFileToTempDir))) {
                                log.info("Backup completed successfully! {}", "");
                                ConfirmDialogBuilder.showInformation("Backup completed successfully!");
                                flashButtonWrapper.enableAnchorForDownloadTheFirmware(writFileToTempDir);
                            } else {
                                ConfirmDialogBuilder.showWarning("The flash does not exist in the tmp " + writFileToTempDir);
                            }
                        });
                    })
                    .doOnError(onError -> {
                        ui.access(() -> {
                            log.info("Error: {}", onError);
                            ConfirmDialogBuilder.showWarning("Error al crear backup de esta flash " + onError);
                        });
                    })
                    .subscribe(inputLine -> {
                        ui.access(() -> {
                            outPutConsole.readFlash(inputLine);
                        });
                    });

        }

    }


}