package com.esp.espflow.views.readflash;

import com.esp.espflow.data.entity.EspDeviceInfo;
import com.esp.espflow.data.util.downloader.FlashButtonWrapper;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.FlexDirection;
import com.vaadin.flow.theme.lumo.LumoUtility.JustifyContent;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin.Right;
import lombok.Getter;
import org.vaadin.olli.ClipboardHelper;

import java.util.Objects;
import java.util.stream.Stream;

import static com.esp.espflow.data.util.UiToolConstants.BOX_SHADOW_VAADIN_BUTTON;
import static com.esp.espflow.data.util.UiToolConstants.CHIP_IS;
import static com.esp.espflow.data.util.UiToolConstants.CHIP_TYPE;
import static com.esp.espflow.data.util.UiToolConstants.CRYSTAL_IS;
import static com.esp.espflow.data.util.UiToolConstants.FLASH_SIZE;
import static com.esp.espflow.data.util.UiToolConstants.MAC;

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
    private final Span spanState = new Span("STATE UP");

    private final Span chipType = new Span(CHIP_TYPE);
    private final Span chipTypeValue = new Span();
    private final Hr hr1 = new Hr();

    private final Span flashSize = new Span(FLASH_SIZE);
    private final Span flashSizeValue = new Span();
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
    private FlashButtonWrapper flashButtonWrapper;

    private String image;
    private EspDeviceInfo espDeviceInfo;

    /**
     * Create a DeviceCardLayout div
     *
     *
     * @param image
     * @param espDeviceInfo
     * @param downloadFlashButton
     * @param flashButtonWrapper
     *
     * @return A {@link DeviceCardLayout}
     */
    public static DeviceCardLayout of(final String image, final EspDeviceInfo espDeviceInfo,
                            final Button downloadFlashButton,
                            final FlashButtonWrapper flashButtonWrapper) {
       return new DeviceCardLayout(image, espDeviceInfo, downloadFlashButton, flashButtonWrapper);
    }

    private DeviceCardLayout(final String image, final EspDeviceInfo espDeviceInfo,
                            final Button downloadFlashButton,
                            final FlashButtonWrapper flashButtonWrapper) {
        super.addClassName("card-container");
        this.image = image;
        this.espDeviceInfo = espDeviceInfo;
        this.downloadFlashButton = downloadFlashButton;
        this.flashButtonWrapper = flashButtonWrapper;

        final Div toolbar = this.createDivToolbarRow();
        final Div slideOverView = this.createDivSlideOverview();

        super.add(toolbar, slideOverView);
    }

    public Div createDivToolbarRow() {
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
        if (Objects.nonNull(flashButtonWrapper)) {
            divControls.add(flashButtonWrapper);
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
        spanDeviceName.setText(espDeviceInfo.chipType());
        divLeftContentInside1.add(spanDeviceName);
        divLeftContentInside1.addClassName("div-left-content-inside-title1");
        return divLeftContentInside1;
    }

    public Div createDivLeftContentInside2() {
        var icon = VaadinIcon.CHECK_CIRCLE.create();
        icon.getStyle().set("color","blue");
        spanState.add(icon);
        divLeftContentInside2.add(spanState);
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
     * Manejar largo del contenido del span value y si pasa 200 meter elipsis y el copy  button
     * @return
     */
    public Div createDivRightContentText() {
        divRightContentText.addClassName("div-right-content-text");
        Stream.of(chipType, flashSize, crystal, chipIs, spanMadAddress).forEach(span -> span.getStyle().set("font-weight", "bold"));
        chipTypeValue.setText(":  " + espDeviceInfo.chipType());
        chipIsValue.setText(":  " + espDeviceInfo.chipIs());

        flashSizeValue.setText(":  " + espDeviceInfo.detectedFlashSize());
        spanMadAddressValue.setText(": " + espDeviceInfo.macAddress());
        crystalValue.setText(":  " + espDeviceInfo.crystalIs());

        /*
         * Detecting chip type
         */
        var copyChipType = createDivWithCopyButton(chipType, chipTypeValue, espDeviceInfo.chipType(), "Copy chip type");

        /*
        * Chip is, // if you exceed a length of 13, it shows 12 characters more ... with the copy button
        **/
        Div divChipIsAndValue = null;
        if(chipIsValue.getText().length() > 13) {
            final String newString = espDeviceInfo.chipIs().substring(0, 12).trim().concat("...");
            chipIsValue.setText("");
            chipIsValue.setText(": "+newString);
            chipIsValue.addClassName(Right.SMALL);
            divChipIsAndValue = createDivWithCopyButton(chipIs, chipIsValue,  espDeviceInfo.chipIs(), "Copy chip is");
            divChipIsAndValue.getElement().setAttribute("title", espDeviceInfo.chipIs());
        } else {
            divChipIsAndValue = createDivWithCopyButton(chipIs, chipIsValue, espDeviceInfo.chipIs(), "Copy chip is");
            divChipIsAndValue.getElement().setAttribute("title", espDeviceInfo.chipIs());
        }

        /*
         * Flash size
         *
         * */
        var copyFlashSize = createDivWithCopyButton(flashSize, flashSizeValue, espDeviceInfo.detectedFlashSize(), "Copy flash size");

        /*
         * Crystal
         *
         * */
        var copyCrystal = createDivWithCopyButton(crystal, crystalValue, espDeviceInfo.crystalIs(), "Copy crystal");

        /*
         *  MAC
         *
         * */
        var copyMac = createDivWithCopyButton(spanMadAddress, spanMadAddressValue, espDeviceInfo.macAddress(), "Copy MAC");

        divRightContentText.add(copyChipType, hr1, divChipIsAndValue, hr2, copyFlashSize, hr3, copyCrystal, hr4, copyMac,  hr5);

        return divRightContentText;
    }


    private Div createDivWithCopyButton(Span spanText, Span spanValue, String value, String copyName) {
        final Button button = new Button(VaadinIcon.COPY_O.create());
        button.addClassName(BOX_SHADOW_VAADIN_BUTTON);
        button.setTooltipText(copyName);
        final ClipboardHelper clipboardHelper = new ClipboardHelper(value.trim(), button);
        spanValue.addDoubleClickListener(event -> {
            Notification.show("Largo " + spanValue.getText().length());
            Notification.show("Value " + spanValue.getText());
        });
        final Div div = new Div(spanText, spanValue, clipboardHelper);
        spanValue.addClassName(Right.SMALL);
        div.addClassNames(Display.FLEX, FlexDirection.ROW, JustifyContent.START, AlignItems.CENTER, Right.SMALL);
        return div;
    }

}