package com.esp.espflow.views.mainheader;

import com.esp.espflow.util.svgfactory.SvgFactory;
import com.esp.espflow.views.settings.SettingsDialog;
import com.infraleap.animatecss.Animated;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.popover.Popover;
import com.vaadin.flow.component.popover.PopoverPosition;
import com.vaadin.flow.component.popover.PopoverVariant;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.esp.espflow.util.EspFlowConstants.BOX_SHADOW_VAADIN_BUTTON;
import static com.esp.espflow.util.EspFlowConstants.CURSOR_POINTER;
import static com.esp.espflow.util.EspFlowConstants.ROTATE_0_DEGREE;
import static com.esp.espflow.util.EspFlowConstants.SIZE_25_PX;
import static com.esp.espflow.util.EspFlowConstants.TRANSFORM;

/**
 * @author rubn
 */
@Getter
@Log4j2
@UIScope
@SpringComponent
@RequiredArgsConstructor
public class NotificationBell extends Popover {

    private final SvgIcon bellIcon = SvgFactory.createIconFromSvg("bell.svg", SIZE_25_PX, null);
    private final Div divBell = new Div();
    private final Span spanCircleRed = new Span();
    private final Div contentAll = new Div();
    private final Div contentUnread = new Div();
    private final Span inboxCounter = new Span("0");
    private final MessageList messageListRead = new MessageList();
    private final MessageList messageListAll = new MessageList();
    private final List<MessageListItem> messageListItemUnreadList = new CopyOnWriteArrayList<>();
    private final List<MessageListItem> messageListItemAllList = new CopyOnWriteArrayList<>();

    private final SettingsDialog settingsDialog;

    @PostConstruct
    public void setup() {
        //Do nothing
    }

    public Div createNotificationBell() {

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

        super.setTarget(divBell);
        super.setWidth("340px");
        super.addThemeVariants(PopoverVariant.ARROW, PopoverVariant.LUMO_NO_PADDING);
        super.setPosition(PopoverPosition.BOTTOM);
        super.setAriaLabelledBy("notifications-heading");
        super.setModal(true);
        super.setBackdropVisible(true);
        super.addOpenedChangeListener(this::rotateTheBellToZeroDegreesIfThePopoverIsNotOpen);

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
                    settingsDialog.open("settings/notifications");
                });
            });
        });

        var headerRow = new HorizontalLayout(new H4("Notifications"), buttonMarkAllRead, buttonSettings);
        headerRow.addClassNames(LumoUtility.JustifyContent.AROUND, LumoUtility.AlignItems.CENTER);
        headerRow.getStyle().set("padding", "var(--lumo-space-m) var(--lumo-space-m) var(--lumo-space-xs)");
        super.add(headerRow);

        final TabSheet tabSheet = new TabSheet();
        tabSheet.addClassName("notifications");

        messageListRead.setItems(messageListItemUnreadList);
        contentUnread.add(messageListRead);
        contentAll.add(messageListAll);

        tabSheet.add("Unread", contentUnread);
        tabSheet.add("All", contentAll);
        super.add(tabSheet);

        return divBell;
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

}
