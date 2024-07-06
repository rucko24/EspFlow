package com.nodemcuui.tool.views.readflash;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;

/**
 * The parent div ?? lmao
 */
public class MatCard extends Div {

    private final Div divRowWrap = new Div();
    private final Div divDeviceInfo = new Div();
    private final Div divDoubleHeight = new Div();
    private final Div divCarContainerFront = new Div();
    private final Div divMatCard = new Div();
    private final Div divMatCardContent = new Div();
    private final Div divMatToolbarRow = new Div();
    private final Div divControls = new Div();
    private final Span spanEspDeviceTitle = new Span("Device");
    private final Div divCarousel = new Div();
    private final Div divSlideOverView = new Div();
    private final Div divLeft = new Div();
    private final Div divOverlay = new Div();
    private final Div divLeftContent = new Div();
    private final Div divLeftContentInsideTitle1 = new Div();
    private final Div divLeftContentInsideTitle2 = new Div();
    private final Div divRight = new Div();
    private final Div divParentInsideRight = new Div();
    private final Div divSubHeaderDivRight = new Div();
    private final Div divRightContent = new Div();

    private final String image;

    public MatCard(final String image) {
        this.image = image;

        super.setSizeFull();
        this.createCard();
    }

    private void createCard() {
        super.add(createDivRowWrap());
    }

    public Div createDivRowWrap() {
        divRowWrap.addClassName("div-row-wrap");
        divRowWrap.add(createDivDeviceInfo());

        return divRowWrap;
    }

    public Div createDivDeviceInfo() {
        divDeviceInfo.addClassName("div-device-info");
        divDeviceInfo.add(createDivDoubleHeight());
        return divDeviceInfo;
    }

    private Div createDivDoubleHeight() {
        divDoubleHeight.addClassName("div-double-height");
        divDoubleHeight.add(createDivCarContainerFront());
        return divDoubleHeight;
    }


    private Div createDivCarContainerFront() {
        divCarContainerFront.addClassName("card-container-front");
        divCarContainerFront.add(createMatCard());
        return divCarContainerFront;
    }

    private Div createMatCard() {
        divMatCard.addClassName("mat-card");
        divMatCard.add(createMatCardContent());
        return divMatCard;
    }

    private Div createMatCardContent() {
        divMatCardContent.addClassName("mat-card-content");
        divMatCardContent.add(createMatToolbarRow());
        divMatCardContent.add(createDivCarousel());
        return divMatCardContent;
    }

    private Div createMatToolbarRow() {
        divMatToolbarRow.addClassName("mat-toolbar-row");
        divMatToolbarRow.add(createSpanEspDeviceTitle());
        divMatToolbarRow.add(createDivControls());
        return divMatToolbarRow;
    }

    private Span createSpanEspDeviceTitle() {
        spanEspDeviceTitle.addClassName("span-esp-device-title");
        return spanEspDeviceTitle;
    }

    private Div createDivControls() {
        divControls.addClassName("div-controls");

        Span spanCod = new Span();
        spanCod.add(VaadinIcon.COG.create());

        Span span = new Span();
        spanCod.add(VaadinIcon.CHART_TIMELINE.create());

        divControls.add(spanCod, span);

        return divControls;
    }

    private Div createDivCarousel() {
        divCarousel.addClassName("div-carousel");
        divCarousel.add(this.createDivSlideOverView());
        return divCarousel;
    }

    private Div createDivSlideOverView() {
        divSlideOverView.addClassName("div-slide-overview");
        divSlideOverView.add(createDivLeft());
        divSlideOverView.add(createDivRight());
        return divSlideOverView;
    }

    /**
     * Contendra la imagen de los micros
     *
     * @return Div
     */
    private Div createDivLeft() {
        divLeft.addClassName("div-left");
        divLeft.getStyle().set("background-image", "url('" + image + "')");
        divLeft.add(this.createDivOverlay());
        return divLeft;
    }

    private Div createDivOverlay() {
        divOverlay.addClassName("div-overlay");
        divOverlay.add(this.createDivLeftContent());
        return divOverlay;
    }

    private Div createDivRight() {
        divRight.addClassName("div-right");
        divRight.add(getDivParentInsideRight());
        return divRight;
    }

    /**
     * Posiblemente añadir contenido aqui sel carousel del adddon
     *
     * @return DIV
     */
    public Div createDivLeftContent() {
        divLeftContent.addClassName("div-left-content");
        divLeftContent.add(createDivLeftContentInsideTitle1());
        divLeftContent.add(createDivLeftContentInsideTitle2());
        return divLeftContent;
    }

    public Div createDivLeftContentInsideTitle1() {
        final Span spanEsp8266 = new Span("Esp8266");
        divLeftContentInsideTitle1.addClassName("div-left-content-inside-title1");
        divLeftContentInsideTitle1.add(spanEsp8266);
        return divLeftContentInsideTitle1;
    }

    public Div createDivLeftContentInsideTitle2() {
        final Span spanEsp8266Status = new Span("status");
        divLeftContentInsideTitle2.addClassName("div-left-content-inside-title2");
        divLeftContentInsideTitle2.add(spanEsp8266Status);
        return divLeftContentInsideTitle2;
    }

    public Div getDivParentInsideRight() {
        divParentInsideRight.addClassName("div-for-parent-div-right");
        divParentInsideRight.add(createDivSubHeaderDivRight());
        divParentInsideRight.add(createDivRightContent());
        return divParentInsideRight;
    }

    public Div createDivSubHeaderDivRight() {
        divSubHeaderDivRight.addClassName("div-subheader-right");
        Span overview = new Span("Overview");
        divSubHeaderDivRight.add(overview);
        return divSubHeaderDivRight;
    }

    public Div createDivRightContent() {
        divRightContent.addClassName("div-right-content");

        return divRightContent;
    }

}
