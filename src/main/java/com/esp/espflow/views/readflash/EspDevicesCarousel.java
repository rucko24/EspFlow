package com.esp.espflow.views.readflash;

import com.esp.espflow.data.entity.EspDeviceInfo;
import com.esp.espflow.data.util.downloader.FlashButtonWrapper;
import com.flowingcode.vaadin.addons.carousel.Carousel;
import com.flowingcode.vaadin.addons.carousel.Slide;
import com.infraleap.animatecss.Animated;
import com.infraleap.animatecss.Animated.Animation;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.FlexDirection;
import com.vaadin.flow.theme.lumo.LumoUtility.Height;
import com.vaadin.flow.theme.lumo.LumoUtility.JustifyContent;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

/**
 * @author rubn
 */
@Uses(Carousel.class)
@CssImport(value = "./carousel-demo-styles.css", themeFor = "fc-l2t-paper-slider")
public class EspDevicesCarousel extends Div {

    private final List<Slide> slideList = new CopyOnWriteArrayList<>();
    private final ProgressBar progressBar;
    private final String title;
    final Div divCenter = new Div();

    public EspDevicesCarousel(final ProgressBar progressBar, final String title) {
        this.progressBar = progressBar;
        this.title = title;
        super.setWidth("500px");
        super.setHeight("100%");
        super.getStyle().set("border-radius", "6px");
        super.getStyle().set("box-shadow", "var(--lumo-box-shadow-s)");
        this.initialCenterLogo();
        Animated.animate(this, Animation.FADE_IN);
    }

    /**
     * A logo for connected devices
     * <p>
     * Allows you to hide the ProgressBar
     *
     */
    private void initialCenterLogo() {
        final Icon icon = VaadinIcon.LINK.create();
        final H2 h2 = new H2(title);
        divCenter.add(icon, h2, progressBar);
        //Set visibility
        Stream.of(icon, h2, divCenter).forEach(component -> component.setVisible(true));
        divCenter.addClassNames(Display.FLEX, FlexDirection.COLUMN,
                Height.FULL,
                AlignItems.CENTER,
                JustifyContent.CENTER);
        this.progressBar.setWidth("50%");
        this.progressBar.setIndeterminate(title.contains("Loading..."));
        this.progressBar.setVisible(title.contains("Loading..."));
        super.add(divCenter);
    }

    /**
     * We add the read devices to this slide
     *
     * @param slide
     */
    public void addSlide(Slide slide) {
        this.slideList.add(slide);
    }

    /**
     * It is invoked inside a listener of the button that is created with each console reading.
     */
    public void createSlides() {
        //remove the previous div
        remove(divCenter);
        final Carousel carousel = new Carousel(slideList.toArray(Slide[]::new));
        carousel.setSizeFull();
        carousel.setThemeName("custom-theme");
        Animated.animate(carousel, Animation.FADE_IN);
        super.add(carousel);
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