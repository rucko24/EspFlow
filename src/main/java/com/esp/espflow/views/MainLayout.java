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
import com.esp.espflow.views.webserial.WebSerialView;
import com.infraleap.animatecss.Animated;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.UIDetachedException;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
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

import java.util.Optional;
import java.util.stream.Stream;

import static com.esp.espflow.util.EspFlowConstants.CURSOR_POINTER;
import static com.esp.espflow.util.EspFlowConstants.ESPFLOW_SOURCE_CODE;
import static com.esp.espflow.util.EspFlowConstants.FLASH_ON_SVG;
import static com.esp.espflow.util.EspFlowConstants.FRONTEND_IMAGES_ESPDEVICES;
import static com.esp.espflow.util.EspFlowConstants.FRONTEND_IMAGES_LOGO;
import static com.esp.espflow.util.EspFlowConstants.ROTATE_0_DEGREE;
import static com.esp.espflow.util.EspFlowConstants.SCROLLBAR_CUSTOM_STYLE;
import static com.esp.espflow.util.EspFlowConstants.SIZE_25_PX;
import static com.esp.espflow.util.EspFlowConstants.SIZE_30_PX;
import static com.esp.espflow.util.EspFlowConstants.TABLE_SVG;
import static com.esp.espflow.util.EspFlowConstants.TRANSFORM;
import static com.esp.espflow.util.EspFlowConstants.WEB_SERIAL_ICON_SVG;

/**
 * The main view is a top-level placeholder for other views.
 */
@Log4j2
@AnonymousAllowed
public class MainLayout extends AppLayout {


    /**
     * Services
     */
    private final AuthenticatedUser authenticatedUser;
    private final AccessAnnotationChecker accessChecker;
    private final Flux<EsptoolFRWMessageListItemEvent> subscribersMessageListItems;
    private final Flux<EsptoolVersionMessageListItemEvent> subscribersEsptoolVersionMessageListItems;
    private final Flux<EspflowMessageListItemEvent> subscribersEspFlowMessageEvent;
    private final SettingsDialogView settingsDialogView;
    private final MainFooter mainFooter;
    private final MainHeader mainHeader;
    /**
     * Mutable properties
     */
    private Disposable disposableSubscribersMessageListItems;
    private Disposable disposableSubscribersEsptoolVersionMessageListItems;
    private Disposable disposableSubscribersEspflowMessageEvent;

    public MainLayout(final AuthenticatedUser authenticatedUser,
                      final AccessAnnotationChecker accessChecker,
                      final Flux<EsptoolFRWMessageListItemEvent> subscribersMessageListItems,
                      final SettingsDialogView settingsDialogView,
                      final Flux<EsptoolVersionMessageListItemEvent> subscribersEsptoolVersionMessageListItems,
                      final Flux<EspflowMessageListItemEvent> subscribersEspFlowMessageEvent,
                      final MainFooter mainFooter,
                      final MainHeader mainHeader) {
        this.authenticatedUser = authenticatedUser;
        this.accessChecker = accessChecker;
        this.subscribersMessageListItems = subscribersMessageListItems;
        this.settingsDialogView = settingsDialogView;
        this.subscribersEsptoolVersionMessageListItems = subscribersEsptoolVersionMessageListItems;
        this.subscribersEspFlowMessageEvent = subscribersEspFlowMessageEvent;
        this.mainFooter = mainFooter;
        this.mainHeader = mainHeader;

        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        addToNavbar(true, toggle, this.mainHeader.createHeaderRow());
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
            this.mainHeader.getInboxCounter().setVisible(false);
            this.mainHeader.getInboxCounter().getElement().getThemeList().add("badge contrast pill");
            this.mainHeader.getInboxCounter().getElement().setAttribute("aria-label", "12 unread messages");
            Tooltip.forComponent(this.mainHeader.getInboxCounter()).setText("unread messages");
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

        if (accessChecker.hasAccess(WebSerialView.class)) {
            nav.addItem(new SideNavItem("Web-Serial", WebSerialView.class, SvgFactory.createIconFromSvg(WEB_SERIAL_ICON_SVG, SIZE_30_PX, null)));
        }

        if (accessChecker.hasAccess(AboutView.class)) {
            nav.addItem(new SideNavItem("About", AboutView.class, VaadinIcon.INFO_CIRCLE.create()));
        }

        return nav;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        this.mainHeader.getViewTitle().setText(getCurrentPageTitle());
        if (getCurrentPageTitle().contains("Read flash")) {
            this.mainHeader.getButtonRefreshDevices().setVisible(true);
            this.mainHeader.getButtonConfigure().setVisible(true);
            this.mainHeader.getInfoCircleIcon().setVisible(true);
            Stream.of(this.mainHeader.getButtonRefreshDevices(), this.mainHeader.getButtonConfigure(), this.mainHeader.getInfoCircleIcon())
                    .forEach(item -> Animated.animate(item, Animated.Animation.FADE_IN));
        } else {
            this.mainHeader.getButtonRefreshDevices().setVisible(false);
            this.mainHeader.getButtonConfigure().setVisible(false);
            this.mainHeader.getInfoCircleIcon().setVisible(false);
        }
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
                this.mainHeader.getMessageListItemUnreadList().add(messageListItem);
                this.mainHeader.getMessageListItemAllList().add(messageListItem);
                this.mainHeader.getMessageListRead().setItems(this.mainHeader.getMessageListItemUnreadList());
                this.mainHeader.getInboxCounter().setText(String.valueOf(this.mainHeader.getMessageListItemUnreadList().size()));
                this.mainHeader.getMessageListAll().setItems(this.mainHeader.getMessageListItemAllList());
                this.mainHeader.getInboxCounter().setVisible(true);
                this.showsRedErrorInTheBell();
                Animated.animate(this.mainHeader.getSpanCircleRed(), Animated.Animation.FADE_IN);
                Animated.animate(this.mainHeader.getInboxCounter(), Animated.Animation.FADE_IN);
                if (this.mainHeader.getContentUnread().getElement().getChildCount() == 0) {
                    this.mainHeader.getBellIcon().getStyle().set(TRANSFORM, ROTATE_0_DEGREE);
                    Animated.animate(this.mainHeader.getBellIcon(), Animated.Animation.FADE_IN);
                    this.showsRedErrorInTheBell();
                    this.mainHeader.getContentUnread().add(this.mainHeader.getMessageListRead());
                }
            });
        } catch (UIDetachedException ex) {
            //Do nothing, "Browser window was closed while pushing updates."
        }
    }

    /**
     * When an event notification arrives, we paint a red circle on the bell.
     */
    public void showsRedErrorInTheBell() {
        this.mainHeader.getSpanCircleRed().getElement().getThemeList().add("badge small error dot primary");
    }


    private void closeSubscribers() {
        if (this.disposableSubscribersMessageListItems != null) {
            disposableSubscribersMessageListItems.dispose();
            disposableSubscribersMessageListItems = null;
        }
        if (this.disposableSubscribersEsptoolVersionMessageListItems != null) {
            disposableSubscribersEsptoolVersionMessageListItems.dispose();
            disposableSubscribersEsptoolVersionMessageListItems = null;
        }
        if (this.disposableSubscribersEspflowMessageEvent != null) {
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
