package com.nodemcuui.tool.views.readflash;

import com.nodemcuui.tool.data.entity.EspDeviceInfo;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import lombok.Getter;

import java.util.stream.Stream;

import static com.nodemcuui.tool.data.util.UiToolConstants.CHIP_TYPE;
import static com.nodemcuui.tool.data.util.UiToolConstants.CRYSTAL_IS;
import static com.nodemcuui.tool.data.util.UiToolConstants.FLASH_SIZE;

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

    private final Span chipType = new Span(new Html(CHIP_TYPE));
    private Span chipTypeValue = new Span();
    private final Hr hr1 = new Hr();

    private final Span flashSize = new Span(FLASH_SIZE);
    private final Span flashSizeValue = new Span();
    private final Hr hr2 = new Hr();

    private final Span crystal = new Span(CRYSTAL_IS);
    private final Span crystalValue = new Span();
    private final Hr hr3 = new Hr();

    private final Button downloadFlashButton = new Button("Download flash", VaadinIcon.DOWNLOAD.create());

    private String image;
    private EspDeviceInfo espDeviceInfo;

    public DeviceCardLayout(final String image) {
        super.addClassName("card-container");
        this.image = image;

        final Div toolbar = this.createDivToolbarRow();
        final Div slideOverView = this.createDivSlideOverview();

        super.add(toolbar, slideOverView);
    }

    public DeviceCardLayout(final String image, final Span chipTypeValue) {
        super.addClassName("card-container");
        this.image = image;
        this.chipTypeValue = chipTypeValue;

        final Div toolbar = this.createDivToolbarRow();
        final Div slideOverView = this.createDivSlideOverview();

        super.add(toolbar, slideOverView);
    }

    public DeviceCardLayout(final String image, final EspDeviceInfo espDeviceInfo) {
        super.addClassName("card-container");
        this.image = image;
        this.espDeviceInfo = espDeviceInfo;

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

        downloadFlashButton.getStyle().set("box-shadow", "0 2px 1px -1px rgba(0, 0, 0, .2), 0 1px 1px 0 rgba(0, 0, 0, .14), 0 1px 3px 0 rgba(0, 0, 0, .12)");
        downloadFlashButton.setTooltipText("Download flash");
        divControls.add(downloadFlashButton);

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
        spanState.add(VaadinIcon.CHECK_CIRCLE.create());
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

    public Div createDivRightContentText() {
        divRightContentText.addClassName("div-right-content-text");
        Stream.of(chipType, flashSize, crystal).forEach(span -> span.getStyle().set("font-weight","bold"));
        chipTypeValue.setText(":  " + espDeviceInfo.chipType());
        flashSizeValue.setText(":  " + espDeviceInfo.detectedFlashSize());
        crystalValue.setText(":  " + espDeviceInfo.crystalIs());
        divRightContentText.add(chipType, chipTypeValue, hr1, flashSize, flashSizeValue, hr2, crystal, crystalValue, hr3);
        return divRightContentText;
    }

}
