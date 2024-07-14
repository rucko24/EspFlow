package com.nodemcuui.tool.views.readflash;

import com.flowingcode.vaadin.addons.carousel.Slide;
import com.nodemcuui.tool.data.entity.EspDeviceInfo;
import com.nodemcuui.tool.data.enums.BaudRates;
import com.nodemcuui.tool.data.service.EsptoolService;
import com.nodemcuui.tool.data.util.CommandsOnFirstLine;
import com.nodemcuui.tool.data.util.IBuilder;
import com.nodemcuui.tool.data.util.NotificationBuilder;
import com.nodemcuui.tool.data.util.console.ConsoleOutPut;
import com.nodemcuui.tool.data.util.downloader.FlashButtonWrapper;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextField;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static com.nodemcuui.tool.views.readflash.EspDevicesCarousel.createSlideContent;

/**
 * <p><strong>ShowDevices</strong>  this class serves as an aid to display in the Read Firmware view the microcontrollers available in the OS.</p>
 *
 * <p>With this StepBuilder we can set it up avoiding the nulls, and with obligatory steps </p>
 *
 * <pre>
 *     ShowDevices.builder()
 *         .withEspDevicesCarousel(espDevicesCarousel)
 *         .withEsptoolService(esptoolService)
 *         .withEspDeviceInfo(espDeviceInfo)
 *         .withConsoleOutStage(consoleOutPut)
 *         .withUi(ui)
 *         .createSlides()
 *         .make();
 * </pre>
 */
@Log4j2
@Getter
public class ShowDevices {

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
        UIStage withConsoleOutStage(ConsoleOutPut consoleOutPut);
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
        EndAddressSizeStage withStartSizeAddress(TextField startSizeAddress);
    }

    /**
     * 7
     */
    public interface EndAddressSizeStage {
        AllAddressSizeStage withCustomFlashSizeAddress(TextField customFlashSizeAddress);
    }

    /**
     * 8
     */
    public interface AllAddressSizeStage {
        CreateSlidedStage withAutoDetectFlashSize(final Checkbox autoDetectFlashSize);
    }

    public interface CreateSlidedStage {
        Build createSlides();
    }

    /**
     * 6
     */
    public interface Build extends IBuilder<ShowDevices> {
    }

    public static class InnerBuilder implements EspDeviceInfoStage, EspDevicesCarouselStage, EsptoolServiceStage, UIStage, ConsoleOutPutStage,
            CreateSlidedStage, StartAddressStage, EndAddressSizeStage, AllAddressSizeStage, Build {
        private EspDevicesCarousel espDevicesCarousel;
        private EsptoolService esptoolService;
        private EspDeviceInfo espDeviceInfo;
        private ConsoleOutPut consoleOutPut;
        private UI ui;
        private TextField startAddressSize;
        private TextField endAddressSize;
        private Checkbox autoDetectFlashSize;

        @Override
        public UIStage withConsoleOutStage(ConsoleOutPut consoleOutPut) {
            this.consoleOutPut = consoleOutPut;
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
        public  AllAddressSizeStage withCustomFlashSizeAddress(TextField customFlashSizeAddress) {
            this.endAddressSize = customFlashSizeAddress;
            return this;
        }

        @Override
        public EndAddressSizeStage withStartSizeAddress(TextField startSizeAddress) {
            this.startAddressSize = startSizeAddress;
            return this;
        }

        @Override
        public CreateSlidedStage withAutoDetectFlashSize(Checkbox autoDetectFlashSize) {
            this.autoDetectFlashSize = autoDetectFlashSize;
            return this;
        }

        @Override
        public Build createSlides() {
            var macAddress = espDeviceInfo.macAddress();
            if (ifItContainsMacAddressShowMeTheSlides(macAddress)) {
                var flashSize = espDeviceInfo.detectedFlashSize();
                showEsp01s(flashSize);
                showEso82664MB(espDeviceInfo);
                showEsp8285(flashSize);
                showEsp32S3(espDeviceInfo);
            } else {
                ui.access(() -> {
                    NotificationBuilder.builder()
                            .withText("Error with microcontroller on port" + espDeviceInfo.port())
                            .withPosition(Position.MIDDLE)
                            .withDuration(3000)
                            .withIcon(VaadinIcon.WARNING)
                            .withThemeVariant(NotificationVariant.LUMO_ERROR)
                            .make();
                });
            }
            return this;
        }

        private boolean ifItContainsMacAddressShowMeTheSlides(String macAddress) {
            return Objects.nonNull(macAddress);
        }

        @Override
        public ShowDevices make() {

            return new ShowDevices();
        }


        private void showEsp8285(String flashSize) {
            if (espDeviceInfo.chipIs().equals("ESP8285H16") && flashSize.equals("2MB")) {

                //chipIs-currentTimeMillis-backupflash
//                                long currentTimeMillis = System.currentTimeMillis();
//                                String fileName = espDeviceInfo.chipIs().concat("-")
//                                        .concat(String.valueOf(currentTimeMillis))
//                                        .concat("-backupflash.bin");
//
//                                var downFlashButton = this.downloadFlash(ui, fileName, espDeviceInfo);
//
//                                Slide esp8285H16Slide = new Slide(createSlideContent(
//                                        "https://rubn0x52.com/assets/images/esp8285h08.jpg", espDeviceInfo, null));
//
//                                espDevicesCarousel.addSlide(esp8285H16Slide);

            }
        }

        /**
         * Show the esp01s slide
         *
         * @param flashSize
         */
        private void showEsp01s(String flashSize) {

            if (espDeviceInfo.chipType().endsWith("8266") && flashSize.equals("1MB")) {

                final FlashButtonWrapper flashButtonWrapper = new FlashButtonWrapper();

                var downloadTest = buttonForReadFlash(ui, espDeviceInfo, flashButtonWrapper);

                Slide esp01sSlide = new Slide(createSlideContent(
                        "https://www.electronicwings.com/storage/PlatformSection/TopicContent/308/description/esp8266%20module.jpg",
                        espDeviceInfo, downloadTest, flashButtonWrapper));

                espDevicesCarousel.addSlide(esp01sSlide);
            }
        }

        private void showEso82664MB(EspDeviceInfo espDeviceInfo) {
            if (espDeviceInfo.chipType().endsWith("8266") && espDeviceInfo.detectedFlashSize().equals("4MB")) {

//        //chipIs-currentTimeMillis-backupflash
//        long currentTimeMillis = System.currentTimeMillis();
//        String fileName = espDeviceInfo.chipIs().concat("-")
//                .concat(String.valueOf(currentTimeMillis))
//                .concat("-backupflash.bin");
//
//        var downFlashButton = this.downloadFlash(ui, fileName, espDeviceInfo);
//
//        Slide esp8266Slide = new Slide(createSlideContent(
//                "https://rubn0x52.com/assets/images/nodemcu-v3-wifi-esp8266-ch340.png",
//                espDeviceInfo,
//                downFlashButton.getDownloadFlashButton()));
//
//        espDevicesCarousel.addSlide(esp8266Slide);

            }
        }

        private void showEsp32S3(EspDeviceInfo espDeviceInfo) {
            if (espDeviceInfo.chipType().endsWith("-S3")) {
                final FlashButtonWrapper flashButtonWrapper = new FlashButtonWrapper();

                var downFlashButton = buttonForReadFlash(ui, espDeviceInfo, flashButtonWrapper);

                Slide esp32s3Slide = new Slide(createSlideContent(
                        "https://www.mouser.es/images/espressifsystems/hd/ESP32-S3-DEVKITC-1-N8_SPL.jpg",
                        espDeviceInfo, downFlashButton, flashButtonWrapper));

                espDevicesCarousel.addSlide(esp32s3Slide);
            }
        }

        /**
         * @param ui
         * @param espDeviceInfo
         * @param flashButtonWrapper
         * @return Button
         */
        private Button buttonForReadFlash(final UI ui, EspDeviceInfo espDeviceInfo, final FlashButtonWrapper flashButtonWrapper) {
            final Button downloadFlashButton = new Button("Read flash");
            downloadFlashButton.setTooltipText("Read flash");
            downloadFlashButton.getStyle().set("box-shadow", "0 2px 1px -1px rgba(0, 0, 0, .2), 0 1px 1px 0 rgba(0, 0, 0, .14), 0 1px 3px 0 rgba(0, 0, 0, .12)");
            downloadFlashButton.addClickListener(event -> {
                ui.access(() -> {
                    esptoolService.createEspBackUpFlashDirIfNotExists();

                    final String currentTimeMillis = String.valueOf(System.currentTimeMillis());
                    final String fileNameResult = espDeviceInfo.chipIs().concat("-")
                            .concat(currentTimeMillis).concat("-backup.bin");

                    final String writFileToTempDir = System.getProperty("java.io.tmpdir")
                            .concat("/esp-backup-flash-dir/")
                            .concat(fileNameResult);

                    this.readFlash(ui, writFileToTempDir, espDeviceInfo, flashButtonWrapper);

                });
            });
            return downloadFlashButton;
        }

        /**
         * <p> esptool.py --port /dev/ttyUSB1 read_flash 0 ALL /tmp/esp-backup-flash-dir/ESP8266EX-1720865320370-backup.bin <p/>
         *
         * @param ui the {@link UI} instance
         * @param writFileToTempDir
         * @param espDeviceInfo
         * @param flashButtonWrapper
         */
        private void readFlash(final UI ui, final String writFileToTempDir,
                               final EspDeviceInfo espDeviceInfo,
                               final FlashButtonWrapper flashButtonWrapper) {

            String processAutoDetectFlashSize = "";
            if(autoDetectFlashSize.getValue()) {
                processAutoDetectFlashSize = "ALL";
            } else {
                processAutoDetectFlashSize = "0x".concat(endAddressSize.getValue().trim());
            }

            final String[] commands = new String[]{
                    "esptool.py",
                    "--port", espDeviceInfo.port(),
                    "--baud", String.valueOf(BaudRates.BAUD_RATE_115200.getBaudRate()),
                    "read_flash",
                    startAddressSize.getValue().isEmpty() ? "0" : startAddressSize.getValue().trim(),
                    processAutoDetectFlashSize,
                    writFileToTempDir
            };

//        this.commands = new String[]{
//                "esptool.py",
//                "--port", espDeviceInfo.port(),
//                "--baud", String.valueOf(BaudRates.BAUD_RATE_115200.getBaudRate()),
//                "flash_id"
//        };

            CommandsOnFirstLine.putCommansdOnFirstLine(commands, consoleOutPut);

            esptoolService.downloadFlash(commands)
                    .doOnComplete(() -> {
                        ui.access(() -> {
                            consoleOutPut.writePrompt();
                            if (Files.exists(Path.of(writFileToTempDir))) {
                                log.info("Backup completado! {}", "");
                                NotificationBuilder.builder()
                                        .withText("Backup completado!")
                                        .withPosition(Position.MIDDLE)
                                        .withDuration(3000)
                                        .withIcon(VaadinIcon.INFO)
                                        .withThemeVariant(NotificationVariant.LUMO_PRIMARY)
                                        .make();
                                flashButtonWrapper.enableAnchorForDownloadTheFirmware(writFileToTempDir);
                            } else {
                                NotificationBuilder.builder()
                                        .withText("Stream reactivo completado pero la flash no exsite en el tmp! " + writFileToTempDir)
                                        .withPosition(Position.MIDDLE)
                                        .withDuration(3000)
                                        .withIcon(VaadinIcon.INFO)
                                        .withThemeVariant(NotificationVariant.LUMO_PRIMARY)
                                        .make();
                            }
                        });
                    })
                    .doOnError(onError -> {
                        ui.access(() -> {
                            log.info("Error: {}", onError);
                            NotificationBuilder.builder()
                                    .withText("Error al crear backup de esta flash " + onError)
                                    .withPosition(Position.MIDDLE)
                                    .withDuration(3000)
                                    .withIcon(VaadinIcon.WARNING)
                                    .withThemeVariant(NotificationVariant.LUMO_ERROR)
                                    .make();
                        });
                    })
                    .subscribe(inputLine -> {
                        ui.access(() -> {
                            consoleOutPut.readFlash(inputLine);
                        });
                    });

        }

    }


}