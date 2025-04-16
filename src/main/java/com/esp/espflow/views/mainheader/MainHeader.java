package com.esp.espflow.views.mainheader;

import com.esp.espflow.entity.event.ReadFlashUpdateEvent;
import com.esp.espflow.enums.RefreshDevicesEvent;
import com.esp.espflow.views.readflash.ReadFlashView;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.UIDetachedException;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
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
import org.springframework.context.event.EventListener;
import org.vaadin.lineawesome.LineAwesomeIcon;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.stream.Stream;

import static com.esp.espflow.util.EspFlowConstants.BOX_SHADOW_VAADIN_BUTTON;
import static com.esp.espflow.util.EspFlowConstants.CURSOR_POINTER;
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
    public static final String CONFIGURE = "Configure";

    private final Button buttonConfigure = new Button(CONFIGURE, LineAwesomeIcon.SLIDERS_H_SOLID.create());
    private final Icon shoWizardIcon = VaadinIcon.INFO_CIRCLE.create();
    private final Button configureIcon = new Button(LineAwesomeIcon.SLIDERS_H_SOLID.create());
    private final Button buttonRefreshDevicesIcon = new Button(VaadinIcon.REFRESH.create());
    private final H1 viewTitle = new H1();

    /**
     * Services
     */
    private final ApplicationEventPublisher applicationEventPublisher;
    private final Flux<RefreshDevicesEvent> subscriberRefreshDevicesEvent;
    private final Sinks.Many<RefreshDevicesEvent> publisherRefreshEvent;
    private final NotificationBell notificationBell;

    private Disposable disposableRefreshEvents;

    @PostConstruct
    public void setup() {
        //Do nothing at the moment!
        ComponentUtil.addListener(UI.getCurrent(), ReadFlashUpdateEvent.class, event -> {
            // Actualiza, por ejemplo, la visibilidad o el texto del botón según el evento recibido.
            //updateHeaderBasedOnEvent(event.getInfo());
        });
    }

    /**
     * @return A {@link HorizontalLayout}
     */
    public MainHeader createHeaderRow() {
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        this.buttonConfigure.addClassName(BOX_SHADOW_VAADIN_BUTTON);

        this.shoWizardIcon.getStyle().setCursor(CURSOR_POINTER);
        this.shoWizardIcon.getStyle().setColor("var(--lumo-contrast-60pct)");
        this.shoWizardIcon.setTooltipText("Show dialog");
        this.configureIcon.setTooltipText(CONFIGURE);
        this.configureIcon.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        this.buttonRefreshDevicesIcon.setTooltipText(CONFIGURE);
        this.buttonRefreshDevicesIcon.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        //Added listeners here, only once
        //Envio de scan
        this.buttonRefreshDevicesIcon.addClickListener(event -> this.applicationEventPublisher.publishEvent(RefreshDevicesEvent.SCAN));
        this.buttonConfigure.addClickListener(event -> this.applicationEventPublisher.publishEvent(RefreshDevicesEvent.OPEN_SIDEBAR));
        this.configureIcon.addClickListener(event -> this.applicationEventPublisher.publishEvent(RefreshDevicesEvent.OPEN_SIDEBAR));
        this.shoWizardIcon.addClickListener(event -> this.applicationEventPublisher.publishEvent(RefreshDevicesEvent.OPEN_READ_FLASH_WIZARD));

        final HorizontalLayout rowEnd = new HorizontalLayout(
                this.shoWizardIcon,
                this.configureIcon,
                this.buttonRefreshDevicesIcon,
                this.buttonConfigure, this.notificationBell.createNotificationBell());
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
            this.shoWizardIcon.setVisible(true);
        } else {
            this.shoWizardIcon.setVisible(false);
            //Stream.of(this.buttonConfigure, this.buttonRefreshDevices).forEach(item -> item.setVisible(false));
            Stream.of(this.configureIcon, this.buttonRefreshDevicesIcon)
                    .forEach(item -> item.setVisible(false));
        }
    }


    private void detectBrowserSize(final UI ui, final int width) {
//        if (width < 500) {
//            ui.getPage().fetchCurrentURL(url -> {
//                if (url.getPath().contains(READ_FLASH)) {
//                    Stream.of(this.buttonConfigure, this.buttonRefreshDevices)
//                            .forEach(item -> {
//                                //item.setVisible(false);
//                                log.info("setVisible(false) buttoms");
//                            });
//                    Stream.of(this.configureIcon, this.buttonRefreshDevicesIcon)
//                            .forEach(item -> {
//                                item.setVisible(true);
//                            });
//                }
//            });
//        } else { // width != 500
//            ui.getPage().fetchCurrentURL(url -> {
//                if (url.getPath().contains(READ_FLASH)) {
//                    Stream.of(this.buttonConfigure, this.buttonRefreshDevices)
//                            .forEach(item -> {
//                                //item.setVisible(true);
//                            });
//                    Stream.of(this.configureIcon, this.buttonRefreshDevicesIcon)
//                            .forEach(item -> {
//                                item.setVisible(false);
//                            });
//                }
//            });
//        }
    }

    private void closeSubscribers() {
        if (disposableRefreshEvents != null) {
            disposableRefreshEvents.dispose();
            disposableRefreshEvents = null;
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

    @EventListener
    public void listenerRefreshEvent(RefreshDevicesEvent refreshDevicesEvent) {
        if (refreshDevicesEvent == RefreshDevicesEvent.ENABLE) {
            this.getUI().ifPresent(this::executeJsAndBrowserListener);
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        final UI ui = event.getUI();
        if (event.getNavigationTarget().equals(ReadFlashView.class)) {
            this.executeJsAndBrowserListener(ui);
        } else {

        }
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        this.closeSubscribers();
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        final UI ui = attachEvent.getUI();
        this.closeSubscribers();
        this.disposableRefreshEvents = this.subscriberRefreshDevicesEvent.subscribe(refreshDevicesEvent -> {
            try {
                var value = RefreshDevicesEvent.fromEvent(refreshDevicesEvent);
                //ui.access(() -> this.buttonRefreshDevices.setEnabled(value));
            } catch (UIDetachedException ex) {
                //Do nothing,  It is thrown when you attempt to access closed UI.
                //https://stackoverflow.com/a/73885127/7267818
            }
        });

        this.executeJsAndBrowserListener(ui);
    }

}
