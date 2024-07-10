package com.nodemcuui.tool.views.readflash;

import com.flowingcode.vaadin.addons.carousel.Carousel;
import com.flowingcode.vaadin.addons.carousel.Slide;
import com.nodemcuui.tool.data.entity.EspDeviceInfo;
import com.nodemcuui.tool.data.util.downloader.DownloadFlashButton;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Use a StepBuilder
 */
@Uses(Carousel.class)
public class EspDevicesCarousel extends Div {

    private final List<Slide> slideList = new CopyOnWriteArrayList<>();

    public void addSlide(Slide slide) {
        this.slideList.add(slide);
    }

    /**
     * Dependiendo de lo que se lea por consola se crea una slide en base a esa lectura
     */
    public void createSlides() {

        Carousel carousel = new Carousel(slideList.toArray(Slide[]::new));
        carousel.setSizeFull();
        carousel.setThemeName("custom-theme");
        carousel.addChangeListener(e -> Notification.show("Slide Changed!", 1000, Position.BOTTOM_START));

        super.add(carousel);
        super.setWidth("450px");
        super.setHeight("100%");
        getStyle().set("border-radius", "6px");
        //getStyle().set("box-shadow", "0 2px 1px -1px rgba(0, 0, 0, .2), 0 1px 1px 0 rgba(0, 0, 0, .14), 0 1px 3px 0 rgba(0, 0, 0, .12)");
        getStyle().set("box-shadow", "var(--lumo-box-shadow-s)");
    }

    public static DeviceCardLayout createSlideContent(String image, EspDeviceInfo espDeviceInfo, final DownloadFlashButton downFlashButton) {
        return DeviceFactoryCardLayout.createDeviceCard(image, espDeviceInfo, downFlashButton);
    }

}