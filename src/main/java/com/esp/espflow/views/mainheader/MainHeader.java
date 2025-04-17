package com.esp.espflow.views.mainheader;

import com.esp.espflow.entity.event.MainHeaderToReadFlashViewEvent;
import com.esp.espflow.enums.RefreshDevicesEvent;
import com.esp.espflow.views.readflash.ReadFlashView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
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

import static com.esp.espflow.util.EspFlowConstants.RETURN_WINDOW_INNER_WIDTH;

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
    private final H1 viewTitle = new H1();
    private final HorizontalLayout rowHeaderForComponentsView = new HorizontalLayout();
    /**
     * Services
     */
    private final ApplicationEventPublisher applicationEventPublisher;
    private final NotificationBell notificationBell;

    @PostConstruct
    public void setup() {
        //Do nothing at the moment!
    }

    /**
     * @return A {@link HorizontalLayout}
     */
    public MainHeader createHeaderRow() {
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        this.rowHeaderForComponentsView.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        final HorizontalLayout rowEnd = new HorizontalLayout();
        rowEnd.add(this.rowHeaderForComponentsView, this.notificationBell.createNotificationBell());
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

    private void detectBrowserSize(final UI ui, final int width) {
        if (width < 500) {
            log.info(" < 500 read flash");
            this.applicationEventPublisher.publishEvent(new MainHeaderToReadFlashViewEvent(ui, RefreshDevicesEvent.ENABLE, width));
        } else { // width != 500
            log.info("!= 500 read flash");
            this.applicationEventPublisher.publishEvent(new MainHeaderToReadFlashViewEvent(ui, RefreshDevicesEvent.ENABLE, width));
        }
    }

    private void executeJsAndBrowserListener(UI ui) {
        ui.getPage().executeJs(RETURN_WINDOW_INNER_WIDTH)
                .then(result -> {
                    final int width = ((Double) result.asNumber()).intValue();
                    this.detectBrowserSize(ui, width);
                });
        ui.getPage().addBrowserWindowResizeListener(event -> this.detectBrowserSize(ui, event.getWidth()));
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        final UI ui = event.getUI();
        if (event.getNavigationTarget().equals(ReadFlashView.class)) {
            this.executeJsAndBrowserListener(ui);
        }
    }
}
