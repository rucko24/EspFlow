package com.esp.espflow.views.about;

import com.esp.espflow.views.Layout;
import com.esp.espflow.views.MainLayout;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;
import lombok.extern.log4j.Log4j2;

@Log4j2
@UIScope
@SpringComponent
@PageTitle("test")
@Route(value = "test", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class Test extends VerticalLayout {

    final Layout content = new Layout();

    public Test() {
        setSizeFull();
        getStyle().set("border", "2px, solid red");

        final Main main = new Main();
        main.addClassNames(LumoUtility.Display.FLEX, LumoUtility.FlexDirection.ROW, LumoUtility.Height.FULL);
        final Nav nav  = new Nav();
        nav.addClassNames(LumoUtility.Display.Breakpoint.Small.FLEX, LumoUtility.FontSize.SMALL, LumoUtility.Position.STICKY);
        final Div divInsideNav = new Div(createPanel());
        divInsideNav.addClassNames(LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN,
                LumoUtility.Margin.Vertical.XLARGE, LumoUtility.Padding.Horizontal.LARGE);
        nav.add(divInsideNav);

        content.setFlexDirection(Layout.FlexDirection.COLUMN);
        content.addClassNames(LumoUtility.BoxSizing.BORDER, LumoUtility.MaxWidth.SCREEN_SMALL, LumoUtility.Padding.LARGE);
        content.add(createAnalitics("1 Analytics"));
        content.add(createAnalitics("2 Customer"));
        content.add(createAnalitics("3 Dashboard"));
        main.add(nav, content);

        add(main);
    }

    private Layout createAnalitics(String name) {
        final Layout layout = new Layout(new Div(name));
        layout.setFlexDirection(Layout.FlexDirection.COLUMN);
        return layout;
    }

    private Scroller createPanel() {
        final Accordion accordion = new Accordion();
        AccordionPanel paymentPanel = accordion.add("Payment", this.createDetail());
        paymentPanel.addThemeVariants(DetailsVariant.REVERSE, DetailsVariant.SMALL);
        AccordionPanel summary = accordion.add("Summary", this.createDetail());
        summary.addThemeVariants(DetailsVariant.REVERSE, DetailsVariant.SMALL);

        Scroller scroller = new Scroller(accordion);
        scroller.setHeightFull();
        scroller.getStyle().set("scrollbar-width", "thin");
        return scroller;
    }

    private Tabs createDetail() {
        Tab analytics = new Tab("Analytics");
        Tab customers = new Tab("Customers");
        Tab dashboards = new Tab("Dashboards");

        Tabs tabs = new Tabs(analytics, customers, dashboards);
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.setHeight("240px");
        tabs.setWidth("240px");

        tabs.addSelectedChangeListener(event -> {

            if(event.getSelectedTab().getLabel().equals("Analytics")) {
                this.content.removeAll();
                this.content.add(createAnalitics("1 Analitics"));
            } else if(event.getSelectedTab().getLabel().equals("Customers")) {
                this.content.removeAll();
                this.content.add(createAnalitics("2 Customer"));
            } else {
                this.content.removeAll();
                this.content.add("3 DashBoard");
            }
        });

        return tabs;
    }

}
