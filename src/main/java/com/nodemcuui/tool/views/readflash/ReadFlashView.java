package com.nodemcuui.tool.views.readflash;

import com.flowingcode.vaadin.addons.carousel.Slide;
import com.nodemcuui.tool.data.service.ComPortService;
import com.nodemcuui.tool.data.service.CommandService;
import com.nodemcuui.tool.data.service.EsptoolService;
import com.nodemcuui.tool.data.util.NotificationBuilder;
import com.nodemcuui.tool.data.util.ResponsiveHeaderDiv;
import com.nodemcuui.tool.data.util.console.ConsoleCommandOutPutArea;
import com.nodemcuui.tool.views.MainLayout;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout.Orientation;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.JustifyContent;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin.Bottom;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import java.util.stream.Stream;

import static com.nodemcuui.tool.data.util.UiToolConstants.HIDDEN;
import static com.nodemcuui.tool.data.util.UiToolConstants.OVERFLOW_X;
import static com.nodemcuui.tool.data.util.UiToolConstants.OVERFLOW_Y;
import static com.nodemcuui.tool.views.readflash.EspDevicesCarousel.createSlideContent;

/**
 * @author rubn
 */
@Log4j2
@UIScope
@SpringComponent
@PageTitle("Read flash")
@Route(value = "read-flash", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@RequiredArgsConstructor
public class ReadFlashView extends Div implements ResponsiveHeaderDiv {

    private final CommandService commandService;
    private final ComPortService comPortService;
    private final EsptoolService esptoolService;
    private final EspDevicesCarousel espDevicesCarousel = new EspDevicesCarousel();

    private final VerticalLayout content = new VerticalLayout();
    private final ProgressBar progressBar = new ProgressBar();

    /**
     * Console output area
     */
    private final ConsoleCommandOutPutArea consoleCommandOutPutArea = new ConsoleCommandOutPutArea();
    private final HorizontalLayout footer = new HorizontalLayout();
    private final Span flashSize = new Span("Flash size: reading...");
    private final Span decimalSize = new Span("Decimal: reading...");
    private final Span hexSize = new Span("Hex: reading...");

    @PostConstruct
    public void init() {
        super.setSizeFull();
        super.getStyle().set("display", "flex");
        super.getStyle().set("flex-direction", "column");

        final SplitLayout splitLayout = getSplitLayout();
        super.add(splitLayout);
    }

    private SplitLayout getSplitLayout() {
        this.progressBar.setVisible(false);
        this.progressBar.setIndeterminate(true);
        this.content.setSpacing(false);
        this.content.addClassNames(AlignItems.CENTER, JustifyContent.CENTER);
        this.content.add(progressBar);

        final var splitLayout = new SplitLayout(Orientation.VERTICAL);

        splitLayout.setSplitterPosition(60);
        splitLayout.setSizeFull();
        splitLayout.addToPrimary(content);
        splitLayout.getStyle().set(OVERFLOW_Y, HIDDEN);

        final var footer = this.getFooter();
        consoleCommandOutPutArea.removeClassName(Bottom.SMALL);
        consoleCommandOutPutArea.getStyle().set(OVERFLOW_Y, "unset");
        final var verticalLayoutToSecondary = new VerticalLayout(consoleCommandOutPutArea, footer);
        verticalLayoutToSecondary.setFlexGrow(1, consoleCommandOutPutArea);

        verticalLayoutToSecondary.getStyle().set(OVERFLOW_Y, HIDDEN);
        verticalLayoutToSecondary.getStyle().set("background", "linear-gradient(var(--lumo-shade-5pct), var(--lumo-shade-5pct))");
        splitLayout.addToSecondary(verticalLayoutToSecondary);
        splitLayout.setSplitterPosition(60);

        splitLayout.getStyle().set(OVERFLOW_X, HIDDEN);
        splitLayout.getPrimaryComponent().getElement().getStyle().set(OVERFLOW_X, HIDDEN);
        splitLayout.getSecondaryComponent().getElement().getStyle().set(OVERFLOW_X, HIDDEN);

        return splitLayout;
    }

    /**
     * @return HorizontalLayout
     */
    private HorizontalLayout getFooter() {
        final Div div = new Div(this.flashSize);
        final Div div2 = new Div(this.decimalSize);
        final Div div3 = new Div(this.hexSize);

        footer.setWidthFull();
        footer.setAlignItems(Alignment.CENTER);
        footer.setJustifyContentMode(JustifyContentMode.AROUND);
        footer.add(div, div2, div3);

        return footer;
    }

    @SneakyThrows
    private void showDetectedDevices(final UI ui) {

        this.esptoolService.readAllDevices()
                .doOnError(error -> {
                    ui.access(() -> {
                        log.info("Error: {}", error);
                        NotificationBuilder.builder()
                                .withText("Error al leer el microcontrolador " + error)
                                .withPosition(Position.MIDDLE)
                                .withDuration(3000)
                                .withIcon(VaadinIcon.WARNING)
                                .withThemeVariant(NotificationVariant.LUMO_ERROR)
                                .make();
                    });
                })
                .doOnComplete(() -> {
                    ui.access(() -> {
                        this.progressBar.setVisible(false);
                        espDevicesCarousel.createSlides();
                        this.content.add(espDevicesCarousel);
                    });
                })
                .subscribe(espDeviceInfo -> {
                    ui.access(() -> {
                        this.progressBar.setVisible(true);
                        final String flashSize = espDeviceInfo.detectedFlashSize();
                        if (flashSize != null) {
                            this.flashSize.setText("Flash size: " + flashSize);
                            this.decimalSize.setText("Decimal: " + espDeviceInfo.decimal());
                            this.hexSize.setText("Hex: 0x" + espDeviceInfo.hex());
                        }
                        final String chipType = espDeviceInfo.chipType();
                        if (chipType != null) {
                            if (chipType.endsWith("8266") && flashSize.equals("1MB")) {
                                Slide esp01sSlide = new Slide(createSlideContent("https://www.electronicwings.com/storage/PlatformSection/TopicContent/308/description/esp8266%20module.jpg", espDeviceInfo));

                                espDevicesCarousel.addSlide(esp01sSlide);
                            }

                            if(espDeviceInfo.chipIs().equals("ESP8285H16") && flashSize.equals("2MB")) {

                                Slide esp8285H16Slide = new Slide(createSlideContent("https://rubn0x52.com/assets/images/esp8285h08.jpg", espDeviceInfo));

                                espDevicesCarousel.addSlide(esp8285H16Slide);
                            }

                            if(chipType.endsWith("8266") && flashSize.equals("4MB")) {
                                Slide esp8266Slide = new Slide(createSlideContent("https://rubn0x52.com/assets/images/nodemcu-v3-wifi-esp8266-ch340.png", espDeviceInfo));
                                espDevicesCarousel.addSlide(esp8266Slide);
                            }

                            if (chipType.endsWith("-S3")) {

                                Slide esp32s3Slide = new Slide(createSlideContent("https://www.mouser.es/images/espressifsystems/hd/ESP32-S3-DEVKITC-1-N8_SPL.jpg", espDeviceInfo));

                                espDevicesCarousel.addSlide(esp32s3Slide);
                            }
                        }
                    });

                });

        Stream.of(flashSize, this.decimalSize, this.hexSize)
                .forEach(span -> {
                    span.getStyle().set("font-size", "var(--lumo-font-size-xs)");
                    span.addClassName("row-span-flash-size-footer");
                });

    }

    /**
     * PORT /dev/ttyUSB1, COM
     * <p>
     * esptool.py --port /dev/ttyUSB1 read_flash 0 ALL esp8266-backupflash.bin
     *
     * @param ui
     */
    private void readFlash(final UI ui) {

    }


    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        if (attachEvent.isInitialAttach()) {
            final UI ui = attachEvent.getUI();
            this.showDetectedDevices(ui);

        }
    }
}
