package com.esp.espflow.views.readflash;

import com.esp.espflow.entity.EspDeviceInfo;
import com.esp.espflow.util.downloader.FlashButtonWrapper;
import com.flowingcode.vaadin.addons.carousel.Carousel;
import com.flowingcode.vaadin.addons.carousel.Slide;
import com.infraleap.animatecss.Animated;
import com.infraleap.animatecss.Animated.Animation;
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
import lombok.Getter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

import static com.esp.espflow.util.EspFlowConstants.LOADING;

/**
 *
 * <p>This carousel div in its first instance will be shown without units, and invisible by default, after the user refreshes
 * from the view we will show the devices scanned from the serial port, if the reactive stream ends without error we make it visible in the method {@link ReadFlashView#onComplete(List, EspDevicesCarousel)}
 * </p>
 *
 * <p>
 *  In case the units are not scanned we will display a notification to the user and a default
 *   message with the following text <strong>No devices shown!</strong>
 * </p>
 *
 *
 * @author rubn
 */
@Getter
@Uses(Carousel.class)
@CssImport(value = "./carousel-demo-styles.css", themeFor = "fc-l2t-paper-slider")
public class EspDevicesCarousel extends Div {

    private final List<Slide> slideList = new CopyOnWriteArrayList<>();
    private final ProgressBar progressBar;
    private final String titleForH2;
    private final H2 h2 = new H2();
    private final Div divCenter = new Div();

    public EspDevicesCarousel(final ProgressBar progressBar, final String titleForH2) {
        this.progressBar = progressBar;
        this.titleForH2 = titleForH2;
        super.setWidth("500px");
        super.setHeight("100%");
        super.getStyle().set("border-radius", "6px");
        super.getStyle().set("box-shadow", "var(--lumo-box-shadow-s)");
        this.initialCenterLogo();
        Animated.animate(this, Animation.FADE_IN);
    }

    /**
     * <p>We show by default the link icon, with the progress bar invisible.</p>
     *
     * <p>The progressBar will be displayed as undetermined in case the text is with <strong>Loading...</strong></p>
     *
     */
    private void initialCenterLogo() {
        final Icon icon = VaadinIcon.LINK.create();
        this.h2.setText(titleForH2);
        divCenter.add(icon, h2, progressBar);
        //Set visibility
        Stream.of(icon, h2, divCenter).forEach(component -> component.setVisible(true));
        divCenter.addClassNames(Display.FLEX, FlexDirection.COLUMN,
                Height.FULL,
                AlignItems.CENTER,
                JustifyContent.CENTER);
        this.progressBar.setWidth("50%");
        this.progressBar.setIndeterminate(titleForH2.contains(LOADING));
        this.progressBar.setVisible(titleForH2.contains(LOADING));
        super.add(divCenter);
    }

    /**
     * We will update it when the device search is completed. {@link ReadFlashView#onComplete(List, EspDevicesCarousel)}
     *
     * @param title
     */
    public void hiddenProgressBarAndUpdatedTitleForH2(String title) {
        this.h2.setText(title);
        this.progressBar.setIndeterminate(false);
        this.progressBar.setVisible(false);
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