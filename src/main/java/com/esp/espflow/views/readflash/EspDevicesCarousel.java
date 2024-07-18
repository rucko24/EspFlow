package com.esp.espflow.views.readflash;

import com.esp.espflow.data.entity.EspDeviceInfo;
import com.esp.espflow.data.util.downloader.FlashButtonWrapper;
import com.flowingcode.vaadin.addons.carousel.Carousel;
import com.flowingcode.vaadin.addons.carousel.Slide;
import com.infraleap.animatecss.Animated;
import com.infraleap.animatecss.Animated.Animation;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.progressbar.ProgressBar;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author rubn
 */
@Uses(Carousel.class)
@CssImport(value = "./carousel-demo-styles.css", themeFor = "fc-l2t-paper-slider")
public class EspDevicesCarousel extends Div {

    private final List<Slide> slideList = new CopyOnWriteArrayList<>();
    private final ProgressBar progressBar;

    public EspDevicesCarousel(final ProgressBar progressBar) {
        this.progressBar = progressBar;
        super.setWidth("500px");
        super.setHeight("100%");
        super.getStyle().set("border-radius", "6px");
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


    /**
     * Create a DeviceCardLayout div
     *
     * @param image
     * @param espDeviceInfo
     * @param downFlashButton
     * @param flashButtonWrapper
     *
     * @return A {@link DeviceCardLayout}
     */
    public static DeviceCardLayout createSlideContent(String image, EspDeviceInfo espDeviceInfo, final Button downFlashButton, FlashButtonWrapper flashButtonWrapper) {
        return DeviceCardLayout.of(image, espDeviceInfo, downFlashButton, flashButtonWrapper);
    }

}