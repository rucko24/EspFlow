package com.esp.espflow.views;

import com.esp.espflow.entity.User;
import com.esp.espflow.security.AuthenticatedUser;
import com.esp.espflow.util.svgfactory.SvgFactory;
import com.esp.espflow.views.about.AboutView;
import com.esp.espflow.views.flashesp.FlashEspView;
import com.esp.espflow.views.readflash.ReadFlashView;
import com.infraleap.animatecss.Animated;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.UIDetachedException;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
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
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.JustifyContent;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.esp.espflow.util.EspFlowConstants.FLASH_ON_SVG;
import static com.esp.espflow.util.EspFlowConstants.FRONTEND_IMAGES_ESPDEVICES;
import static com.esp.espflow.util.EspFlowConstants.FRONTEND_IMAGES_LOGO;
import static com.esp.espflow.util.EspFlowConstants.SIZE_25_PX;

/**
 * The main view is a top-level placeholder for other views.
 */
@Log4j2
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
    private List<MessageListItem> messageListItemUnreadList = new CopyOnWriteArrayList<>();
    private List<MessageListItem> messageListItemAllList = new CopyOnWriteArrayList<>();

    private H1 viewTitle;

    private final AuthenticatedUser authenticatedUser;
    private final AccessAnnotationChecker accessChecker;
    private final Flux<MessageListItem> subscribersMessageListItems;

    public MainLayout(AuthenticatedUser authenticatedUser, AccessAnnotationChecker accessChecker,
                      Flux<MessageListItem> subscribersMessageListItems) {
        this.authenticatedUser = authenticatedUser;
        this.accessChecker = accessChecker;
        this.subscribersMessageListItems = subscribersMessageListItems;

        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H1();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        addToNavbar(true, toggle, this.headerRow());
    }

    /**
     * @return A {@link HorizontalLayout}
     */
    private HorizontalLayout headerRow() {
        Tooltip.forComponent(divBell).setText("Notifications");
        divBell.add(bellIcon);
        divBell.addClassNames(LumoUtility.Display.FLEX, LumoUtility.FlexDirection.ROW);

        divBell.addClickListener(event -> {
            bellIcon.getStyle().set("transform", "rotate(20deg)");
            this.removeRedCircleError();
            Animated.animate(bellIcon, Animated.Animation.FADE_IN);
        });
        spanCircleRed.addClassNames(LumoUtility.Display.INLINE_BLOCK, LumoUtility.Position.FIXED);
        spanCircleRed.getStyle().set("margin-left", "13px");
        spanCircleRed.getStyle().set("top", "14px");
        divBell.add(spanCircleRed);

        popover.setTarget(divBell);
        popover.setWidth("300px");
        popover.addThemeVariants(PopoverVariant.ARROW, PopoverVariant.LUMO_NO_PADDING);
        popover.setPosition(PopoverPosition.BOTTOM);
        popover.setAriaLabelledBy("notifications-heading");
        popover.setModal(true);
        popover.setBackdropVisible(true);
        popover.addOpenedChangeListener(this::rotateTheBellToZeroDegreesIfThePopoverIsNotOpen);

        final var buttonMarkAllRead = new Button("Marks all read");
        buttonMarkAllRead.addClickListener(event -> {
            this.removeRedCircleError();
            messageListItemUnreadList.clear();
            bellIcon.getStyle().set("transform", "rotate(0deg)");
            contentUnread.removeAll();
            this.inboxCounter.setVisible(false);
            Animated.removeAnimations(spanCircleRed);
        });

        var headerRow = new HorizontalLayout(new H4("Notifications"), buttonMarkAllRead);
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
        Tooltip.forComponent(logo).setText("https://github.com/rucko24/EspFlow");
        logo.getStyle().setCursor("pointer");
        logo.setMaxWidth("75%");
        logo.setHeight("auto");
        logo.addClickListener(e -> {
            getUI().ifPresent(ui -> ui.getPage().open("https://github.com/rucko24/EspFlow"));
        });
        appName.add(logo);
        appName.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.LARGE);
        Header header = new Header(appName);
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
        addToDrawer(scroller, createFooter());

    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();

        if (accessChecker.hasAccess(FlashEspView.class)) {
            var itemFlash = new SideNavItem("Flash Esp32-ESP8266", FlashEspView.class, SvgFactory.createIconFromSvg(FLASH_ON_SVG, SIZE_25_PX, null));
            inboxCounter.setVisible(false);
            inboxCounter.getElement().getThemeList().add("badge contrast pill");
            inboxCounter.getElement().setAttribute("aria-label","12 unread messages");
            Tooltip.forComponent(inboxCounter).setText("unread messages");
            itemFlash.setSuffixComponent(inboxCounter);
            Tooltip.forComponent(itemFlash).setText("Flash Esp32-ESP8266, Execute flash_id and write flash");
            nav.addItem(itemFlash);
        }

        if (accessChecker.hasAccess(ReadFlashView.class)) {
            var itemReadFlash = new SideNavItem("Read flash/firmware", ReadFlashView.class, VaadinIcon.DOWNLOAD.create());
            Tooltip.forComponent(itemReadFlash).setText("All devices are read and displayed in a carousel");
            nav.addItem(itemReadFlash);
        }

        if (accessChecker.hasAccess(AboutView.class)) {
            nav.addItem(new SideNavItem("About", AboutView.class, VaadinIcon.INFO_CIRCLE.create()));

        }

        return nav;
    }

    private Footer createFooter() {
        Footer layout = new Footer();
        layout.addClassNames("app-nav-footer");

        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();

            Avatar avatar = new Avatar(user.getName(), user.getProfilePictureUrl());
            avatar.setThemeName("xsmall");
            avatar.getElement().setAttribute("tabindex", "-1");

            MenuBar userMenu = new MenuBar();
            userMenu.setThemeName("tertiary-inline contrast");

            MenuItem userName = userMenu.addItem("");
            Div div = new Div();
            div.add(avatar);
            div.add(user.getName());
            div.add(new Icon("lumo", "dropdown"));
            div.getElement().getStyle().set("display", "flex");
            div.getElement().getStyle().set("align-items", "center");
            div.getElement().getStyle().set("gap", "var(--lumo-space-s)");
            userName.add(div);
            userName.getSubMenu().addItem("Sign out", e -> {
                authenticatedUser.logout();
            });

            layout.add(userMenu);
        } else {
            Anchor loginLink = new Anchor("login", "Sign in");
            layout.add(loginLink);
        }

        return layout;
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

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        if (attachEvent.isInitialAttach()) {
            final UI ui = attachEvent.getUI();
            subscribersMessageListItems
                    .subscribe(messageListItem -> {
                        this.subscribe(ui, messageListItem);
                    });
        }
    }

    /**
     * @param ui
     * @param messageListItem
     */
    private void subscribe(final UI ui, final MessageListItem messageListItem) {
        try {
            ui.access(() -> {
                System.out.println("MessageListItem listener " + messageListItem.getText());
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
                    bellIcon.getStyle().set("transform", "rotate(0deg)");
                    Animated.animate(bellIcon, Animated.Animation.FADE_IN);
                    this.showsRedErrorInTheBell();
                    contentUnread.add(messageListRead);
                }
            });
        } catch (UIDetachedException ex) {
            //Do nothing
        }
    }

    /**
     *
     *  @param event a Popover.OpenedChangeEvent
     */
    public void rotateTheBellToZeroDegreesIfThePopoverIsNotOpen(Popover.OpenedChangeEvent event) {
        if (!event.getSource().isOpened()) {
            bellIcon.getStyle().set("transform", "rotate(0deg)");
        }
    }

    /**
     *
     */
    public void showsRedErrorInTheBell() {
        spanCircleRed.getElement().getThemeList().add("badge small error dot primary");
        //spanCircleRed.getElement().getThemeList().add("badge small primary dot primary");
    }

    /**
     *
     */
    public void removeRedCircleError() {
        spanCircleRed.getElement().removeAttribute("theme");
    }
}
