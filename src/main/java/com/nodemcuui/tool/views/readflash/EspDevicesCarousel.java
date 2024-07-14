package com.nodemcuui.tool.views.readflash;

import com.flowingcode.vaadin.addons.carousel.Carousel;
import com.flowingcode.vaadin.addons.carousel.Slide;
import com.infraleap.animatecss.Animated;
import com.infraleap.animatecss.Animated.Animation;
import com.nodemcuui.tool.data.entity.EspDeviceInfo;
import com.nodemcuui.tool.data.util.downloader.FlashButtonWrapper;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.progressbar.ProgressBar;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author rubn
 */
@Uses(Carousel.class)
public class EspDevicesCarousel extends Div {

    private final List<Slide> slideList = new CopyOnWriteArrayList<>();
    private final ProgressBar progressBar;

    public EspDevicesCarousel(final ProgressBar progressBar) {
        this.progressBar = progressBar;
        super.setWidth("450px");
        super.setHeight("100%");
        super.getStyle().set("border-radius", "6px");
        //getStyle().set("box-shadow", "0 2px 1px -1px rgba(0, 0, 0, .2), 0 1px 1px 0 rgba(0, 0, 0, .14), 0 1px 3px 0 rgba(0, 0, 0, .12)");
        super.getStyle().set("box-shadow", "var(--lumo-box-shadow-s)");
        updateProgressBar(true);
        Animated.animate(this, Animation.FADE_IN);
    }

    private void updateProgressBar(final Boolean value) {
        super.add(progressBar);
        this.progressBar.setIndeterminate(value);
        this.progressBar.setVisible(value);
    }

    /**
     * @param slide
     */
    public void addSlide(Slide slide) {
        this.slideList.add(slide);
    }

    /**
     * It is invoked inside a listener of the button that is created with each console reading.
     */
    public void createSlides() {

        Carousel carousel = new Carousel(slideList.toArray(Slide[]::new));
        carousel.setSizeFull();
        carousel.setThemeName("custom-theme");
        super.add(carousel);
        updateProgressBar(false);
    }

    public static DeviceCardLayout createSlideContent(String image, EspDeviceInfo espDeviceInfo, final Button downFlashButton, FlashButtonWrapper anchor) {
        return DeviceFactoryCardLayout.createDeviceCard(image, espDeviceInfo, downFlashButton, anchor);
    }

}