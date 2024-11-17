package com.esp.espflow.views.readflash;

import com.esp.espflow.entity.dto.WizardEspDto;
import com.esp.espflow.service.respository.impl.WizardEspService;
import com.esp.espflow.util.EspFlowConstants;
import com.esp.espflow.util.svgfactory.SvgFactory;
import com.esp.espflow.views.Layout;
import com.esp.espflow.views.flashesp.InitialInformationFlashEspViewDialog;
import com.infraleap.animatecss.Animated;
import com.infraleap.animatecss.Animated.Animation;
import com.infraleap.animatecss.Animated.Modifier;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
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
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.Scroller.ScrollDirection;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.dom.Style.AlignSelf;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.BorderRadius;
import com.vaadin.flow.theme.lumo.LumoUtility.BoxSizing;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin.Bottom;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin.Right;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin.Top;
import com.vaadin.flow.theme.lumo.LumoUtility.MaxWidth;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.Width;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.esp.espflow.util.EspFlowConstants.BOX_SHADOW_PROPERTY;
import static com.esp.espflow.util.EspFlowConstants.BOX_SHADOW_VALUE;
import static com.esp.espflow.util.EspFlowConstants.ESPFLOW_SOURCE_CODE;
import static com.esp.espflow.util.EspFlowConstants.FRONTEND_ICON;
import static com.esp.espflow.util.EspFlowConstants.FRONTEND_IMAGES_CUSTOM;
import static com.esp.espflow.util.EspFlowConstants.INNER_HTML;
import static com.esp.espflow.util.EspFlowConstants.WIZARD_READ_FLASH_ESP_VIEW;

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
public class WizardReadFlashView extends Dialog {

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

    @PostConstruct
    public void init() {

        this.initConfiguration();
    }

    private void initConfiguration() {
        super.setMaxWidth("680px");
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

        super.add(hr, this.createWelcomeLayout());
        super.addClassName("settings-content-dialog");
    }

    /**
     * Configure the dialog header here
     */
    private void configureHeader() {
        this.step1 = this.rowStepAvatar(avatar1, "1", "1", new H4("Welcome"));
        this.step2 = this.rowStepAvatar(avatar2, "2", "2", new H4("Execute flash_id"));
        this.step3 = this.rowStepAvatar(avatar3, "3", "3", new H4("Execute write_flash"));
        step3.remove(step3.getComponentAt(2));

        final Div divHeader = new Div(step1, step2, step3);
        divHeader.addClassNames(Display.FLEX, LumoUtility.FlexDirection.ROW,
                Width.FULL, LumoUtility.JustifyContent.CENTER, Top.MEDIUM);

        step1.addClickListener(event -> {
            this.mainContent.removeAll();
            this.mainContent.add(createNavWithEspLogoWelcome(), createWelcomeContent());
        });

        step2.addClickListener(event -> {
            this.mainContent.removeAll();
            this.mainContent.add(createFlashIdContent());
            avatar2.removeClassNames("avatar-step-inactive");
            avatar2.addClassName("avatar-step-active");
        });

        step3.addClickListener(event -> {
            this.mainContent.removeAll();
            this.mainContent.add(writeFlashContent());
            avatar3.removeClassNames("avatar-step-inactive");
            avatar3.addClassName("avatar-step-active");
        });

        super.getHeader().add(divHeader);
    }

    /**
     * Configure the footer here, and we add the listener of the buttons as well
     */
    private void configureFooter() {
        this.next.addClickListener(event -> {
            getUI().ifPresent(ui -> {
                ui.getPage().fetchCurrentURL(url -> {
                    ui.getPage().getHistory().replaceState(null, url.getPath());
                });
            });
        });
        next.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        this.previous.addClickListener(event -> {
            getUI().ifPresent(ui -> {
                ui.getPage().fetchCurrentURL(url -> {
                    ui.getPage().getHistory().replaceState(null, url.getPath());
                });
            });
        });
        hideButton.setTooltipText("Hide this dialog");
        hideButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        hideButton.getStyle().set("margin-right", "auto");
        hideButton.addClickListener(event -> {
            final WizardEspDto wizardEspDto = WizardEspDto.builder()
                    .isWizardEnabled(false)
                    .name(WIZARD_READ_FLASH_ESP_VIEW)
                    .build();
            this.wizardEspService.save(wizardEspDto);
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
            avatar.addClassName("avatar-step-inactive");
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
    private Main createWelcomeLayout() {
        mainContent.addClassNames(Display.FLEX, LumoUtility.FlexDirection.ROW, LumoUtility.Height.FULL);
        mainContent.add(this.createNavWithEspLogoWelcome(), this.createWelcomeContent());
        return mainContent;
    }

    /**
     * The nav with the logo for the first welcome step
     *
     * @return A {@link Component}
     */
    public Component createNavWithEspLogoWelcome() {

        Stream.of(step1, step2, step3, avatar1, avatar2, avatar3)
                .forEach(items -> items.getStyle().setCursor("pointer"));

        final Image image = new Image(FRONTEND_ICON, "icon");
        image.getStyle().set(BOX_SHADOW_PROPERTY, BOX_SHADOW_VALUE);
        image.getStyle().setCursor(EspFlowConstants.CURSOR_POINTER);
        image.addClassName(BorderRadius.LARGE);
        image.setWidth("180px");
        image.setHeight("200px");
        var tooltip = Tooltip.forComponent(image);
        tooltip.setPosition(Tooltip.TooltipPosition.TOP);
        tooltip.setText(ESPFLOW_SOURCE_CODE);
        image.addClickListener(e -> getUI().ifPresent(ui -> ui.getPage().open(ESPFLOW_SOURCE_CODE)));
        Animated.animate(image, Animation.FADE_IN, Modifier.SLOW);

        final Div div = new Div(image);
        div.addClassNames(Display.FLEX, LumoUtility.FlexDirection.COLUMN,
                LumoUtility.Margin.Vertical.XLARGE, Padding.Horizontal.LARGE);

        final Nav nav = new Nav(div);
        nav.addClassNames(Display.HIDDEN, Display.Breakpoint.Small.FLEX, LumoUtility.FontSize.SMALL, LumoUtility.Position.STICKY, "top-0");

        return nav;
    }

    /**
     * The welcome content
     *
     * @return A {@link Component}
     */
    public Component createWelcomeContent() {

        final InitialInformationFlashEspViewDialog welcome = new InitialInformationFlashEspViewDialog();

        final Layout layout = new Layout(welcome);
        layout.setId("welcome-id");
        // Viewport < 1024px
        layout.setFlexDirection(Layout.FlexDirection.COLUMN);
        // Viewport > 1024px
        layout.setColumns(Layout.GridColumns.COLUMNS_2);
        layout.setColumnGap(Layout.Gap.MEDIUM);
        layout.setColumnSpan(Layout.ColumnSpan.COLUMN_SPAN_FULL);

        final Layout layoutResult = new Layout(layout);
        layout.addClassNames(BoxSizing.BORDER, MaxWidth.SCREEN_SMALL, Padding.LARGE);
        layout.setFlexDirection(Layout.FlexDirection.COLUMN);

        return layoutResult;
    }

    /**
     * The content for flash_id step
     *
     * @return A {@link Component}
     */
    public Component createFlashIdContent() {
        final H2 titlePortScanning = new H2("Port scanning");
        titlePortScanning.addClassNames(LumoUtility.FontSize.XLARGE, LumoUtility.Margin.Top.MEDIUM);
        titlePortScanning.setId(titlePortScanning.getText().replace(" ", "-").toLowerCase());

        final Button scanButton = new Button(VaadinIcon.REFRESH.create());
        var rowScanningPort = new HorizontalLayout(scanButton, titlePortScanning);

        final Paragraph descriptionComboSelectionPort = new Paragraph();
        descriptionComboSelectionPort.getElement().setProperty(INNER_HTML, "To run the port scan, first we use the refresh button, it will search the system ports, and when selecting a port the <strong>flash_id</strong> command is executed.");
        descriptionComboSelectionPort.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);

        final Image comboImage = new Image(FRONTEND_IMAGES_CUSTOM + "using-combo.gif", "combo");
        comboImage.setWidth("80%");
        comboImage.getStyle().set(BOX_SHADOW_PROPERTY, BOX_SHADOW_VALUE);

        final H2 titlePlayButton = new H2("Using the play button");
        titlePlayButton.addClassNames(LumoUtility.FontSize.XLARGE, LumoUtility.Margin.Top.MEDIUM);
        titlePlayButton.setId(titlePlayButton.getText().replace(" ", "-").toLowerCase());

        final Button playButton = new Button(VaadinIcon.PLAY.create());
        var rowPlayButtonTitle = new HorizontalLayout(playButton, titlePlayButton);

        final Paragraph descriptionButtonPlay = new Paragraph();
        descriptionButtonPlay.getElement().setProperty(INNER_HTML, "The play button will be used to scan the previous port, without the need to use the drop-down, executing the <strong>flash_id</strong> command as well.");
        descriptionButtonPlay.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);

        final Image playButtonImage = new Image(FRONTEND_IMAGES_CUSTOM + "play-button.gif", "play");
        playButtonImage.setWidth("80%");
        playButtonImage.getStyle().set(BOX_SHADOW_PROPERTY, BOX_SHADOW_VALUE);

        final Hr separator = this.createSeparator();

        final H2 titlePermissionDenied = new H2("Permission denied: '/dev/ttyUSB2'");
        titlePermissionDenied.addClassNames(LumoUtility.FontSize.XLARGE, LumoUtility.Margin.Top.MEDIUM);
        titlePermissionDenied.setId(titlePermissionDenied.getText().replace(" ", "-").toLowerCase());
        final Button writePasswordButton = new Button(SvgFactory.createIconFromSvg("unlock-black.svg", "30px", null));
        var rowPermisson = new HorizontalLayout(writePasswordButton, titlePermissionDenied);

        final Hr separatorChangePermissions = this.createSeparator();

        final Paragraph descriptionPermissionDeniedPort = new Paragraph();
        descriptionPermissionDeniedPort.getElement().setProperty(INNER_HTML, "If when executing the <strong>flash_id</strong> we have permissions problems, we can execute the lock button to enter the user credentials, <strong>no password will be saved or sent anywhere<strong>.");
        descriptionPermissionDeniedPort.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);


        final Image changePermissionsImage = new Image(FRONTEND_IMAGES_CUSTOM + "change-permissions.png", "change-permissions");
        changePermissionsImage.setWidth("80%");
        changePermissionsImage.getStyle().set(BOX_SHADOW_PROPERTY, BOX_SHADOW_VALUE);

        Animated.animate(comboImage, Animation.FADE_IN, Modifier.SLOW);
        Stream.of(comboImage, playButtonImage, changePermissionsImage)
                .forEach(image -> image.getStyle().setAlignSelf(AlignSelf.CENTER));

        final Layout layout = new Layout(rowScanningPort, descriptionComboSelectionPort, comboImage, separator, rowPlayButtonTitle, descriptionButtonPlay, playButtonImage, separatorChangePermissions, rowPermisson, descriptionPermissionDeniedPort, changePermissionsImage);
        layout.setFlexDirection(Layout.FlexDirection.COLUMN);
        layout.setColumnGap(Layout.Gap.MEDIUM);
        layout.setColumnSpan(Layout.ColumnSpan.COLUMN_SPAN_FULL, rowScanningPort, rowPlayButtonTitle, separator,
                descriptionComboSelectionPort, comboImage, descriptionButtonPlay, playButtonImage, separatorChangePermissions, changePermissionsImage, rowPermisson, descriptionPermissionDeniedPort);

        final Scroller scroller = new Scroller(layout);
        scroller.setScrollDirection(ScrollDirection.VERTICAL);

        return scroller;
    }

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
    public Component writeFlashContent() {
        final H2 selectingTheFirmwareTitle = new H2("Selecting the firmware");
        selectingTheFirmwareTitle.addClassNames(LumoUtility.FontSize.XLARGE, LumoUtility.Margin.Top.MEDIUM);
        selectingTheFirmwareTitle.setId(selectingTheFirmwareTitle.getText().replace(" ", "-").toLowerCase());

        final Paragraph description = new Paragraph();
        description.getElement().setProperty(INNER_HTML, "Here we must select the firmware, as in the following animation, the port must be <strong>selected</strong> with permissions, and the <strong>firmware.</strong>");
        description.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);

        final Image writeFlashImage = new Image(FRONTEND_IMAGES_CUSTOM + "write_flash.gif", "combo");
        writeFlashImage.setWidth("90%");
        writeFlashImage.getStyle().set(BOX_SHADOW_PROPERTY, BOX_SHADOW_VALUE);
        Animated.animate(writeFlashImage, Animation.FADE_IN, Modifier.SLOW);

        Stream.of(writeFlashImage)
                .forEach(image -> image.getStyle().setAlignSelf(AlignSelf.CENTER));

        final Layout layout = new Layout(selectingTheFirmwareTitle, description, writeFlashImage);
        layout.setFlexDirection(Layout.FlexDirection.COLUMN);

        final Scroller scroller = new Scroller(layout);
        scroller.setScrollDirection(ScrollDirection.VERTICAL);

        return scroller;
    }

    /**
     * @param buttonToChange that only the background will be set
     */
    private void setBackGroundOnClick(Button buttonToChange) {
        final String backgroundColorStyle = "background-color";

        String backgroundColor = buttonToChange.getStyle().get(backgroundColorStyle);

        if (Objects.isNull(backgroundColor)) {
            buttonToChange.getStyle().setBackgroundColor("var(--lumo-primary-color-10pct)");
        }

        final Predicate<Button> ignoreTheParameterButtonSoAsNotToChangeItsStyle = buttonItem -> !buttonItem.equals(buttonToChange);

//        Stream.of(passwordButton, notificationsButton, contactInformationButton, publicInformationButton)
//                .filter(ignoreTheParameterButtonSoAsNotToChangeItsStyle)
//                .forEach(buttonItem -> buttonItem.getStyle().remove(backgroundColorStyle));

    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
    }

}