package com.esp.espflow.views.readflash;

import com.esp.espflow.entity.EspDeviceInfoRecord;
import com.esp.espflow.service.downloader.FlashDownloadButtonWrapper;
import com.esp.espflow.util.svgfactory.SvgFactory;
import com.flowingcode.vaadin.addons.carousel.Carousel;
import com.flowingcode.vaadin.addons.carousel.Slide;
import com.infraleap.animatecss.Animated;
import com.infraleap.animatecss.Animated.Animation;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.SvgIcon;
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
 * <p>This carousel div in its first instance will be shown without units, and invisible by default, after the user refreshes
 * from the view we will show the devices scanned from the serial port, if the reactive stream ends without error we make it visible in the method {@link ReadFlashView#onComplete(List, EspDevicesCarousel)}
 * </p>
 *
 * <p>
 * In case the units are not scanned we will display a notification to the user and a default
 * message with the following text <strong>No devices shown!</strong>
 * </p>
 *
 * @author rubn
 */
@Getter
@Uses(Carousel.class)
@CssImport(value = "./styles/carousel-demo-styles.css", themeFor = "fc-l2t-paper-slider")
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
        super.addClassName("carousel-border-dark");
        this.initialCenterLogo();
        Animated.animate(this, Animation.FADE_IN);
    }

    /**
     * <p>We show by default the link icon, with the progress bar invisible.</p>
     *
     * <p>The progressBar will be displayed as undetermined in case the text is with <strong>Loading...</strong></p>
     */
    private void initialCenterLogo() {
        final SvgIcon icon = SvgFactory.createIconFromSvg("broken-link.svg", "50px", null);
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
     * We show Loading when starting to search for devices {@link ReadFlashView#showDetectedDevices(UI, EspDevicesCarousel)}
     *
     * @param title   LOADING...
     * @param visible to make visible the progressbar and h2
     */
    public void showLoading(String title, boolean visible) {
        this.h2.setText(title);
        this.progressBar.setIndeterminate(visible);
        this.progressBar.setVisible(visible);
    }

    /**
     * We add the read devices to this slide
     *
     * @param slide
     */
    public void addSlide(Slide slide) {
        slide.getStyle().set("scrollbar-width","thin");
        this.slideList.add(slide);
    }

    /**
     * It is invoked inside a listener of the button that is created with each console reading.
     */
    public void createSlidesAndShow() {
        //we remove the content in the previous div
        this.divCenter.removeAll();
        final Carousel carousel = new Carousel(slideList.toArray(Slide[]::new));
        carousel.setSizeFull();
        carousel.setThemeName("custom-theme");
        Animated.animate(carousel, Animation.FADE_IN);
        this.divCenter.add(carousel);
        super.add(divCenter);
        super.setVisible(true);
    }

    /**
     * Create a DeviceCardLayout div
     *
     * @param image
     * @param espDeviceInfoRecord
     * @param downFlashButton
     * @param flashDownloadButtonWrapper
     * @return A {@link DeviceCardLayout}
     */
    public static DeviceCardLayout createSlideContent(String image, EspDeviceInfoRecord espDeviceInfoRecord, final Button downFlashButton,
                                                      FlashDownloadButtonWrapper flashDownloadButtonWrapper) {
        return DeviceCardLayout.of(image, espDeviceInfoRecord, downFlashButton, flashDownloadButtonWrapper);
    }

}