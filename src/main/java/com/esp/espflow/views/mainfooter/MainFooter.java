package com.esp.espflow.views.mainfooter;

import com.esp.espflow.dto.CurrentThemeDto;
import com.esp.espflow.entity.User;
import com.esp.espflow.security.AuthenticatedUser;
import com.esp.espflow.service.respository.impl.CurrentThemeService;
import com.esp.espflow.util.svgfactory.SvgFactory;
import com.esp.espflow.views.settings.SettingsDialog;
import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.Shortcuts;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.Lumo;
import com.vaadin.flow.theme.lumo.LumoUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

import static com.esp.espflow.util.EspFlowConstants.BLACK_TO_WHITE_ICON;
import static com.esp.espflow.util.EspFlowConstants.CURSOR_POINTER;
import static com.esp.espflow.util.EspFlowConstants.ICONS_RESPONSIVE_SIZE;
import static com.esp.espflow.util.EspFlowConstants.SETTINGS;
import static com.esp.espflow.util.EspFlowConstants.SETTINGS_SHARP;

/**
 * The Main Footer for the Applayout Drawer
 */
@Log4j2
@RequiredArgsConstructor
@SpringComponent
@UIScope
@PreserveOnRefresh
public class MainFooter {

    private final Span spanDarkOrLight = new Span();
    private final ToggleButton toggleButtonTheme = new ToggleButton();
    private final Icon moonO = VaadinIcon.MOON_O.create();
    /**
     * Services
     */
    private final AuthenticatedUser authenticatedUser;
    private final SettingsDialog settingsDialog;
    private final CurrentThemeService currentThemeService;
    /**
     * Mutable field
     */
    private String density = "";

    // Load from db
    public void loadThemeFromDatabase() {
        var currentTheme = this.currentThemeService.getActiveTheme();
        boolean isDark = currentTheme != null && Lumo.DARK.equals(currentTheme.getCurrentTheme());
        toggleButtonTheme.setValue(isDark);
        this.applyTheme(isDark, false);
        toggleButtonTheme.addValueChangeListener(event -> {
            if (event.isFromClient()) {
                this.applyTheme(event.getValue(), true);
            }
        });
    }

    /**
     * @return A Custom Footer
     */
    public Footer createFooter() {
        final Footer footer = new Footer();
        footer.addClassNames("app-nav-footer");

        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();

            Avatar avatar = new Avatar(user.getName(), user.getProfilePictureUrl());
            avatar.setThemeName("xsmall");
            avatar.getElement().setAttribute("tabindex", "-1");

            MenuBar userMenu = new MenuBar();
            userMenu.setThemeName("tertiary-inline contrast");
            MenuItem userName = userMenu.addItem("");

            userName.getStyle().setCursor(CURSOR_POINTER);
            Div div = new Div();
            div.add(avatar);
            div.add(user.getName());
            div.add(new Icon("lumo", "dropdown"));
            div.getElement().getStyle().set("display", "flex");
            div.getElement().getStyle().set("align-items", "center");
            div.getElement().getStyle().set("gap", "var(--lumo-space-s)");
            userName.add(div);

            var itemSizeMode = userName.getSubMenu().addItem(StringUtils.EMPTY);
            itemSizeMode.setKeepOpen(true);
            itemSizeMode.getStyle().setHeight("40px");
            itemSizeMode.addComponentAsFirst(this.sizeMode(footer));

            var itemTheme = userName.getSubMenu().addItem(StringUtils.EMPTY);
            itemTheme.setKeepOpen(true);
            itemTheme.getStyle().setHeight("40px");
            itemTheme.addComponentAsFirst(this.changeTheme());

            final HorizontalLayout rowForSettings = new HorizontalLayout();
            rowForSettings.addClassNames(LumoUtility.Display.FLEX, LumoUtility.Width.FULL, LumoUtility.FlexDirection.ROW, LumoUtility.JustifyContent.BETWEEN, LumoUtility.AlignItems.CENTER);
            final Span span = new Span("Settings...");
            span.addClassName(LumoUtility.Margin.Left.MEDIUM);
            final Span shorcutSettings = new Span("Ctrl+Alt+S");
            shorcutSettings.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);
            rowForSettings.add(span, shorcutSettings);

            userName.getSubMenu().addItem(rowForSettings, event -> {
                footer.getUI().ifPresent(ui -> {
                    ui.getPage().fetchCurrentURL(url -> {
                        log.info("fetchCurrentURL {}", url);
                        String urlWithParameters = url.getPath().concat(SETTINGS_SHARP);
                        ui.getPage().getHistory().replaceState(null, urlWithParameters);
                        settingsDialog.open(SETTINGS);
                    });
                });
            }).addComponentAsFirst(VaadinIcon.COG.create());
            userName.getSubMenu().add(new Hr());

            final Span spanSignOut = new Span("Sign out");
            spanSignOut.addClassName(LumoUtility.Margin.Left.MEDIUM);
            final MenuItem sigOutItem = userName.getSubMenu().addItem(spanSignOut, e -> authenticatedUser.logout());
            var iconSigout = SvgFactory.createIconFromSvg("signout.svg", "20px", null);
            iconSigout.addClassName(BLACK_TO_WHITE_ICON);
            sigOutItem.addComponentAsFirst(iconSigout);

            Shortcuts.addShortcutListener(userName, shortcutEvent -> {
                footer.getUI().ifPresent(ui -> {
                    ui.getPage().fetchCurrentURL(url -> {
                        String urlWithParameters = url.getPath().concat(SETTINGS_SHARP);
                        ui.getPage().getHistory().replaceState(null, urlWithParameters);
                        settingsDialog.open(SETTINGS);
                        log.info("fetchCurrentURL from shortcut {}{}", url, SETTINGS_SHARP);
                    });
                });
            }, Key.KEY_S, KeyModifier.CONTROL, KeyModifier.ALT);

            footer.add(userMenu, settingsDialog);
        } else {
            Anchor loginLink = new Anchor("login", "Sign in");
            footer.add(loginLink);
        }

        return footer;
    }

    public HorizontalLayout sizeMode(Footer footer) {
        // Density
        final RadioButtonGroup<String> densityRadioButtomGroup = new RadioButtonGroup<>();
        densityRadioButtomGroup.setTooltipText("Compact or default mode");
        densityRadioButtomGroup.setWidthFull();
        densityRadioButtomGroup.setItems("default", "compact");
        densityRadioButtomGroup.setValue("default");
        densityRadioButtomGroup.setWidthFull();
        densityRadioButtomGroup.getChildren().forEach(component -> {
            component.getElement().getThemeList().add("toggle badge pill contrast");
            component.addClassName(LumoUtility.Margin.Right.MEDIUM);
        });

        densityRadioButtomGroup.addValueChangeListener(event -> {
            final String fontSize = event.getValue().equals("compact") ? "compact" : "default";
            this.setDensity(footer, fontSize);
        });

        final ToggleButton toggleButtonFontSize = new ToggleButton();
        toggleButtonFontSize.setTooltipText("Font size");
        toggleButtonFontSize.addValueChangeListener(e -> {
            final String fontSize = toggleButtonFontSize.getValue() ? "compact" : "default";
            this.setDensity(footer, fontSize);
        });

        final Span spanFontSize = new Span("Compact or default");
        spanFontSize.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.SMALL);
        final HorizontalLayout rowToggleSpan = new HorizontalLayout(toggleButtonFontSize, spanFontSize);
        rowToggleSpan.addClassNames(LumoUtility.Display.FLEX, LumoUtility.FlexDirection.ROW,
                LumoUtility.Width.FULL, LumoUtility.JustifyContent.BETWEEN, LumoUtility.AlignItems.CENTER);
        final HorizontalLayout rowSizeMode = new HorizontalLayout(spanDarkOrLight, rowToggleSpan);
        rowSizeMode.addClassNames(LumoUtility.Width.FULL, LumoUtility.Gap.SMALL);
        rowSizeMode.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.END);
        return rowSizeMode;
    }

    private HorizontalLayout changeTheme() {
        toggleButtonTheme.addClassName(LumoUtility.Margin.Left.XSMALL);
        moonO.setSize("20px");
        moonO.addClassName(ICONS_RESPONSIVE_SIZE);
        spanDarkOrLight.add(moonO);
        spanDarkOrLight.getStyle().setMarginLeft("2px");
        spanDarkOrLight.getStyle().setMarginBottom("2px");
        toggleButtonTheme.setTooltipText("Change to dark theme");

        final Span spanTheme = new Span("Theme");
        spanTheme.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.SMALL);
        final HorizontalLayout rowToggleSpan = new HorizontalLayout(toggleButtonTheme, spanTheme);
        rowToggleSpan.addClassNames(LumoUtility.Display.FLEX, LumoUtility.FlexDirection.ROW, LumoUtility.Width.FULL, LumoUtility.JustifyContent.BETWEEN,
                LumoUtility.AlignItems.CENTER);
        final HorizontalLayout rowTogle = new HorizontalLayout(spanDarkOrLight, rowToggleSpan);
        rowTogle.addClassNames(LumoUtility.Width.FULL, LumoUtility.Gap.SMALL);
        rowTogle.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.END);
        return rowTogle;
    }

    private void applyTheme(boolean isDark, boolean saveToDatabase) {
        final ThemeList themeList = UI.getCurrent().getElement().getThemeList();
        if (isDark) {
            toggleButtonTheme.setTooltipText("Change to light theme");
            spanDarkOrLight.removeAll();
            var icon = SvgFactory.createIconFromSvg("brightness.svg", "20px", "");
            icon.addClassName(BLACK_TO_WHITE_ICON);
            spanDarkOrLight.add(icon);
            themeList.add(Lumo.DARK);
        } else {
            toggleButtonTheme.setTooltipText("Change to dark theme");
            spanDarkOrLight.removeAll();
            spanDarkOrLight.add(moonO);
            themeList.remove(Lumo.DARK);
        }
        if (saveToDatabase) {
            var dto = CurrentThemeDto
                    .builder()
                    .currentTheme(isDark ? Lumo.DARK : Lumo.LIGHT)
                    .build();
            this.currentThemeService.update(dto);
        }
    }

    private void setDensity(Footer footer, String density) {
        this.density = density.equals("compact") ? "compact" : "";
        updateTheme(footer);
    }

    private void updateTheme(Footer footer) {
        var js = "document.documentElement.setAttribute('theme', $0)";
        var currentTheme = Lumo.LIGHT + " " + this.density;
        footer.getElement().executeJs(js, currentTheme);
    }

}
