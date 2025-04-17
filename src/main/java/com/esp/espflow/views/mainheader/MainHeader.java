package com.esp.espflow.views.mainheader;

import com.esp.espflow.enums.RefreshDevicesEvent;
import com.esp.espflow.views.readflash.ReadFlashView;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.stream.Stream;

import static com.esp.espflow.util.EspFlowConstants.CONFIGURE;
import static com.esp.espflow.util.EspFlowConstants.RETURN_WINDOW_INNER_WIDTH;

/**
 * @author rubn
 */
@Getter
@Log4j2
@SpringComponent
@UIScope
@RequiredArgsConstructor
public class MainHeader extends HorizontalLayout implements BeforeEnterObserver {

    public static final String READ_FLASH = "read-flash";
    public static final String REFRESH_DEVICES = "Refresh devices";

    private final Button configureIcon = new Button(LineAwesomeIcon.SLIDERS_H_SOLID.create());
    private final Button buttonRefreshDevicesIcon = new Button(VaadinIcon.REFRESH.create());
    private final H1 viewTitle = new H1();
    private final HorizontalLayout rowHeaderForComponentsView = new HorizontalLayout();
    /**
     * Services
     */
    private final ApplicationEventPublisher applicationEventPublisher;
    private final NotificationBell notificationBell;

    @PostConstruct
    public void setup() {
        //Do nothing at the moment!
    }

    /**
     * @return A {@link HorizontalLayout}
     */
    public MainHeader createHeaderRow() {
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);


        this.configureIcon.setTooltipText(CONFIGURE);
        this.configureIcon.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        this.buttonRefreshDevicesIcon.setTooltipText(CONFIGURE);
        this.buttonRefreshDevicesIcon.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        //Added listeners here, only once
        //Envio de scan
        this.buttonRefreshDevicesIcon.addClickListener(event -> this.applicationEventPublisher.publishEvent(RefreshDevicesEvent.SCAN));
        this.configureIcon.addClickListener(event -> this.applicationEventPublisher.publishEvent(RefreshDevicesEvent.OPEN_SIDEBAR));

        this.buttonRefreshDevicesIcon.setVisible(false);

        this.rowHeaderForComponentsView.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        final HorizontalLayout rowEnd = new HorizontalLayout();
        rowEnd.add(this.rowHeaderForComponentsView, this.notificationBell.createNotificationBell());
        rowEnd.setAlignItems(FlexComponent.Alignment.CENTER);

        super.add(viewTitle, rowEnd);
        super.setWidthFull();
        super.addClassNames(LumoUtility.Margin.Right.MEDIUM);
        super.setAlignItems(FlexComponent.Alignment.CENTER);
        super.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        super.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        return this;
    }

    /**
     * Change the title of the current view
     *
     * @param title
     */
    public void changeTitle(String title) {
        this.viewTitle.setText(title);
    }

    /**
     * @param currentPageTitle to match
     */
    public void showHeaderComponents(String currentPageTitle) {
        if (currentPageTitle.contains("Read flash")) {
//            Stream.of(this.buttonConfigure, this.buttonRefreshDevices)
//                    .forEach(item -> item.setVisible(true));
            Stream.of(this.configureIcon, this.buttonRefreshDevicesIcon)
                    .forEach(item -> item.setVisible(false));
        } else {
            //Stream.of(this.buttonConfigure, this.buttonRefreshDevices).forEach(item -> item.setVisible(false));
            Stream.of(this.configureIcon, this.buttonRefreshDevicesIcon)
                    .forEach(item -> item.setVisible(false));
        }
    }

    private void detectBrowserSize(final UI ui, final int width) {
        if (width < 500) {
            ui.getPage().fetchCurrentURL(url -> {
                if (url.getPath().contains(READ_FLASH)) {
                    log.info(" < 500 read flash");
                    this.applicationEventPublisher.publishEvent(RefreshDevicesEvent.ENABLE);
                }
            });
        } else { // width != 500
            ui.getPage().fetchCurrentURL(url -> {
                if (url.getPath().contains(READ_FLASH)) {
                    log.info("!= 500 read flash");
                    this.applicationEventPublisher.publishEvent(RefreshDevicesEvent.ENABLE);
                }
            });
        }
    }

    private void executeJsAndBrowserListener(UI ui) {
        ui.getPage().executeJs(RETURN_WINDOW_INNER_WIDTH)
                .then(result -> {
                    final int width = ((Double) result.asNumber()).intValue();
                    log.info("Ancho de la pantalla: {}", width);
                    this.detectBrowserSize(ui, width);
                });

        ui.getPage().addBrowserWindowResizeListener(event -> this.detectBrowserSize(ui, event.getWidth()));
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        final UI ui = event.getUI();
        if (event.getNavigationTarget().equals(ReadFlashView.class)) {
            this.executeJsAndBrowserListener(ui);
            log.info("Main beforeEnter");
        }
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
    }

}
