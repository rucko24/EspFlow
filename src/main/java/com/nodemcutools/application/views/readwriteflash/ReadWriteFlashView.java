package com.nodemcutools.application.views.readwriteflash;

import com.nodemcutools.application.components.console.Console;
import com.nodemcutools.application.data.service.ComPortService;
import com.nodemcutools.application.data.service.CommandService;
import com.nodemcutools.application.data.util.ResponsiveHeaderDiv;
import com.nodemcutools.application.views.MainLayout;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout.Orientation;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;

import static com.nodemcutools.application.data.util.UiToolConstants.HIDDEN;
import static com.nodemcutools.application.data.util.UiToolConstants.OVERFLOW_X;
import static com.nodemcutools.application.data.util.UiToolConstants.OVERFLOW_Y;

/**
 * @author rubn
 */
@Log4j2
@UIScope
@SpringComponent
@PageTitle("Read write flash")
@Route(value = "read-write-flash", layout = MainLayout.class)
//@RouteAlias(value = "", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@RequiredArgsConstructor
public class ReadWriteFlashView extends Div implements ResponsiveHeaderDiv {

    private final CommandService commandService;
    private final ComPortService comPortService;
    private final VerticalLayout content = new VerticalLayout();
    private final Tab readTab = new Tab(VaadinIcon.ARROW_CIRCLE_DOWN.create(), new Span("Read flash"));
    private final Tab writeTab = new Tab(VaadinIcon.ARROW_CIRCLE_UP.create(), new Span("Write flash"));
    private final Console console;
    private final SplitLayout splitLayout = new SplitLayout(Orientation.VERTICAL);

    @PostConstruct
    public void init() {
        super.setSizeFull();

        final Tabs tabs = new Tabs(readTab, writeTab);
//        tabs.addThemeVariants(TabsVariant.LUMO_EQUAL_WIDTH_TABS);
        tabs.addSelectedChangeListener(event ->
                setContent(event.getSelectedTab())
        );

        this.content.setSpacing(false);
        setContent(tabs.getSelectedTab());
        super.getStyle().set("display", "flex");
        super.getStyle().set("flex-direction", "column");

        this.splitLayout.setSplitterPosition(60);
        this.splitLayout.setSizeFull();
        this.splitLayout.addToPrimary(tabs, content);
        this.splitLayout.getStyle().set(OVERFLOW_Y, HIDDEN);

        this.splitLayout.addToSecondary(console.getConsole());

        this.splitLayout.getStyle().set(OVERFLOW_X, HIDDEN);
        splitLayout.getPrimaryComponent().getElement().getStyle().set(OVERFLOW_X, HIDDEN);
        splitLayout.getSecondaryComponent().getElement().getStyle().set(OVERFLOW_X, HIDDEN);

        super.add(splitLayout);
    }

    private void setContent(final Tab tab) {
        content.removeAll();

        if (tab.equals(readTab)) {
            content.add(new Paragraph("read flash"));
        } else if (tab.equals(writeTab)) {
            content.add(new Paragraph("write flash"));
        } else {

        }
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {

    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        if (attachEvent.isInitialAttach()) {

        }
    }
}
