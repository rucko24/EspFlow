package com.esp.espflow.views.login;

import com.esp.espflow.security.AuthenticatedUser;
import com.infraleap.animatecss.Animated;
import com.infraleap.animatecss.Animated.Animation;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import static com.esp.espflow.util.EspFlowConstants.BOX_SHADOW_PROPERTY;
import static com.esp.espflow.util.EspFlowConstants.BOX_SHADOW_VALUE;
import static com.esp.espflow.util.EspFlowConstants.FRONTEND_ICON;

@PageTitle("Login")
@Route(value = "login")
@CssImport(value = "./login-view-background.css", themeFor = "vaadin-login-overlay-wrapper")
public class LoginView extends LoginOverlay implements BeforeEnterObserver {

    private final AuthenticatedUser authenticatedUser;

    public LoginView(AuthenticatedUser authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
        setAction("login");
        var image = new Image(FRONTEND_ICON, "icon");
        image.setWidth("100px");
        image.setHeight("100px");
        image.getStyle().set(BOX_SHADOW_PROPERTY, BOX_SHADOW_VALUE);
        setTitle(new Span(image));

        LoginI18n i18n = LoginI18n.createDefault();
        i18n.setHeader(new LoginI18n.Header());
        i18n.getHeader().setDescription("Login using espflow");
        i18n.setAdditionalInformation(null);
        setI18n(i18n);

        setForgotPasswordButtonVisible(false);
        setOpened(true);
        Animated.animate(this, Animation.FADE_IN);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (authenticatedUser.get().isPresent()) {
            // Already logged in
            setOpened(false);
            event.forwardTo("");
            Animated.animate(this, Animation.FADE_OUT);
        }

        setError(event.getLocation().getQueryParameters().getParameters().containsKey("error"));
    }
}
