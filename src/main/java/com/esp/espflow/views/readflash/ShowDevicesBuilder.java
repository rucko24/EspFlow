package com.esp.espflow.views.readflash;

import com.esp.espflow.entity.AddressRecord;
import com.esp.espflow.entity.EspDeviceInfoRecord;
import com.esp.espflow.entity.event.EsptoolFRWMessageListItemEvent;
import com.esp.espflow.enums.BaudRatesEnum;
import com.esp.espflow.exceptions.CreateEspBackUpFlashDirException;
import com.esp.espflow.mappers.ExtractChipIsFromStringMapper;
import com.esp.espflow.service.EsptoolPathService;
import com.esp.espflow.service.EsptoolService;
import com.esp.espflow.service.downloader.FlashDownloadButtonService;
import com.esp.espflow.service.downloader.FlashDownloadButtonWrapper;
import com.esp.espflow.service.strategy.filterespslide.FilterEsp01s;
import com.esp.espflow.service.strategy.filterespslide.FilterEsp32S3;
import com.esp.espflow.service.strategy.filterespslide.FilterEsp32S3Mini;
import com.esp.espflow.service.strategy.filterespslide.FilterEsp8266CH340G;
import com.esp.espflow.service.strategy.filterespslide.FilterEsp8266Cp210xAmica;
import com.esp.espflow.service.strategy.filterespslide.FilterEsp828852MB;
import com.esp.espflow.service.strategy.filterespslide.FilterEspDeviceContext;
import com.esp.espflow.util.CommandsOnFirstLine;
import com.esp.espflow.util.ConfirmDialogBuilder;
import com.esp.espflow.util.IBuilder;
import com.esp.espflow.util.console.OutPutConsole;
import com.flowingcode.vaadin.addons.carousel.Slide;
import com.infraleap.animatecss.Animated;
import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.binder.Binder;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Sinks;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static com.esp.espflow.util.EspFlowConstants.BAUD_RATE;
import static com.esp.espflow.util.EspFlowConstants.BOX_SHADOW_PROPERTY;
import static com.esp.espflow.util.EspFlowConstants.BOX_SHADOW_VAADIN_BUTTON;
import static com.esp.espflow.util.EspFlowConstants.BOX_SHADOW_VALUE;
import static com.esp.espflow.util.EspFlowConstants.FRONTEND_IMAGES_CUSTOM;
import static com.esp.espflow.util.EspFlowConstants.FRONTEND_IMAGES_ESPDEVICES;
import static com.esp.espflow.util.EspFlowConstants.JAVA_IO_TEMPORAL_DIR_OS;
import static com.esp.espflow.util.EspFlowConstants.PORT;
import static com.esp.espflow.util.EspFlowConstants.READ_FLASH;
import static com.esp.espflow.views.readflash.EspDevicesCarousel.createSlideContent;
import static com.infraleap.animatecss.Animated.Modifier.INFINITE;

/**
 * <p><strong>ShowDevices</strong>  this class serves as an aid to display in the <strong>ReadFirmwareView</strong> the microcontrollers available in the OS.</p>
 *
 * <p>With this StepBuilder we can set it up avoiding the nulls, and with obligatory steps </p>
 *
 * <blockquote>
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
 *             .withEsptoolPathService(this.esptoolPathService)
 *             .withFlashDownloadButton(this.flashButtonWrapperService)
 *             .withPublisher(this.publishMessageListItem)
 *             .applyStrategiesWithCustomContentCreationPerSlide()
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
        ConsoleOutPutStage withEspDeviceInfo(EspDeviceInfoRecord espDeviceInfoRecord);
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
        EsptoolPathServiceStage withBaudRatesComboBox(final ComboBox<BaudRatesEnum> baudRatesComboBox);
    }

    /**
     * 10
     */
    public interface EsptoolPathServiceStage {
        FlashDownloadWrapperStage withEsptoolPathService(EsptoolPathService esptoolPathService);
    }

    /**
     * 11
     */
    public interface FlashDownloadWrapperStage {
        PublishMessageListItemStage withFlashDownloadButton(FlashDownloadButtonService flashDownloadButton);
    }

    /**
     * 12
     */
    public interface PublishMessageListItemStage {
        StrategiesPlusCustomContentCreationPerSlideStage withPublisher(Sinks.Many<EsptoolFRWMessageListItemEvent> publishMessageListItem);
    }

    /**
     * 13
     */
    public interface StrategiesPlusCustomContentCreationPerSlideStage {
        Build applyStrategiesWithCustomContentCreationPerSlide();
    }

    /**
     * 14
     */
    public interface Build extends IBuilder<EspDevicesCarousel> {
    }

    /**
     * The InnerBuilder
     */
    public static class InnerBuilder implements EspDeviceInfoStage, EspDevicesCarouselStage,
            EsptoolServiceStage, UIStage, ConsoleOutPutStage, StartAddressStage,
            EndAddressSizeStage, AllAddressSizeStage, BaudRateStage, EsptoolPathServiceStage, FlashDownloadWrapperStage,
            PublishMessageListItemStage, StrategiesPlusCustomContentCreationPerSlideStage, Build {

        private EspDevicesCarousel espDevicesCarousel;
        private EsptoolService esptoolService;
        private EspDeviceInfoRecord espDeviceInfoRecord;
        private OutPutConsole outPutConsole;
        private UI ui;
        private IntegerField startAddressSize;
        private IntegerField customSizeToRead;
        private ToggleButton autoDetectFlashSize;
        private ComboBox<BaudRatesEnum> baudRatesComboBox;
        private EsptoolPathService esptoolPathService;
        private FlashDownloadButtonService flashDownloadButtonService;
        private Sinks.Many<EsptoolFRWMessageListItemEvent> publishMessageListItem;

        /**
         * To bind {@link AddressRecord}
         */
        private final Binder<AddressRecord> addressBinderRecord = new Binder<>();

        @Override
        public UIStage withOutPutConsole(OutPutConsole outPutConsole) {
            this.outPutConsole = outPutConsole;
            return this;
        }

        @Override
        public ConsoleOutPutStage withEspDeviceInfo(EspDeviceInfoRecord espDeviceInfoRecord) {
            this.espDeviceInfoRecord = espDeviceInfoRecord;
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
            var customSize = customFlashSizeAddress.getValue().toString();
            if (customSize.matches("\\d+") || customSize.isEmpty()) {
                this.customSizeToRead = customFlashSizeAddress;
            } else {
                customFlashSizeAddress.clear();  // delete if it contains foreign characters and set again.
                this.customSizeToRead = customFlashSizeAddress;
            }
            return this;
        }

        @Override
        public EndAddressSizeStage withStartSizeAddress(IntegerField startSizeAddress) {
            var startSize = startSizeAddress.getValue().toString();
            if (startSize.matches("\\d+") || startSize.isEmpty()) {
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
        public EsptoolPathServiceStage withBaudRatesComboBox(ComboBox<BaudRatesEnum> baudRatesComboBox) {
            this.baudRatesComboBox = baudRatesComboBox;
            return this;
        }

        @Override
        public FlashDownloadWrapperStage withEsptoolPathService(EsptoolPathService esptoolPathService) {
            this.esptoolPathService = esptoolPathService;
            return this;
        }

        @Override
        public PublishMessageListItemStage withFlashDownloadButton(FlashDownloadButtonService flashDownloadButtonService) {
            this.flashDownloadButtonService = flashDownloadButtonService;
            return this;
        }

        @Override
        public StrategiesPlusCustomContentCreationPerSlideStage withPublisher(Sinks.Many<EsptoolFRWMessageListItemEvent> publishMessageListItem) {
            this.publishMessageListItem = publishMessageListItem;
            return this;
        }

        @Override
        public Build applyStrategiesWithCustomContentCreationPerSlide() {
            var macAddress = espDeviceInfoRecord.macAddress();
            if (ifItContainsMacAddressShowMeTheSlides(macAddress)) {
                //avoid Cannot invoke "com.vaadin.flow.server.VaadinService.getInstantiator()" because "service" is null
                ui.access(() -> {
                    this.showEsp01s();
                    this.showEsp8266340G4MB();
                    this.showEsp82664Cp201x4MB();
                    this.showEsp8285();
                    this.showEsp32S3();
                    this.showEsp32S3Mini();
                });
            }
            return this;
        }

        private boolean ifItContainsMacAddressShowMeTheSlides(String macAddress) {
            return Objects.nonNull(macAddress);
        }

        /**
         * Show the esp01s 1MB slide
         */
        private void showEsp01s() {
            final FilterEspDeviceContext filterEspDeviceContext = new FilterEspDeviceContext(new FilterEsp01s());

            if (filterEspDeviceContext.filter(espDeviceInfoRecord)) {

                final FlashDownloadButtonWrapper flashDownloadButtonWrapper = flashDownloadButtonService.getFlashDownloadButtonWrapper();

                var downFlashButton = buttonForReadFlash(ui, flashDownloadButtonWrapper);

                Slide esp01sSlide = new Slide(createSlideContent(
                        FRONTEND_IMAGES_ESPDEVICES + "esp01s-1MB.jpg",
                        espDeviceInfoRecord, downFlashButton, flashDownloadButtonWrapper));

                espDevicesCarousel.addSlide(esp01sSlide);
            } else {
                //log.info("Cannot create the Slide from esp01s {}", espDeviceInfo);
            }

        }

        /**
         * Show the ESP8266 chip 340G slide
         **/
        private void showEsp8266340G4MB() {
            final FilterEspDeviceContext filterEspDeviceContext = new FilterEspDeviceContext(new FilterEsp8266CH340G());

            if (filterEspDeviceContext.filter(espDeviceInfoRecord)) {

                final FlashDownloadButtonWrapper flashDownloadButtonWrapper = flashDownloadButtonService.getFlashDownloadButtonWrapper();

                var downFlashButton = buttonForReadFlash(ui, flashDownloadButtonWrapper);

                Slide esp8266Slide = new Slide(createSlideContent(
                        FRONTEND_IMAGES_ESPDEVICES + "esp8266-4MB.png",
                        espDeviceInfoRecord, downFlashButton, flashDownloadButtonWrapper));

                espDevicesCarousel.addSlide(esp8266Slide);

            } else {
                //log.info("Cannot create the Slide from esp8266-4MB {}", espDeviceInfo);
            }

        }

        /**
         * Show the ESP8266 4MG amica slide
         */
        private void showEsp82664Cp201x4MB() {
            final FilterEspDeviceContext filterEspDeviceContext = new FilterEspDeviceContext(new FilterEsp8266Cp210xAmica());

            if (filterEspDeviceContext.filter(espDeviceInfoRecord)) {

                final FlashDownloadButtonWrapper flashDownloadButtonWrapper = flashDownloadButtonService.getFlashDownloadButtonWrapper();

                var downFlashButton = buttonForReadFlash(ui, flashDownloadButtonWrapper);

                Slide esp8266Slide = new Slide(createSlideContent(
                        FRONTEND_IMAGES_ESPDEVICES + "esp8266-cp201x.png",
                        espDeviceInfoRecord, downFlashButton, flashDownloadButtonWrapper));

                espDevicesCarousel.addSlide(esp8266Slide);

            } else {
                //log.info("Cannot create the Slide from esp8266-cp201x AMICA {}", espDeviceInfo);
            }

        }

        /**
         * Show the ESP8285H16 2MB slide
         */
        private void showEsp8285() {
            final FilterEspDeviceContext filterEspDeviceContext = new FilterEspDeviceContext(new FilterEsp828852MB());

            if (filterEspDeviceContext.filter(espDeviceInfoRecord)) {

                final FlashDownloadButtonWrapper flashDownloadButtonWrapper = flashDownloadButtonService.getFlashDownloadButtonWrapper();

                var downloadTest = buttonForReadFlash(ui, flashDownloadButtonWrapper);

                Slide esp8285H16Slide = new Slide(createSlideContent(
                        FRONTEND_IMAGES_ESPDEVICES + "ESP8285H08-2MB.jpeg",
                        espDeviceInfoRecord, downloadTest, flashDownloadButtonWrapper));

                espDevicesCarousel.addSlide(esp8285H16Slide);

            } else {
                //log.info("Cannot create the Slide from esp8255 {}", espDeviceInfo);
            }

        }

        /**
         * Show the ESP32-s3 slide
         */
        private void showEsp32S3() {
            final FilterEspDeviceContext filterEspDeviceContext = new FilterEspDeviceContext(new FilterEsp32S3());

            if (filterEspDeviceContext.filter(espDeviceInfoRecord)) {

                final FlashDownloadButtonWrapper flashDownloadButtonWrapper = flashDownloadButtonService.getFlashDownloadButtonWrapper();

                Button downFlashButton = this.buttonForReadFlash(ui, flashDownloadButtonWrapper);

                Slide esp32s3Slide = new Slide(createSlideContent(
                        FRONTEND_IMAGES_ESPDEVICES + "ESP32-S3-DEVKITC-1-N8_SPL.webp",
                        espDeviceInfoRecord, downFlashButton, flashDownloadButtonWrapper));

                espDevicesCarousel.addSlide(esp32s3Slide);
            } else {
                //log.info("Cannot create the Slide from ESP32-S3-DEVKITC-1-N8_SPL {}", espDeviceInfo);
            }

        }

        /**
         * Show the ESP32-s3 Mini slide
         */
        private void showEsp32S3Mini() {
            final FilterEspDeviceContext filterEspDeviceContext = new FilterEspDeviceContext(new FilterEsp32S3Mini());

            if (filterEspDeviceContext.filter(espDeviceInfoRecord)) {

                final FlashDownloadButtonWrapper flashDownloadButtonWrapper = flashDownloadButtonService.getFlashDownloadButtonWrapper();

                Button downFlashButton = this.buttonForReadFlash(ui, flashDownloadButtonWrapper);

                Slide esp32s3MiniSlide = new Slide(createSlideContent(
                        FRONTEND_IMAGES_ESPDEVICES + "Esp32-S3-Mini.png",
                        espDeviceInfoRecord, downFlashButton, flashDownloadButtonWrapper));

                espDevicesCarousel.addSlide(esp32s3MiniSlide);
            } else {
                //log.info("Cannot create the Slide from ESP32-S3-DEVKITC-1-N8_SPL {}", espDeviceInfo);
            }

        }

        /**
         * This button contains the listener that will do the reading to create the carousel with the detected ports.
         *
         * @param ui
         * @param flashDownloadButtonWrapper
         * @return A {@link Button} con el listener para la lectura
         */
        private Button buttonForReadFlash(final UI ui, final FlashDownloadButtonWrapper flashDownloadButtonWrapper) {
            final Button downloadFlashButton = new Button("Read flash");
            downloadFlashButton.setTooltipText("Read flash");
            downloadFlashButton.addClassName(BOX_SHADOW_VAADIN_BUTTON);
            /*
             * Execute the Binder 🔥🚒
             */
            addressBinderRecord.forField(startAddressSize)
                    .withValidator(text -> text.toString().matches("\\d+")
                            || text.toString().isEmpty(), "Invalid input, set only numbers")
                    .bind(AddressRecord::startAddressSize, (addressRecord, value) -> {
                    });
            addressBinderRecord.forField(customSizeToRead)
                    .withValidator(text -> text.toString().matches("\\d+"),
                            "Invalid input, set only numbers and greater than zero")
                    .bind(AddressRecord::customAddressSize, (addressRecord, value) -> {
                    });

            downloadFlashButton.addClickListener(event -> {
                ui.access(() -> {
                    var newRecord = new AddressRecord(startAddressSize.getValue(), customSizeToRead.getValue());
                    if (addressBinderRecord.writeBeanIfValid(newRecord)) {
                        this.validate(flashDownloadButtonWrapper);
                    } else {
                        ConfirmDialogBuilder.showWarning("Invalid input, please check!");
                    }
                });
            });
            return downloadFlashButton;
        }

        /**
         * This will validate the textfield with the final address and the checkbox
         *
         * @param flashDownloadButtonWrapper
         */
        private void validate(final FlashDownloadButtonWrapper flashDownloadButtonWrapper) {
            String processAutoDetectFlashSize = "";
            if (Boolean.TRUE.equals(autoDetectFlashSize.getValue())) {
                processAutoDetectFlashSize = "ALL";
                this.readFlash(flashDownloadButtonWrapper, processAutoDetectFlashSize);
            } else {
                if (customSizeToRead.getValue() != 0) {
                    processAutoDetectFlashSize = "0x".concat(String.valueOf(customSizeToRead.getValue()));
                    this.readFlash(flashDownloadButtonWrapper, processAutoDetectFlashSize);
                } else {
                    customSizeToRead.focus();
                    String text = "Please set the custom size greater than zero, or enable the toggle button for full readability.";
                    ConfirmDialogBuilder.showWarning(text, showImageWithInformationAboutToggleButton(text));
                }

            }
        }

        /**
         * Displays an image with an arrow with HEART_BEAT, INFINITE effect, pointing to the toggle button
         *
         * @return A {@link Component} with information about the toggle button
         */
        private Component showImageWithInformationAboutToggleButton(String text) {
            final Image image = new Image(FRONTEND_IMAGES_CUSTOM + "enable-toggle-button.png", "alt");
            image.setWidth("50%");
            image.setHeight("50%");
            image.getStyle().set(BOX_SHADOW_PROPERTY, BOX_SHADOW_VALUE);
            Animated.animate(image, Animated.Animation.FADE_IN);

            final VerticalLayout content = new VerticalLayout(new com.vaadin.flow.component.Text(text));

            final Icon iconArrowRight = VaadinIcon.ARROW_RIGHT.create();
            Tooltip.forComponent(iconArrowRight).setText("Enable the toggle button!!!");
            iconArrowRight.setSize("30px");
            Animated.animate(iconArrowRight, Animated.Animation.HEART_BEAT, INFINITE);

            final HorizontalLayout row = new HorizontalLayout(iconArrowRight, image);
            row.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
            row.setVerticalComponentAlignment(FlexComponent.Alignment.END, iconArrowRight);
            content.add(row);
            content.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, row);

            return content;
        }

        /**
         * @param flashDownloadButtonWrapper
         * @param processAutoDetectFlashSize
         */
        private String readFlash(final FlashDownloadButtonWrapper flashDownloadButtonWrapper, String processAutoDetectFlashSize) {
            try {
                esptoolService.createEspBackUpFlashDirIfNotExists();
            } catch (CreateEspBackUpFlashDirException e) {
                log.error("Error {} ", e.getMessage());
            }

            final String currentTimeMillis = String.valueOf(System.currentTimeMillis());

            final String fileNameResult = espDeviceInfoRecord.chipIs().concat("-")
                    .concat(currentTimeMillis).concat("-backup.bin");

            final String writFileToTempDir = JAVA_IO_TEMPORAL_DIR_OS
                    .concat("/esp-backup-flash-dir/")
                    .concat(fileNameResult);

            this.readFlash(ui, writFileToTempDir, espDeviceInfoRecord, flashDownloadButtonWrapper, processAutoDetectFlashSize);

            return writFileToTempDir;
        }

        /**
         * <blockquote>
         * <pre>esptool.py --port /dev/ttyUSB1 read_flash 0 ALL /tmp/esp-backup-flash-dir/ESP8266EX-1720865320370-backup.bin</pre>
         * </blockquote><p>
         *
         * @param ui                         the {@link UI} instance
         * @param writFileToTempDir
         * @param espDeviceInfoRecord
         * @param flashDownloadButtonWrapper
         * @param processAutoDetectFlashSize
         */
        private void readFlash(final UI ui, final String writFileToTempDir,
                               final EspDeviceInfoRecord espDeviceInfoRecord,
                               final FlashDownloadButtonWrapper flashDownloadButtonWrapper,
                               final String processAutoDetectFlashSize) {

            final String[] commands = {
                    esptoolPathService.esptoolPath(),
                    PORT, espDeviceInfoRecord.port(),
                    BAUD_RATE, this.baudRatesComboBox.getValue().toString().split(" ")[0],
                    READ_FLASH,
                    startAddressSize.getValue().toString().isEmpty() ? "0" : startAddressSize.getValue().toString().trim(),
                    processAutoDetectFlashSize,
                    writFileToTempDir
            };

            CommandsOnFirstLine.putCommansdOnFirstLine(commands, outPutConsole);

            this.esptoolService.readFlash(commands)
                    .doOnError(onError -> {
                        ui.access(() -> {
                            log.info("Error: {}", onError);
                            ConfirmDialogBuilder.showWarning("Error creating backup of this flash " + onError);
                        });
                    })
                    .doOnComplete(() -> {
                        ui.access(() -> {
                            outPutConsole.writePrompt();
                            if (Files.exists(Path.of(writFileToTempDir))) {
                                log.info("Backup completed successfully! {}", "");
                                ConfirmDialogBuilder.showInformation("Backup completed successfully!");
                                flashDownloadButtonWrapper.enableAnchorForDownloadTheFirmware(writFileToTempDir);

                                String chipIs = ExtractChipIsFromStringMapper.INSTANCE.getChipIsFromThisString(outPutConsole.scrollBarBuffer());

                                EsptoolFRWMessageListItemEvent esptoolFRWMessageListItemEvent = new EsptoolFRWMessageListItemEvent(
                                        chipIs.concat(" Flash successfully read!!!"),
                                        espDeviceInfoRecord.port());

                                this.publishMessageListItem.tryEmitNext(esptoolFRWMessageListItemEvent);

                            } else {
                                ConfirmDialogBuilder.showWarning("The flash does not exist in the tmp " + writFileToTempDir);
                            }
                        });
                    })
                    .subscribe(inputLine -> {
                        ui.access(() -> {
                            outPutConsole.readFlash(inputLine);
                        });
                    });

        }

        @Override
        public EspDevicesCarousel make() {
            return espDevicesCarousel;
        }

    }

}