package com.nodemcuui.tool.views.readflash;

import com.nodemcuui.tool.data.service.EsptoolService;
import com.nodemcuui.tool.data.util.NotificationBuilder;
import com.nodemcuui.tool.data.util.ResponsiveHeaderDiv;
import com.nodemcuui.tool.data.util.console.ConsoleOutPut;
import com.nodemcuui.tool.views.MainLayout;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout.Orientation;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.FlexDirection;
import com.vaadin.flow.theme.lumo.LumoUtility.JustifyContent;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

import java.util.stream.Stream;

import static com.nodemcuui.tool.data.util.UiToolConstants.HIDDEN;
import static com.nodemcuui.tool.data.util.UiToolConstants.OVERFLOW_X;
import static com.nodemcuui.tool.data.util.UiToolConstants.OVERFLOW_Y;

/**
 * @author rubn
 */
@Log4j2
@UIScope
@SpringComponent
@PageTitle("Read flash")
@Route(value = "read-flash", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@RequiredArgsConstructor
public class ReadFlashView extends Div implements ResponsiveHeaderDiv {

    private final EsptoolService esptoolService;
    private final EspDevicesCarousel espDevicesCarousel = new EspDevicesCarousel();

    private final VerticalLayout content = new VerticalLayout();
    private final ProgressBar progressBar = new ProgressBar();
    /**
     * Console output
     */
    private final ConsoleOutPut consoleOutPut = new ConsoleOutPut();
    private final HorizontalLayout footer = new HorizontalLayout();

    private final Span spanTotalDevices = new Span("Total devices: ");
    private final Span spanTotalDevicesValue = new Span();

    @PostConstruct
    public void init() {
        super.setSizeFull();
        super.getStyle().set("display", "flex");
        super.getStyle().set("flex-direction", "column");

        final SplitLayout splitLayout = getSplitLayout();
        super.add(splitLayout);
    }

    private SplitLayout getSplitLayout() {
        this.progressBar.setVisible(false);
        this.progressBar.setIndeterminate(true);
        this.content.setSpacing(false);
        this.content.addClassNames(AlignItems.CENTER, JustifyContent.CENTER);
        this.content.add(progressBar);

        final var splitLayout = new SplitLayout(Orientation.VERTICAL);
        splitLayout.setSplitterPosition(60);
        splitLayout.setSizeFull();
        splitLayout.addToPrimary(content);
        splitLayout.getStyle().set(OVERFLOW_Y, HIDDEN);

        final var footer = this.getFooter();
        //consoleOutPut.removeClassName(Bottom.SMALL);
        //consoleOutPut.getStyle().set(OVERFLOW_Y, "unset");

        final var divRowToSecondary = new Div(consoleOutPut);
        divRowToSecondary.addClassNames(Display.FLEX, FlexDirection.ROW);
        divRowToSecondary.getStyle().set(OVERFLOW_Y, HIDDEN);

        //verticalLayoutToSecondary.getStyle().set("background", "linear-gradient(var(--lumo-shade-5pct), var(--lumo-shade-5pct))");
        splitLayout.addToSecondary(divRowToSecondary, footer);

        splitLayout.getStyle().set(OVERFLOW_X, HIDDEN);
        splitLayout.getPrimaryComponent().getElement().getStyle().set(OVERFLOW_X, HIDDEN);
        splitLayout.getSecondaryComponent().getElement().getStyle().set(OVERFLOW_X, HIDDEN);

        return splitLayout;
    }

    /**
     * @return HorizontalLayout
     */
    private HorizontalLayout getFooter() {
        final Div div = new Div(this.spanTotalDevices, this.spanTotalDevicesValue);
        div.getElement().setAttribute("theme", "badge");
//        final Div div2 = new Div(this.decimalSize);
//        final Div div3 = new Div(this.hexSize);
        footer.setWidthFull();
        footer.setAlignItems(Alignment.CENTER);
        footer.setJustifyContentMode(JustifyContentMode.AROUND);
        footer.add(div);
        Stream.of(this.spanTotalDevices, this.spanTotalDevicesValue)
                .forEach(span -> {
                    span.getStyle().set("font-size", "var(--lumo-font-size-xs)");
                    span.addClassName("row-span-flash-size-footer");
                });
        return footer;
    }

    @SneakyThrows
    private void showDetectedDevices(final UI ui) {
        //FIXME shot total devices
//        this.esptoolService.readAllDevices()
//                .doOnError(error -> {
//                    ui.access(() -> {
//                        log.info("Error: {}", error);
//                        NotificationBuilder.builder()
//                                .withText("Devices: " + error)
//                                .withPosition(Position.MIDDLE)
//                                .withDuration(3000)
//                                .withIcon(VaadinIcon.WARNING)
//                                .withThemeVariant(NotificationVariant.LUMO_ERROR)
//                                .make();
//                    });
//                })
//                .count()
//                .subscribe(totalDevices -> {
//                    ui.access(() -> this.spanTotalDevicesValue.setText(" " + totalDevices));
//                });

        this.progressBar.setVisible(true);
        this.esptoolService.readAllDevices()
                .doOnError(error -> {
                    ui.access(() -> {
                        this.progressBar.setVisible(false);
                        log.info("Error: {}", error);
                        NotificationBuilder.builder()
                                .withText("Error reading the microcontroller " + error)
                                .withPosition(Position.MIDDLE)
                                .withDuration(3000)
                                .withIcon(VaadinIcon.WARNING)
                                .withThemeVariant(NotificationVariant.LUMO_ERROR)
                                .make();
                    });
                })
                .doOnComplete(() -> {
                    ui.access(() -> {
                        this.progressBar.setVisible(false);
                        espDevicesCarousel.createSlides();
                        this.content.add(espDevicesCarousel);
                    });
                })
                .subscribe(espDeviceInfo -> {
                    ui.access(() -> {
                        ShowDevices.builder()
                                .withEspDevicesCarousel(espDevicesCarousel)
                                .withEsptoolService(esptoolService)
                                .withEspDeviceInfo(espDeviceInfo)
                                .withConsoleOutStage(consoleOutPut)
                                .withUi(ui)
                                .createSlides()
                                .make();
                    });
                });
    }


    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        if (attachEvent.isInitialAttach()) {
            super.onAttach(attachEvent);
            final UI ui = attachEvent.getUI();
            this.showDetectedDevices(ui);
        }
    }
}
