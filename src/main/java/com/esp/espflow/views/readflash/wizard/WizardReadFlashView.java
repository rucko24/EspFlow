package com.esp.espflow.views.readflash.wizard;

import com.esp.espflow.entity.dto.WizardEspDto;
import com.esp.espflow.service.respository.impl.WizardEspService;
import com.esp.espflow.util.EspFlowConstants;
import com.esp.espflow.views.Layout;
import com.infraleap.animatecss.Animated;
import com.infraleap.animatecss.Animated.Animation;
import com.infraleap.animatecss.Animated.Modifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.Scroller.ScrollDirection;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.dom.Style.AlignSelf;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin.Bottom;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin.Right;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin.Top;
import com.vaadin.flow.theme.lumo.LumoUtility.Width;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.stream.Stream;

import static com.esp.espflow.util.EspFlowConstants.AVATAR_STEP_ACTIVE;
import static com.esp.espflow.util.EspFlowConstants.AVATAR_STEP_INACTIVE;
import static com.esp.espflow.util.EspFlowConstants.BOX_SHADOW_PROPERTY;
import static com.esp.espflow.util.EspFlowConstants.BOX_SHADOW_VALUE;
import static com.esp.espflow.util.EspFlowConstants.FRONTEND_IMAGES_CUSTOM;
import static com.esp.espflow.util.EspFlowConstants.INNER_HTML;
import static com.esp.espflow.util.EspFlowConstants.STEP1;
import static com.esp.espflow.util.EspFlowConstants.STEP2;
import static com.esp.espflow.util.EspFlowConstants.STEP3;
import static com.esp.espflow.util.EspFlowConstants.WIZARD_READ_FLASH_ESP_VIEW;
import static com.infraleap.animatecss.Animated.Modifier.INFINITE;

/**
 * No modal
 *
 * @author rub'n
 */
@UIScope
@SpringComponent
@Log4j2
@RolesAllowed("ADMIN")
@RequiredArgsConstructor
public class WizardReadFlashView extends Dialog implements BeforeEnterObserver {

    private HorizontalLayout step1 = new HorizontalLayout();
    private HorizontalLayout step2 = new HorizontalLayout();
    private HorizontalLayout step3 = new HorizontalLayout();

    private final Avatar avatar1 = new Avatar();
    private final Avatar avatar2 = new Avatar();
    private final Avatar avatar3 = new Avatar();

    private final Button next = new Button("Next");
    private final Button previous = new Button("Previous");
    private final Button hideButton = new Button("Hide");
    /**
     * It will contain each step content
     */
    private final Main mainContent = new Main();
    /**
     * The repository to save the status of this dialog when hiding it
     */
    private final WizardEspService wizardEspService;
    /**
     * This variable will be updated with each next or previous step.
     */
    private String sNext = STEP1;

    @PostConstruct
    public void init() {

        this.initConfiguration();
    }

    /**
     * We load the initial configuration of the Wizard
     */
    private void initConfiguration() {
        super.setMaxWidth("740px");
        super.setMaxHeight("500px");
        super.setHeight("500px");

        /*
         * Allows that when closing the dialog, we remove the portion aka fragment of the uri after the and leaving the original path.
         */
        super.addOpenedChangeListener(event -> {
            if (!event.getSource().isOpened()) {
                getUI().ifPresent(ui -> {
                    ui.getPage().fetchCurrentURL(url -> {
                        ui.getPage().getHistory().replaceState(null, url.getPath());
                    });
                });
            }
        });

        super.setCloseOnOutsideClick(true);
        this.configureHeader();
        this.configureFooter();

        final Hr hr = new Hr();
        hr.addClassName("hr-header-settings");

        super.add(hr, this.createReadFlashDevices());
        super.addClassName("settings-content-dialog");
    }

    /**
     * Configure the dialog header here
     */
    private void configureHeader() {
        this.step1 = this.rowStepAvatar(avatar1, "1", "1", new H4("Welcome"));
        this.step2 = this.rowStepAvatar(avatar2, "2", "2", new H4("Refresh devices"));
        this.step3 = this.rowStepAvatar(avatar3, "3", "3", new H4("Read and Download flash"));
        step3.remove(step3.getComponentAt(2));

        Stream.of(step1, step2, step3, avatar1, avatar2, avatar3)
                .forEach(items -> items.getStyle().setCursor("pointer"));

        final Div divHeader = new Div(step1, step2, step3);
        divHeader.addClassNames(Display.FLEX, LumoUtility.FlexDirection.ROW,
                Width.FULL, LumoUtility.JustifyContent.CENTER, Top.MEDIUM);

        step1.addClickListener(event -> {
            this.sNext = STEP1;
            this.next.setText("Next");
            this.previous.setVisible(false);
            this.mainContent.removeAll();
            this.mainContent.add(refreshDevices());
            avatar2.removeClassNames(AVATAR_STEP_ACTIVE);
            avatar2.addClassName(AVATAR_STEP_INACTIVE);
            avatar3.removeClassNames(AVATAR_STEP_ACTIVE);
            avatar3.addClassName(AVATAR_STEP_INACTIVE);
        });

        step2.addClickListener(event -> {
            this.sNext = STEP2;
            this.previous.setVisible(true);
            this.next.setText("Next");
            this.mainContent.removeAll();
            this.mainContent.add(incorrectFlashSize());
            avatar2.removeClassNames(AVATAR_STEP_INACTIVE);
            avatar2.addClassName(AVATAR_STEP_ACTIVE);
            avatar3.removeClassNames(AVATAR_STEP_ACTIVE);
            avatar3.addClassName(AVATAR_STEP_INACTIVE);
        });

        step3.addClickListener(event -> {
            this.sNext = STEP3;
            this.next.setText("Continue...");
            this.previous.setVisible(true);
            this.mainContent.removeAll();
            this.mainContent.add(createDownloadFlashContent());
            avatar1.removeClassNames(AVATAR_STEP_INACTIVE);
            avatar1.addClassName(AVATAR_STEP_ACTIVE);
            avatar2.removeClassNames(AVATAR_STEP_INACTIVE);
            avatar2.addClassName(AVATAR_STEP_ACTIVE);
            avatar3.removeClassNames(AVATAR_STEP_INACTIVE);
            avatar3.addClassName(AVATAR_STEP_ACTIVE);
        });

        super.getHeader().add(divHeader);
    }

    /**
     * Configure the footer here, and we add the listener of the buttons as well
     */
    private void configureFooter() {
        this.previous.setVisible(false);
        this.next.addClickListener(event -> {
            next.setText("Next");
            getUI().ifPresent(ui -> {
                ui.getPage().fetchCurrentURL(url -> {
                    ui.getPage().getHistory().replaceState(null, url.getPath());
                });
            });
            if (sNext.equals(STEP1)) {
                sNext = STEP2;
                next.setText("Next");
                this.mainContent.removeAll();
                this.mainContent.add(incorrectFlashSize());
                avatar2.removeClassNames(AVATAR_STEP_INACTIVE);
                avatar2.addClassName(AVATAR_STEP_ACTIVE);
                avatar3.removeClassNames(AVATAR_STEP_ACTIVE);
                avatar3.addClassName(AVATAR_STEP_INACTIVE);
                previous.setVisible(true);
                Animated.animate(previous, Animation.FADE_IN);
            } else if (sNext.equals(STEP2)) {
                this.mainContent.removeAll();
                this.mainContent.add(createDownloadFlashContent());
                previous.setVisible(true);
                avatar2.removeClassNames(AVATAR_STEP_INACTIVE);
                avatar2.addClassName(AVATAR_STEP_ACTIVE);
                avatar3.removeClassNames(AVATAR_STEP_INACTIVE);
                avatar3.addClassName(AVATAR_STEP_ACTIVE);
                Animated.removeAnimations(previous);
                sNext = STEP3;
                next.setText("Continue...");
            } else if (sNext.equals("step3")) {
                sNext = STEP1;
                super.close();
            }
        });
        next.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        this.previous.addClickListener(event -> {
            getUI().ifPresent(ui -> {
                ui.getPage().fetchCurrentURL(url -> {
                    ui.getPage().getHistory().replaceState(null, url.getPath());
                });
            });
            if (sNext.equals(STEP2)) {
                sNext = STEP1;
                this.mainContent.removeAll();
                this.mainContent.add(this.refreshDevices());
                avatar2.removeClassNames(AVATAR_STEP_ACTIVE);
                avatar2.addClassName(AVATAR_STEP_INACTIVE);
                avatar3.removeClassNames(AVATAR_STEP_ACTIVE);
                avatar3.addClassName(AVATAR_STEP_INACTIVE);
                previous.setVisible(false);
                Animated.animate(previous, Animation.FADE_IN);
            } else if (sNext.equals(STEP3)) {
                sNext = STEP2;
                this.mainContent.removeAll();
                this.mainContent.add(incorrectFlashSize());
                previous.setVisible(true);
                Animated.animate(previous, Animation.FADE_IN);
                avatar2.removeClassNames(AVATAR_STEP_INACTIVE);
                avatar2.addClassName(AVATAR_STEP_ACTIVE);
                avatar3.removeClassNames(AVATAR_STEP_ACTIVE);
                avatar3.addClassName(AVATAR_STEP_INACTIVE);
                Animated.removeAnimations(previous);
                next.setText("Next");
            } else if (sNext.equals(STEP1)) {
                sNext = STEP1;
                previous.setVisible(false);
            }
        });
        hideButton.setTooltipText("Hide this dialog");
        hideButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        hideButton.getStyle().set("margin-right", "auto");
        hideButton.addClickListener(event -> {
            this.wizardEspService.findByName(WIZARD_READ_FLASH_ESP_VIEW)
                    .ifPresent(entityPresent -> {
                        final WizardEspDto wizardEspDto = WizardEspDto.builder()
                                .id(entityPresent.id())
                                .isWizardEnabled(false)
                                .name(WIZARD_READ_FLASH_ESP_VIEW)
                                .build();
                        this.wizardEspService.save(wizardEspDto);
                    });
            super.close();
        });
        super.getFooter().add(hideButton);

        final HorizontalLayout rowEnd = new HorizontalLayout(previous, next);
        rowEnd.setVerticalComponentAlignment(Alignment.CENTER);
        rowEnd.setAlignSelf(Alignment.END, rowEnd);
        super.getFooter().add(rowEnd);

    }

    /**
     * Build the circles for the steps
     *
     * @param avatar
     * @param numberStep the step
     * @param tooltip    for this avatar
     * @param header     to describe the passage
     * @return A {@link HorizontalLayout}
     */
    private HorizontalLayout rowStepAvatar(final Avatar avatar, final String numberStep,
                                           final String tooltip, final H4 header) {
        avatar.setName(numberStep);
        Tooltip.forComponent(avatar).setText(tooltip);
        avatar.setThemeName(AvatarVariant.LUMO_SMALL.getVariantName());
        avatar.addClassName(EspFlowConstants.BOX_SHADOW_VAADIN_BUTTON);
        if (numberStep.equals("2") || numberStep.equals("3")) {
            avatar.addClassName(AVATAR_STEP_INACTIVE);
        } else {
            avatar.getStyle().set("background", "var(--lumo-primary-color)");
            avatar.getStyle().set("color", "var(--lumo-primary-contrast-color)");
        }

        final Hr hr = new Hr();
        hr.addClassName(Right.MEDIUM);
        hr.setWidth("50px");
        final var row = new HorizontalLayout(avatar, header, hr);
        row.setSpacing(true);
        row.addClassNames(AlignItems.CENTER, LumoUtility.JustifyContent.START);
        row.setVerticalComponentAlignment(Alignment.CENTER, hr);
        return row;
    }

    /**
     * Layout with nav and content, first step
     *
     * @return A {@link Component}
     */
    private Main createReadFlashDevices() {
        mainContent.addClassNames(Display.FLEX, LumoUtility.FlexDirection.ROW, LumoUtility.Height.FULL);
        mainContent.add(this.refreshDevices());
        return mainContent;
    }


    /**
     * The welcome content
     *
     * @return A {@link Component}
     */
    private Component refreshDevices() {
        final StepRefreshDevicesContent stepRefreshDevices = new StepRefreshDevicesContent();
        final Layout layout = new Layout(stepRefreshDevices);
        layout.setFlexDirection(Layout.FlexDirection.COLUMN);
        layout.setColumnGap(Layout.Gap.MEDIUM);
        layout.setColumnSpan(Layout.ColumnSpan.COLUMN_SPAN_FULL, stepRefreshDevices);
        return layout;
    }

    /**
     * The content for step 2
     *
     * @return A {@link Component}
     */
    private Component incorrectFlashSize() {
        final H2 titleIncorrectSizeAddress = new H2("Incorrect size address");
        titleIncorrectSizeAddress.addClassNames(LumoUtility.FontSize.XLARGE, LumoUtility.Margin.Top.MEDIUM);
        titleIncorrectSizeAddress.setId(titleIncorrectSizeAddress.getText().replace(" ", "-").toLowerCase());

        final Component descriptionIncorrectFinalFlashSize = showImageWithInformationAboutToggleButton();

        final Hr separator = this.createSeparator();

        final H2 titlePermissionDenied = new H2("Port failure: E.g. '/dev/ttyUSB2'");
        titlePermissionDenied.addClassNames(LumoUtility.FontSize.XLARGE, LumoUtility.Margin.Top.MEDIUM);
        titlePermissionDenied.setId(titlePermissionDenied.getText().replace(" ", "-").toLowerCase());

        final Div divPortFailure = new Div("Port failure: /dev/ttyUSB2");
        divPortFailure.setHeight("25px");
        divPortFailure.getStyle().set("color", "red");
        divPortFailure.getElement().setAttribute("theme", "badge");
        divPortFailure.addClassName(EspFlowConstants.BOX_SHADOW_VAADIN_BUTTON);

        final HorizontalLayout rowPermisson = new HorizontalLayout(divPortFailure, titlePermissionDenied);
        rowPermisson.setVerticalComponentAlignment(Alignment.CENTER, divPortFailure);

        final Paragraph descriptionPermissionDeniedPort = new Paragraph();
        descriptionPermissionDeniedPort.getElement().setProperty(INNER_HTML, "If when executing the <strong>Refresh device button</strong> we have permissions problems, in the bottom bar we will have in red the ports with error, we can use <strong>right click</strong> and give the necessary permissions, <strong>no password will be saved or sent anywhere<strong>.");
        descriptionPermissionDeniedPort.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);

        final UnorderedList unorderedListPorts = new UnorderedList();
        final ListItem flashIdItem = new ListItem("FreeBSD E.g. port /dev/cuaU0");
        final ListItem writeIdItem = new ListItem("MacOs E.g. port /dev/tty.usbserial-0001");
        unorderedListPorts.add(flashIdItem, writeIdItem);
        unorderedListPorts.addClassNames(Top.SMALL, Bottom.LARGE, LumoUtility.FontSize.SMALL,
                LumoUtility.TextColor.SECONDARY);

        final Image changePermissionsImage = new Image(FRONTEND_IMAGES_CUSTOM + "read-flash-permissions.gif", "read-flash-permissions");
        changePermissionsImage.setWidth("80%");
        changePermissionsImage.getStyle().set(BOX_SHADOW_PROPERTY, BOX_SHADOW_VALUE);

        Stream.of(changePermissionsImage)
                .forEach(image -> image.getStyle().setAlignSelf(AlignSelf.CENTER));

        final Layout layout = new Layout(titleIncorrectSizeAddress, descriptionIncorrectFinalFlashSize, separator, rowPermisson, descriptionPermissionDeniedPort, unorderedListPorts, changePermissionsImage);
        layout.setFlexDirection(Layout.FlexDirection.COLUMN);
        layout.setColumnGap(Layout.Gap.MEDIUM);
        layout.setColumnSpan(Layout.ColumnSpan.COLUMN_SPAN_FULL, titleIncorrectSizeAddress, descriptionIncorrectFinalFlashSize, separator, changePermissionsImage, descriptionPermissionDeniedPort);

        final Scroller scroller = new Scroller(layout);
        scroller.getStyle().set("scrollbar-width", "thin");
        scroller.setScrollDirection(ScrollDirection.VERTICAL);

        return scroller;
    }

    /**
     * Displays an image with an arrow with HEART_BEAT, INFINITE effect, pointing to the toggle button
     *
     * @return A {@link Component} with information about the toggle button
     */
    private Component showImageWithInformationAboutToggleButton() {
        final Image image = new Image(FRONTEND_IMAGES_CUSTOM + "enable-toggle-button.png", "alt");
        image.setWidth("70%");
        image.setHeight("70%");
        image.getStyle().set(BOX_SHADOW_PROPERTY, BOX_SHADOW_VALUE);

        final VerticalLayout content = new VerticalLayout(new Text("Please set the custom size greater than zero, or enable the toggle button for full readability."));
        content.setPadding(false);
        content.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);

        final Icon iconArrowRight = VaadinIcon.ARROW_RIGHT.create();
        Tooltip.forComponent(iconArrowRight).setText("Enable the toggle button!!!");
        iconArrowRight.setSize("30px");
        Animated.animate(iconArrowRight, Animated.Animation.HEART_BEAT, INFINITE);

        final HorizontalLayout row = new HorizontalLayout(iconArrowRight, image);
        row.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        row.setVerticalComponentAlignment(FlexComponent.Alignment.END, iconArrowRight);
        content.add(row);
        content.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, row);

        return content;
    }

    /**
     * Separator for sections in step2 and step3
     *
     * @return A {@link Hr}
     */
    private Hr createSeparator() {
        final Hr separator = new Hr();
        separator.setWidthFull();
        separator.addClassNames(Top.XLARGE, Bottom.MEDIUM);
        return separator;
    }

    /**
     * The content for write_flash step
     *
     * @return A {@link Component}
     */
    private Component createDownloadFlashContent() {
        final H2 selectingTheFirmwareTitle = new H2("Read flash and Download flash backup");
        selectingTheFirmwareTitle.addClassNames(LumoUtility.FontSize.XLARGE, LumoUtility.Margin.Top.MEDIUM);
        selectingTheFirmwareTitle.setId(selectingTheFirmwareTitle.getText().replace(" ", "-").toLowerCase());

        final Button readFlashButton = new Button("Read flash");
        readFlashButton.addClassName(EspFlowConstants.BOX_SHADOW_VAADIN_BUTTON);

        final Paragraph description = new Paragraph();
        description.getElement().setProperty(INNER_HTML, "Here the read flash will be available for download in the folder of your choice.");
        description.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);

        final Button downloadFlashBackUpButton = new Button(VaadinIcon.DOWNLOAD.create());
        downloadFlashBackUpButton.addClassName(EspFlowConstants.BOX_SHADOW_VAADIN_BUTTON);

        final Paragraph descriptionDownloadFlashButton = new Paragraph();
        descriptionDownloadFlashButton.getElement().setProperty(INNER_HTML, "The download button will be available if the download is completed successfully.");
        descriptionDownloadFlashButton.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);

        final HorizontalLayout rowDownloadFlash = new HorizontalLayout(downloadFlashBackUpButton, descriptionDownloadFlashButton);

        final Image writeFlashImage = new Image(FRONTEND_IMAGES_CUSTOM + "read-flash-and-download-button.png", "read_flash");
        writeFlashImage.setWidth("90%");
        writeFlashImage.addClassName(Top.LARGE);
        writeFlashImage.getStyle().set(BOX_SHADOW_PROPERTY, BOX_SHADOW_VALUE);
        Animated.animate(writeFlashImage, Animation.FADE_IN, Modifier.SLOW);

        Stream.of(writeFlashImage)
                .forEach(image -> image.getStyle().setAlignSelf(AlignSelf.CENTER));

        final Layout layout = new Layout(selectingTheFirmwareTitle, description, rowDownloadFlash, writeFlashImage);
        layout.setFlexDirection(Layout.FlexDirection.COLUMN);

        final Scroller scroller = new Scroller(layout);
        scroller.setWidthFull();
        scroller.setScrollDirection(ScrollDirection.VERTICAL);
        scroller.getStyle().set("scrollbar-width", "thin");

        return scroller;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        sNext = "step1";
        next.setText("Next");
        this.previous.setVisible(false);
        this.mainContent.removeAll();
        this.mainContent.add(refreshDevices());
        avatar2.removeClassNames(AVATAR_STEP_ACTIVE);
        avatar2.addClassName(AVATAR_STEP_INACTIVE);
        avatar3.removeClassNames(AVATAR_STEP_ACTIVE);
        avatar3.addClassName(AVATAR_STEP_INACTIVE);
    }
}