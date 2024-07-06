package com.nodemcuui.tool.views.readflash;

import com.flowingcode.vaadin.addons.carousel.Carousel;
import com.flowingcode.vaadin.addons.carousel.Slide;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import lombok.Builder;

@Uses(Carousel.class)
public class EspDevicesCarousel extends Div {

    public EspDevicesCarousel() {
        Slide slide1 = new Slide(createSlideContent(
                "Slide 1",
                "https://www.electronicwings.com/storage/PlatformSection/TopicContent/308/description/esp8266%20module.jpg"));
        Slide s2 =
                new Slide(createSlideContent(
                        "Slide 2",
                        "https://2.bp.blogspot.com/-nvtIfgN8duc/XKUQh9VEyFI/AAAAAAAABT8/mE7P45E2uqwWlkKimAmes7fT2rdW9UDWwCEwYBhgL/s320/anniversary_1.jpg"));
        Slide s3 =
                new Slide(createSlideContent(
                        "Slide 3",
                        "https://www.flowingcode.com/wp-content/uploads/2020/04/photo4blog-300x300.jpg"));
        Slide s4 =
                new Slide(createSlideContent(
                        "Slide 4",
                        "https://www.flowingcode.com/wp-content/uploads/2021/03/happy_birthday_2.jpg"));

        Carousel carousel = new Carousel(slide1, s2, s3, s4);
        carousel.setSizeFull();
        carousel.setThemeName("custom-theme");
        carousel.addChangeListener(e -> Notification.show("Slide Changed!", 1000, Position.BOTTOM_START));

        super.add(carousel);
        super.setWidth("55%");
        super.setHeight("85%");

    }


    public static Div createSlideContent(String string, String image ) {
        final MatCard parent = new MatCard(image);

        Span spanName = new Span("Name: esp8266");
        Hr hr1 = new Hr();
        Span spanChipId = new Span("EspDeviceInfo: ESP8266EX ");
        Hr hr2 = new Hr();
        Span hex = new Span("Hex: 0x400000");
        Hr hr3 = new Hr();

        parent.createDivRightContent().add(spanName, hr1, spanChipId, hr2, hex, hr3);

        return parent;
    }

    private EspDeviceInfo getEspDeviceInfo() {
        return EspDeviceInfo.builder()
                .name("esp8266")
                .chipId("ESP8266EX")
                .hex("0x400000")
                .decimal("4194304")
                .flashSize("4MB")
                .build();
    }

    @Builder
    record EspDeviceInfo(
            String name,
            String chipId,
            String macAddress,
            String decimal,
            String hex,
            String flashSize) {

    }


}