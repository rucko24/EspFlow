package com.esp.espflow.views.readflash;

import com.esp.espflow.entity.EspDeviceInfoRecord;
import com.esp.espflow.service.downloader.FlashDownloadButtonWrapper;
import com.esp.espflow.util.GetOsName;
import com.esp.espflow.util.svgfactory.SvgFactory;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.FlexDirection;
import com.vaadin.flow.theme.lumo.LumoUtility.JustifyContent;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin.Left;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin.Right;
import lombok.Getter;
import org.vaadin.olli.ClipboardHelper;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Objects;
import java.util.stream.Stream;

import static com.esp.espflow.util.EspFlowConstants.BOX_SHADOW_VAADIN_BUTTON;
import static com.esp.espflow.util.EspFlowConstants.CHIP_IS;
import static com.esp.espflow.util.EspFlowConstants.CHIP_TYPE;
import static com.esp.espflow.util.EspFlowConstants.CRYSTAL_IS;
import static com.esp.espflow.util.EspFlowConstants.DETECTED_FLASH_SIZE;
import static com.esp.espflow.util.EspFlowConstants.MAC;

/**
 * The DeviceCardLayout
 */
@Getter
public final class DeviceCardLayout extends Div {

    private final Div divToolbarRow = new Div();
    private final Div divSlideOverview = new Div();
    private final Div divLeft = new Div();
    private final Div divOverlay = new Div();
    private final Div divLeftContent = new Div();
    private final Div divLeftContentInside1 = new Div();
    private final Div divLeftContentInside2 = new Div();
    private final Div divRightParent = new Div();
    private final Div divRightContent = new Div();
    private final Div divRightSubHeader = new Div();
    private final Div divRightContentText = new Div();

    private final Span spanDeviceName = new Span();
    private final Span spanState = new Span("AVAILABLE");
    private final Span spanPort = new Span();
    private final Span spanFriendlyName = new Span();

    private final Span chipType = new Span(CHIP_TYPE);
    private final Span chipTypeValue = new Span();
    private final Hr hr1 = new Hr();

    private final Span detectedFlashSize = new Span(DETECTED_FLASH_SIZE);
    private final Span detectedFlashSizeValue = new Span();
    private final Hr hr2 = new Hr();

    private final Span crystal = new Span(CRYSTAL_IS);
    private final Span crystalValue = new Span();
    private final Hr hr3 = new Hr();

    private final Span chipIs = new Span(CHIP_IS);
    private final Span chipIsValue = new Span();
    private final Hr hr4 = new Hr();

    private final Span spanMadAddress = new Span(MAC);
    private final Span spanMadAddressValue = new Span();
    private final Hr hr5 = new Hr();

    /**
     * The download flash button
     */
    private Button downloadFlashButton;
    private FlashDownloadButtonWrapper flashDownloadButtonWrapper;

    private String image;
    private EspDeviceInfoRecord espDeviceInfoRecord;

    /**
     * Create a DeviceCardLayout div
     *
     * @param image
     * @param espDeviceInfoRecord
     * @param downloadFlashButton
     * @param flashDownloadButtonWrapper
     *
     * @return A {@link DeviceCardLayout}
     */
    public static DeviceCardLayout of(final String image, final EspDeviceInfoRecord espDeviceInfoRecord,
                            final Button downloadFlashButton,
                            final FlashDownloadButtonWrapper flashDownloadButtonWrapper) {
       return new DeviceCardLayout(image, espDeviceInfoRecord, downloadFlashButton, flashDownloadButtonWrapper);
    }

    private DeviceCardLayout(final String image, final EspDeviceInfoRecord espDeviceInfoRecord,
                            final Button downloadFlashButton,
                            final FlashDownloadButtonWrapper flashDownloadButtonWrapper) {
        super.addClassName("card-container");
        this.image = image;
        this.espDeviceInfoRecord = espDeviceInfoRecord;
        this.downloadFlashButton = downloadFlashButton;
        this.flashDownloadButtonWrapper = flashDownloadButtonWrapper;

        final Div toolbar = this.createDivToolbarRow();
        final Div slideOverView = this.createDivSlideOverview();

        super.add(toolbar, slideOverView);
    }

    private Div createDivToolbarRow() {
        divToolbarRow.addClassName("toolbar-row");
        divToolbarRow.add(createSpanEspDeviceTitle(), createDivControls());
        return divToolbarRow;
    }

    private Span createSpanEspDeviceTitle() {
        Span spanEspDeviceTitle = new Span("Device");
        spanEspDeviceTitle.addClassName("span-esp-device-title");
        return spanEspDeviceTitle;
    }

    private Div createDivControls() {
        Div divControls = new Div();
        divControls.addClassName("div-controls");

        if (Objects.nonNull(downloadFlashButton)) {
            divControls.add(downloadFlashButton);
        }
        if (Objects.nonNull(flashDownloadButtonWrapper)) {
            divControls.add(flashDownloadButtonWrapper);
        }

        return divControls;
    }

    private Div createDivSlideOverview() {
        divSlideOverview.addClassName("div-slide-overview");
        divSlideOverview.add(createDivLeft());
        divSlideOverview.add(createDivRightParent());
        return divSlideOverview;
    }

    private Div createDivLeft() {
        divLeft.addClassName("div-left");
        divLeft.getStyle().set("background-image", "url('" + image + "')");
        divLeft.add(createDivOverlay());
        return divLeft;
    }

    private Div createDivOverlay() {
        divOverlay.addClassName("div-overlay");
        divOverlay.add(createDivLeftContent());
        return divOverlay;
    }

    private Div createDivLeftContent() {
        divLeftContent.addClassName("div-left-content");
        divLeftContent.add(createDivLeftContentInside1(), createDivLeftContentInside2());
        return divLeftContent;
    }

    private Div createDivLeftContentInside1() {
        spanDeviceName.setText(espDeviceInfoRecord.chipType());
        divLeftContentInside1.add(spanDeviceName);
        divLeftContentInside1.addClassName("div-left-content-inside-title1");
        return divLeftContentInside1;
    }

    /**
     * @return A {@link Div}
     */
    public Div createDivLeftContentInside2() {
        var icon = VaadinIcon.CHECK_CIRCLE.create();
        icon.getStyle().set("color","blue");
        icon.addClassName(Left.SMALL);
        spanState.add(icon);
        spanPort.setText(espDeviceInfoRecord.port());
        var usbIcon = SvgFactory.createUsbIconFromSvg();
        usbIcon.addClassName(Left.SMALL);
        spanPort.add(usbIcon);
        //Filter the Silicon Labs CP210x UART Bridge, QinHeng Electronics CH340 serial converter
        //Silicon Labs CP210x USB to UART Bridge windows
        //Serial Port FreeBSD
        String descriptivePortName = espDeviceInfoRecord.descriptivePortName();
        if(descriptivePortName.contains("CP21")) {
            int index = descriptivePortName.toUpperCase().indexOf("CP");
            int lastIndexOf = descriptivePortName.lastIndexOf("x");
            String tempDescriptiveName = descriptivePortName.substring(index, lastIndexOf +1);
            if(GetOsName.getOsName() == GetOsName.WINDOWS) {
                spanFriendlyName.setText(tempDescriptiveName);
            } else {
                spanFriendlyName.setText(descriptivePortName.split(" ")[0]);
            }
        }

        if(descriptivePortName.contains("Serial Port")) {
            if(GetOsName.getOsName() == GetOsName.FREEBSD) {
                spanFriendlyName.setText(descriptivePortName);
            }
        }

        if(descriptivePortName.startsWith("USB Serial")
                || descriptivePortName.startsWith("USB-SERIAL")
                || descriptivePortName.startsWith("USB2.0-Serial")) {
            spanFriendlyName.setText("CH3xx serial");
        }

        divLeftContentInside2.add(spanState, spanPort, spanFriendlyName);
        divLeftContentInside2.addClassName("div-left-content-inside-title2");
        return divLeftContentInside2;
    }

    private Div createDivRightParent() {
        divRightParent.addClassName("div-right-parent");
        divRightParent.add(createDivRightContent());
        return divRightParent;
    }

    private Div createDivRightContent() {
        divRightContent.addClassName("div-right-content");
        divRightContent.add(createDivRightSubHeader(), createDivRightContentText());
        return divRightContent;
    }

    private Div createDivRightSubHeader() {
        final Span spanOverView = new Span("Overview");
        divRightSubHeader.add(spanOverView);
        divRightSubHeader.addClassName("div-right-subheader");
        return divRightSubHeader;
    }

    /**
     * Handle the length of the span value content and if it exceeds 200 put ellipsis and copy button
     *
     * @return
     */
    private Div createDivRightContentText() {
        divRightContentText.addClassName("div-right-content-text");
        Stream.of(chipType, detectedFlashSize, crystal, chipIs, spanMadAddress).forEach(span -> span.getStyle().set("font-weight", "bold"));
        chipTypeValue.setText(":  " + espDeviceInfoRecord.chipType());
        chipIsValue.setText(":  " + espDeviceInfoRecord.chipIs());

        detectedFlashSizeValue.setText(":  " + espDeviceInfoRecord.detectedFlashSize());
        spanMadAddressValue.setText(": " + espDeviceInfoRecord.macAddress());
        crystalValue.setText(":  " + espDeviceInfoRecord.crystalIs());

        /*
         * Detecting chip type
         */
        var copyChipType = createDivWithCopyButton(chipType, chipTypeValue, espDeviceInfoRecord.chipType(), "Copy chip type");

        /*
         * Chip is, // if you exceed a length of 13, it shows 12 characters more ... with the copy button
         **/
        Div divChipIsAndValue = null;
        if (chipIsValue.getText().length() > 13) {
            final String newString = espDeviceInfoRecord.chipIs().substring(0, 12).trim().concat("...");
            chipIsValue.setText("");
            chipIsValue.setText(": " + newString);
            chipIsValue.addClassName(Right.SMALL);
            divChipIsAndValue = createDivWithCopyButton(chipIs, chipIsValue, espDeviceInfoRecord.chipIs(), "Copy chip is");
        } else {
            divChipIsAndValue = createDivWithCopyButton(chipIs, chipIsValue, espDeviceInfoRecord.chipIs(), "Copy chip is");
        }

        /*
         * Flash size
         *
         * */
        var copyFlashSize = createDivWithCopyButton(detectedFlashSize, detectedFlashSizeValue, espDeviceInfoRecord.detectedFlashSize(), "Copy flash size");

        /*
         * Crystal
         *
         * */
        var copyCrystal = createDivWithCopyButton(crystal, crystalValue, espDeviceInfoRecord.crystalIs(), "Copy crystal");

        /*
         *  MAC
         *
         * */
        var copyMac = createDivWithCopyButton(spanMadAddress, spanMadAddressValue, espDeviceInfoRecord.macAddress(), "Copy MAC");

        divRightContentText.add(copyChipType, hr1, divChipIsAndValue, hr2, copyFlashSize, hr3, copyCrystal, hr4, copyMac, hr5);

        return divRightContentText;
    }

    private Div createDivWithCopyButton(Span spanText, Span spanValue, String value, String copyName) {
        var copyButton = SvgFactory.createCopyButtonFromSvg();
        final Button button = new Button(copyButton);
        button.addClassName(BOX_SHADOW_VAADIN_BUTTON);
        button.addClickListener(event -> {
            Notification.show("Copied " + spanText.getText(), 2500, Position.MIDDLE);
            button.setIcon(VaadinIcon.CHECK.create());
            Mono.just(button)
                    .delayElement(Duration.ofMillis(1500))
                    .subscribe(btn -> {
                        btn.getUI().ifPresent(ui -> ui.access(() -> {
                            btn.setIcon(copyButton);
                        }));
                    });
        });
        button.setTooltipText(copyName);
        final ClipboardHelper clipboardHelper = new ClipboardHelper(value.trim(), button);
        Tooltip.forComponent(spanValue).setText(value.trim());
        final Div div = new Div(spanText, spanValue, clipboardHelper);
        spanValue.addClassName(Right.SMALL);
        div.addClassNames(Display.FLEX, FlexDirection.ROW, JustifyContent.START, AlignItems.CENTER, Right.SMALL);
        return div;
    }

}
