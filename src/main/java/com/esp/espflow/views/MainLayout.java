package com.esp.espflow.views;

import com.esp.espflow.entity.User;
import com.esp.espflow.entity.event.EspflowMessageListItemEvent;
import com.esp.espflow.entity.event.EsptoolFRWMessageListItemEvent;
import com.esp.espflow.entity.event.EsptoolVersionMessageListItemEvent;
import com.esp.espflow.security.AuthenticatedUser;
import com.esp.espflow.util.svgfactory.SvgFactory;
import com.esp.espflow.views.about.AboutView;
import com.esp.espflow.views.flashesp.FlashEspView;
import com.esp.espflow.views.hexdump.HexDumpView;
import com.esp.espflow.views.readflash.ReadFlashView;
import com.esp.espflow.views.settings.SettingsDialogView;
import com.infraleap.animatecss.Animated;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.UIDetachedException;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.popover.Popover;
import com.vaadin.flow.component.popover.PopoverPosition;
import com.vaadin.flow.component.popover.PopoverVariant;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.JustifyContent;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import lombok.extern.log4j.Log4j2;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.esp.espflow.util.EspFlowConstants.CURSOR_POINTER;
import static com.esp.espflow.util.EspFlowConstants.ESPFLOW_SOURCE_CODE;
import static com.esp.espflow.util.EspFlowConstants.FLASH_ON_SVG;
import static com.esp.espflow.util.EspFlowConstants.FRONTEND_IMAGES_ESPDEVICES;
import static com.esp.espflow.util.EspFlowConstants.FRONTEND_IMAGES_LOGO;
import static com.esp.espflow.util.EspFlowConstants.ROTATE_0_DEGREE;
import static com.esp.espflow.util.EspFlowConstants.SCROLLBAR_CUSTOM_STYLE;
import static com.esp.espflow.util.EspFlowConstants.SIZE_25_PX;
import static com.esp.espflow.util.EspFlowConstants.TABLE_SVG;
import static com.esp.espflow.util.EspFlowConstants.TRANSFORM;

/**
 * The main view is a top-level placeholder for other views.
 */
@Log4j2
@AnonymousAllowed
public class MainLayout extends AppLayout {

    private final Popover popover = new Popover();
    private final Div contentUnread = new Div();
    private final Div divBell = new Div();
    private final SvgIcon bellIcon = SvgFactory.createIconFromSvg("bell.svg", "24px", null);
    private final Span spanCircleRed = new Span();
    private final Span inboxCounter = new Span("0");
    private final Div contentAll = new Div();
    private final MessageList messageListRead = new MessageList();
    private final MessageList messageListAll = new MessageList();
    private final List<MessageListItem> messageListItemUnreadList = new CopyOnWriteArrayList<>();
    private final List<MessageListItem> messageListItemAllList = new CopyOnWriteArrayList<>();
    private H1 viewTitle;

    private final AuthenticatedUser authenticatedUser;
    private final AccessAnnotationChecker accessChecker;
    private final Flux<EsptoolFRWMessageListItemEvent> subscribersMessageListItems;
    private final Flux<EsptoolVersionMessageListItemEvent> subscribersEsptoolVersionMessageListItems;
    private final Flux<EspflowMessageListItemEvent> subscribersEspFlowMessageEvent;
    private final SettingsDialogView settingsDialogView;
    private final MainFooter mainFooter;
    private Disposable disposableSubscribersMessageListItems;
    private Disposable disposableSubscribersEsptoolVersionMessageListItems;
    private Disposable disposableSubscribersEspflowMessageEvent;

    public MainLayout(final AuthenticatedUser authenticatedUser,
                      final AccessAnnotationChecker accessChecker,
                      final Flux<EsptoolFRWMessageListItemEvent> subscribersMessageListItems,
                      final SettingsDialogView settingsDialogView,
                      final Flux<EsptoolVersionMessageListItemEvent> subscribersEsptoolVersionMessageListItems,
                      final Flux<EspflowMessageListItemEvent> subscribersEspFlowMessageEvent,
                      final MainFooter mainFooter) {
        this.authenticatedUser = authenticatedUser;
        this.accessChecker = accessChecker;
        this.subscribersMessageListItems = subscribersMessageListItems;
        this.settingsDialogView = settingsDialogView;
        this.subscribersEsptoolVersionMessageListItems = subscribersEsptoolVersionMessageListItems;
        this.subscribersEspFlowMessageEvent = subscribersEspFlowMessageEvent;
        this.mainFooter = mainFooter;

        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        addToNavbar(true, toggle, this.headerRow());
    }

    /**
     * @return A {@link HorizontalLayout}
     */
    private HorizontalLayout headerRow() {
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
        headerRow.addClassNames(JustifyContent.AROUND, AlignItems.CENTER);
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

        viewTitle = new H1();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        final var row = new HorizontalLayout(viewTitle, divBell);
        row.setWidthFull();
        row.addClassNames(LumoUtility.Margin.Right.MEDIUM);
        row.setAlignItems(FlexComponent.Alignment.CENTER);
        row.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        row.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        return row;
    }

    private void addDrawerContent() {
        Span appName = new Span();
        final Image logo = new Image(FRONTEND_IMAGES_LOGO + "espflow_176px.png", "logo");
        Tooltip.forComponent(logo).setText(ESPFLOW_SOURCE_CODE);
        logo.getStyle().setCursor(CURSOR_POINTER);
        logo.setMaxWidth("75%");
        logo.setHeight("auto");
        logo.addClickListener(e -> {
            getUI().ifPresent(ui -> ui.getPage().open(ESPFLOW_SOURCE_CODE));
        });
        appName.add(logo);
        appName.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.LARGE);

        Header header = new Header(appName);
        header.setId("header-logo");
        addToDrawer(header);

        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();

            var avatar = new Avatar(user.getName(), FRONTEND_IMAGES_ESPDEVICES + "ESP8285H08-2MB.jpeg");
            avatar.setWidth("56px");
            avatar.setHeight("56px");

            var row = new HorizontalLayout(avatar);
            row.setWidthFull();
            row.addClassNames(AlignItems.CENTER, JustifyContent.CENTER, Padding.MEDIUM);
            addToDrawer(row);
        }

        final Scroller scroller = new Scroller(createNavigation());
        scroller.getElement().executeJs(SCROLLBAR_CUSTOM_STYLE);
        addToDrawer(scroller, mainFooter.createFooter());

    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();
        if (accessChecker.hasAccess(FlashEspView.class)) {
            var itemFlash = new SideNavItem("Flash Esp32-ESP8266", FlashEspView.class, SvgFactory.createIconFromSvg(FLASH_ON_SVG, SIZE_25_PX, null));
            inboxCounter.setVisible(false);
            inboxCounter.getElement().getThemeList().add("badge contrast pill");
            inboxCounter.getElement().setAttribute("aria-label", "12 unread messages");
            Tooltip.forComponent(inboxCounter).setText("unread messages");
            //itemFlash.setSuffixComponent(inboxCounter);
            Tooltip.forComponent(itemFlash).setText("Flash Esp32-ESP8266, Execute flash_id and write flash");
            nav.addItem(itemFlash);
        }

        if (accessChecker.hasAccess(ReadFlashView.class)) {
            var itemReadFlash = new SideNavItem("Read flash/firmware", ReadFlashView.class, VaadinIcon.DOWNLOAD.create());
            Tooltip.forComponent(itemReadFlash).setText("All devices are read and displayed in a carousel");
            nav.addItem(itemReadFlash);
        }

        if (accessChecker.hasAccess(HexDumpView.class)) {
            nav.addItem(new SideNavItem("HexDump", HexDumpView.class, SvgFactory.createIconFromSvg(TABLE_SVG, SIZE_25_PX, null)));
        }

        if (accessChecker.hasAccess(AboutView.class)) {
            nav.addItem(new SideNavItem("About", AboutView.class, VaadinIcon.INFO_CIRCLE.create()));
        }

        return nav;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }

    /**
     * @param ui
     * @param messageListItem
     */
    private void subscribe(final UI ui, final MessageListItem messageListItem) {
        try {
            ui.access(() -> {
                log.info("MessageListItem listener {}", messageListItem.getText());
                messageListItemUnreadList.add(messageListItem);
                messageListItemAllList.add(messageListItem);
                this.messageListRead.setItems(messageListItemUnreadList);
                this.inboxCounter.setText(String.valueOf(messageListItemUnreadList.size()));
                this.messageListAll.setItems(messageListItemAllList);
                this.inboxCounter.setVisible(true);
                this.showsRedErrorInTheBell();
                Animated.animate(spanCircleRed, Animated.Animation.FADE_IN);
                Animated.animate(inboxCounter, Animated.Animation.FADE_IN);
                if (this.contentUnread.getElement().getChildCount() == 0) {
                    bellIcon.getStyle().set(TRANSFORM, ROTATE_0_DEGREE);
                    Animated.animate(bellIcon, Animated.Animation.FADE_IN);
                    this.showsRedErrorInTheBell();
                    contentUnread.add(messageListRead);
                }
            });
        } catch (UIDetachedException ex) {
            //Do nothing, "Browser window was closed while pushing updates."
        }
    }

    /**
     * We rotate the bell to zero if the notification panel is closed.
     *
     * @param event a Popover.OpenedChangeEvent
     */
    public void rotateTheBellToZeroDegreesIfThePopoverIsNotOpen(Popover.OpenedChangeEvent event) {
        if (!event.getSource().isOpened()) {
            bellIcon.getStyle().set(TRANSFORM, ROTATE_0_DEGREE);
            Animated.removeAnimations(bellIcon);
        }
    }

    /**
     * When an event notification arrives, we paint a red circle on the bell.
     */
    public void showsRedErrorInTheBell() {
        spanCircleRed.getElement().getThemeList().add("badge small error dot primary");
    }

    /**
     * When the bell is clicked, and when we mark messages as read, we remove the style in the red circle, so that we can set it again to give the correct effect.
     */
    public void removeRedCircleErrorInTheBell() {
        spanCircleRed.getElement().removeAttribute("theme");
    }

    private void closeSubscribers() {
        if (this.disposableSubscribersMessageListItems != null) {
            disposableSubscribersMessageListItems.dispose();
            disposableSubscribersMessageListItems = null;
        }
        if (disposableSubscribersEsptoolVersionMessageListItems != null) {
            disposableSubscribersEsptoolVersionMessageListItems.dispose();
            disposableSubscribersEsptoolVersionMessageListItems = null;
        }
        if(disposableSubscribersEspflowMessageEvent != null) {
            disposableSubscribersEspflowMessageEvent.dispose();
            disposableSubscribersEspflowMessageEvent = null;
        }
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        this.settingsDialogView.close();
        this.closeSubscribers();
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        this.closeSubscribers();
        final UI ui = attachEvent.getUI();
        //Subscription to flash_id, read_flash, write_flash events
        this.disposableSubscribersMessageListItems = this.subscribersMessageListItems
                .subscribe(messageListItem -> {
                    this.subscribe(ui, messageListItem);
                });
        //Subscription to esptool version events
        this.disposableSubscribersEsptoolVersionMessageListItems = this.subscribersEsptoolVersionMessageListItems
                .subscribe(esptoolVersionMessageListItemEvent -> {
                    this.subscribe(ui, esptoolVersionMessageListItemEvent);
                });
        this.subscribersEspFlowMessageEvent
                .subscribe(espflowMessageListItemEvent -> {
                    this.subscribe(ui, espflowMessageListItemEvent);
                });

    }

}
