package com.esp.espflow.views;

import com.esp.espflow.enums.RefreshDevicesEvent;
import com.esp.espflow.util.svgfactory.SvgFactory;
import com.esp.espflow.views.settings.SettingsDialogView;
import com.infraleap.animatecss.Animated;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.UIDetachedException;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.popover.Popover;
import com.vaadin.flow.component.popover.PopoverPosition;
import com.vaadin.flow.component.popover.PopoverVariant;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.tabs.TabSheet;
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
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

import static com.esp.espflow.util.EspFlowConstants.BOX_SHADOW_VAADIN_BUTTON;
import static com.esp.espflow.util.EspFlowConstants.CURSOR_POINTER;
import static com.esp.espflow.util.EspFlowConstants.RETURN_WINDOW_INNER_WIDTH;
import static com.esp.espflow.util.EspFlowConstants.ROTATE_0_DEGREE;
import static com.esp.espflow.util.EspFlowConstants.TRANSFORM;

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
    private final SvgIcon bellIcon = SvgFactory.createIconFromSvg("bell.svg", "24px", null);
    private final Button configureIcon = new Button(LineAwesomeIcon.SLIDERS_H_SOLID.create());
    private final Button buttonRefreshDevicesIcon = new Button(VaadinIcon.REFRESH.create());

    private final Div divBell = new Div();
    private final Span spanCircleRed = new Span();
    private final Popover popover = new Popover();
    private final Div contentAll = new Div();
    private final Div contentUnread = new Div();
    private final Span inboxCounter = new Span("0");
    private final MessageList messageListRead = new MessageList();
    private final MessageList messageListAll = new MessageList();
    private final List<MessageListItem> messageListItemUnreadList = new CopyOnWriteArrayList<>();
    private final List<MessageListItem> messageListItemAllList = new CopyOnWriteArrayList<>();
    private final Button buttonRefreshDevices = new Button("Refresh devices", VaadinIcon.REFRESH.create());
    private final Button buttonConfigure = new Button(CONFIGURE, LineAwesomeIcon.SLIDERS_H_SOLID.create());
    private final Icon shoWizardIcon = VaadinIcon.INFO_CIRCLE.create();
    /**
     * Services
     */
    private final SettingsDialogView settingsDialogView;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final Flux<RefreshDevicesEvent> subscriberRefreshDevicesEvent;
    private final Sinks.Many<RefreshDevicesEvent> publisherRefreshEvent;

    private final H1 viewTitle = new H1();
    private Disposable disposableRefreshEvents;

    @PostConstruct
    public void setup() {
        //Do nothing at the moment!
    }

    /**
     * @return A {@link HorizontalLayout}
     */
    public MainHeader createHeaderRow() {
        Tooltip.forComponent(divBell).setText("Notifications");
        bellIcon.addClassName("black-to-white");
        divBell.add(bellIcon);
        divBell.addClassNames(LumoUtility.Display.FLEX, LumoUtility.FlexDirection.ROW);
        divBell.getStyle().setCursor(CURSOR_POINTER);

        divBell.addClickListener(event -> {
            Animated.animate(bellIcon, Animated.Animation.FADE_IN);
            bellIcon.getStyle().set(TRANSFORM, "rotate(20deg)");
            this.removeRedCircleErrorInTheBell();
            Animated.removeAnimations(spanCircleRed);
        });
        spanCircleRed.addClassNames(LumoUtility.Display.INLINE_BLOCK, LumoUtility.Position.ABSOLUTE);
        spanCircleRed.getStyle().set("margin-left", "13px");
        spanCircleRed.getStyle().set("top", "14px");
        divBell.add(spanCircleRed);

        popover.setTarget(divBell);
        popover.setWidth("340px");
        popover.addThemeVariants(PopoverVariant.ARROW, PopoverVariant.LUMO_NO_PADDING);
        popover.setPosition(PopoverPosition.BOTTOM);
        popover.setAriaLabelledBy("notifications-heading");
        popover.setModal(true);
        popover.setBackdropVisible(true);
        popover.addOpenedChangeListener(this::rotateTheBellToZeroDegreesIfThePopoverIsNotOpen);

        final Button buttonMarkAllRead = new Button("Marks all read");
        buttonMarkAllRead.addClassName(BOX_SHADOW_VAADIN_BUTTON);
        buttonMarkAllRead.addClickListener(event -> {
            this.removeRedCircleErrorInTheBell();
            messageListItemUnreadList.clear();
            bellIcon.getStyle().set(TRANSFORM, ROTATE_0_DEGREE);
            contentUnread.removeAll();
            this.inboxCounter.setVisible(false);
            Animated.removeAnimations(spanCircleRed);
        });

        final Button buttonSettings = new Button(VaadinIcon.COG.create());
        buttonSettings.setTooltipText("Open notifications settings - Ctrl+Alt+S");
        buttonSettings.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        buttonSettings.addClickListener(event -> {
            getUI().ifPresent(ui -> {
                ui.getPage().fetchCurrentURL(url -> {
                    log.info("fetchCurrentURL {}", url);
                    String urlWithParameters = url.getPath().concat("#settings/notifications");
                    ui.getPage().getHistory().replaceState(null, urlWithParameters);
                    settingsDialogView.open("settings/notifications");
                });
            });
        });

        var headerRow = new HorizontalLayout(new H4("Notifications"), buttonMarkAllRead, buttonSettings);
        headerRow.addClassNames(LumoUtility.JustifyContent.AROUND, LumoUtility.AlignItems.CENTER);
        headerRow.getStyle().set("padding", "var(--lumo-space-m) var(--lumo-space-m) var(--lumo-space-xs)");
        popover.add(headerRow);

        final TabSheet tabSheet = new TabSheet();
        tabSheet.addClassName("notifications");

        messageListRead.setItems(messageListItemUnreadList);
        contentUnread.add(messageListRead);
        contentAll.add(messageListAll);

        tabSheet.add("Unread", contentUnread);
        tabSheet.add("All", contentAll);
        popover.add(tabSheet);

        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        this.buttonConfigure.addClassName(BOX_SHADOW_VAADIN_BUTTON);
        this.buttonRefreshDevices.setId("button-refresh-device");
        this.buttonRefreshDevices.setEnabled(true);
        this.buttonRefreshDevices.addClassName(BOX_SHADOW_VAADIN_BUTTON);
        this.buttonRefreshDevices.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        this.buttonRefreshDevices.addClickShortcut(Key.ENTER);
        this.buttonRefreshDevices.setDisableOnClick(true);
        this.shoWizardIcon.getStyle().setCursor(CURSOR_POINTER);
        this.shoWizardIcon.getStyle().setColor("var(--lumo-contrast-60pct)");
        this.shoWizardIcon.setTooltipText("Show dialog");
        this.configureIcon.setTooltipText(CONFIGURE);
        this.configureIcon.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        this.buttonRefreshDevicesIcon.setTooltipText(CONFIGURE);
        this.buttonRefreshDevicesIcon.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        //Added listeners here, only once
        //Envio de scan
        this.buttonRefreshDevices.addClickListener(event -> this.applicationEventPublisher.publishEvent(RefreshDevicesEvent.SCAN));
        this.buttonRefreshDevicesIcon.addClickListener(event -> this.applicationEventPublisher.publishEvent(RefreshDevicesEvent.SCAN));
        this.buttonConfigure.addClickListener(event -> this.applicationEventPublisher.publishEvent(RefreshDevicesEvent.OPEN_SIDEBAR));
        this.configureIcon.addClickListener(event -> this.applicationEventPublisher.publishEvent(RefreshDevicesEvent.OPEN_SIDEBAR));
        this.shoWizardIcon.addClickListener(event -> this.applicationEventPublisher.publishEvent(RefreshDevicesEvent.OPEN_READ_FLASH_WIZARD));

        final HorizontalLayout rowEnd = new HorizontalLayout(
                this.shoWizardIcon,
                this.configureIcon,
                this.buttonRefreshDevicesIcon,
                this.buttonConfigure,
                this.buttonRefreshDevices, this.divBell);
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
            Stream.of(this.buttonConfigure, this.buttonRefreshDevices)
                    .forEach(item -> item.setVisible(true));
            Stream.of(this.configureIcon, this.buttonRefreshDevicesIcon)
                    .forEach(item -> item.setVisible(false));
            this.shoWizardIcon.setVisible(true);
        } else {
            this.shoWizardIcon.setVisible(false);
            Stream.of(this.buttonConfigure, this.buttonRefreshDevices)
                    .forEach(item -> item.setVisible(false));
            Stream.of(this.configureIcon, this.buttonRefreshDevicesIcon)
                    .forEach(item -> item.setVisible(false));
        }
    }

    /**
     * When the bell is clicked, and when we mark messages as read, we remove the style in the red circle, so that we can set it again to give the correct effect.
     */
    private void removeRedCircleErrorInTheBell() {
        spanCircleRed.getElement().removeAttribute("theme");
    }

    /**
     * We rotate the bell to zero if the notification panel is closed.
     *
     * @param event a Popover.OpenedChangeEvent
     */
    private void rotateTheBellToZeroDegreesIfThePopoverIsNotOpen(Popover.OpenedChangeEvent event) {
        if (!event.getSource().isOpened()) {
            bellIcon.getStyle().set(TRANSFORM, ROTATE_0_DEGREE);
            Animated.removeAnimations(bellIcon);
        }
    }

    private void detectBrowserSize(final UI ui, final int width) {
        if (width < 500) {
            ui.getPage().fetchCurrentURL(url -> {
                if (url.getPath().contains(READ_FLASH)) {
                    Stream.of(this.buttonConfigure, this.buttonRefreshDevices)
                            .forEach(item -> {
                                item.setVisible(false);
                            });
                    Stream.of(this.configureIcon, this.buttonRefreshDevicesIcon)
                            .forEach(item -> {
                                item.setVisible(true);
                            });
                }
            });
        } else { // width != 500
            ui.getPage().fetchCurrentURL(url -> {
                if (url.getPath().contains(READ_FLASH)) {
                    Stream.of(this.buttonConfigure, this.buttonRefreshDevices)
                            .forEach(item -> {
                                item.setVisible(true);
                            });
                    Stream.of(this.configureIcon, this.buttonRefreshDevicesIcon)
                            .forEach(item -> {
                                item.setVisible(false);
                            });
                }
            });
        }
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

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        final UI ui = event.getUI();
        this.executeJsAndBrowserListener(ui);
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
                ui.access(() -> this.buttonRefreshDevices.setEnabled(value));
            } catch (UIDetachedException ex) {
                //Do nothing,  It is thrown when you attempt to access closed UI.
                //https://stackoverflow.com/a/73885127/7267818
            }
        });

        this.executeJsAndBrowserListener(ui);
    }

}
