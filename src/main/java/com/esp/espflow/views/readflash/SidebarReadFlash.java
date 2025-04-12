package com.esp.espflow.views.readflash;

import com.esp.espflow.util.animations.AnimationsUtils;
import com.esp.espflow.views.Layout;
import com.infraleap.animatecss.Animated;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasTheme;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Shortcuts;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Section;
import com.vaadin.flow.component.popover.Popover;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.time.Duration;

/**
 * @author rubn
 */
@Log4j2
@UIScope
@SpringComponent
@RequiredArgsConstructor
public class SidebarReadFlash extends Popover implements HasEnabled, HasTheme, AnimationsUtils {

    private final Section sidebar = new Section();

    @PostConstruct
    public void setup() {
        super.addClassNames("popover-sidebar");
        super.setCloseOnEsc(true);
        // IMPORTANTE: desactivar el cierre automático al hacer clic fuera
        super.setCloseOnOutsideClick(false);
        super.setBackdropVisible(true);
        this.add(sidebar);
        this.sidebar.setId("sidebar");
        this.sidebar.addClassNames(LumoUtility.Background.BASE,
                LumoUtility.BoxShadow.MEDIUM,
                LumoUtility.Display.FLEX,
                LumoUtility.FlexDirection.COLUMN,
                LumoUtility.Overflow.HIDDEN,
                LumoUtility.Position.FIXED, "bottom-0", "top-0", "transition-all", "z-10");
        this.sidebar.setMaxWidth(100, Unit.PERCENTAGE);
        this.sidebar.setWidth(480, Unit.PIXELS);
        this.closeSidebar();
        Shortcuts.addShortcutListener(this.sidebar, shortcutEvent -> this.closeSidebar(), Key.ESCAPE);
    }

    public void createSection(final Div leftFormForAddress) {
        final H2 title = new H2("Configure");
        title.addClassNames(LumoUtility.FontSize.MEDIUM);

        final Button close = new Button(LineAwesomeIcon.TIMES_SOLID.create(), e -> closeSidebar());
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        close.addClassName(LumoUtility.Padding.Right.NONE);
        close.setAriaLabel("Close sidebar");
        close.setTooltipText("Close sidebar - ESC");

        final Layout header = new Layout(title, close);
        header.addClassNames(LumoUtility.Padding.End.LARGE, LumoUtility.Padding.Start.LARGE, LumoUtility.Padding.Vertical.SMALL);
        header.setAlignItems(Layout.AlignItems.CENTER);
        header.setJustifyContent(Layout.JustifyContent.BETWEEN);

        final Layout form = new Layout(leftFormForAddress);
        form.addClassNames(LumoUtility.Padding.Horizontal.LARGE);
        form.setFlexDirection(Layout.FlexDirection.COLUMN);

        this.sidebar.add(header, leftFormForAddress);
        //this.sidebar.addClassNames("sidebar-read-flash");

    }

    public void toggleSidebar() {
        if (this.sidebar.isEnabled()) {
            this.closeSidebar();
        } else {
            this.openSidebar();
        }
    }

    private void openSidebar() {
        super.setModal(true);
        super.getElement().executeJs(
                """
                        setTimeout(function() {
                          document.addEventListener('click', function(e) {
                            var sidebar = document.getElementById('sidebar');
                            if (sidebar && !sidebar.contains(e.target)) {
                              $0.$server.closeFromOutsideClick();
                            }
                          }, { once: true });
                        }, 100);
                        """, this);
        Animated.removeAnimations(this);
        Animated.animate(this.sidebar, Animated.Animation.FADE_IN_RIGHT);
        this.sidebar.addClassNames("end-0");
        this.sidebar.removeClassNames("-end-full");
        this.sidebar.setEnabled(true);
        super.open();
    }

    public void closeSidebar() {
        Animated.animate(this.sidebar, Animated.Animation.FADE_OUT_RIGHT);
        this.removesClassWithDelay(() -> {
            this.sidebar.addClassNames("-end-full");
            this.sidebar.removeClassName("end-0");
            this.sidebar.setEnabled(false);
            super.setModal(false);
            super.close();
        }, this, Duration.ofMillis(1500));
    }

    @ClientCallable
    public void closeFromOutsideClick() {
        this.closeSidebar();
    }

}
