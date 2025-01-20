package com.esp.espflow.views.settings;

import com.esp.espflow.entity.dto.WizardEspDto;
import com.esp.espflow.enums.Breakpoint;
import com.esp.espflow.service.respository.impl.WizardEspService;
import com.esp.espflow.util.EspFlowConstants;
import com.esp.espflow.util.animations.AnimationsUtils;
import com.esp.espflow.util.svgfactory.SvgFactory;
import com.esp.espflow.views.Layout;
import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.Style.Overflow;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.theme.lumo.LumoUtility.BoxSizing;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.MaxWidth;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.esp.espflow.util.EspFlowConstants.NAV_SETTINGS;
import static com.esp.espflow.util.EspFlowConstants.SCROLLBAR_CUSTOM_STYLE;
import static com.esp.espflow.util.EspFlowConstants.SETTINGS;
import static com.esp.espflow.util.EspFlowConstants.THIS_FEATURE_HAS_NOT_BEEN_IMPLEMENTED_YET;
import static com.esp.espflow.util.EspFlowConstants.WIZARD_FLASH_ESP_VIEW;
import static com.esp.espflow.util.EspFlowConstants.WIZARD_READ_FLASH_ESP_VIEW;

/**
 * https://github.com/vaadin/web-components/issues/7778#issuecomment-2334597476
 *
 * @author rub'n
 */
@UIScope
@SpringComponent
@Log4j2
@RolesAllowed("ADMIN")
@RequiredArgsConstructor
public class SettingsDialogView extends Dialog implements AnimationsUtils {

    private static final String PASSWORD = "password";
    private static final String CONTACT_INFORMATION = "contact-information";
    private static final String ESPTOOL_HOMEPATH = "esptool-homepath";
    private static final String NOTIFICATION = "notifications";
    private static final String ITEM_ICON_SIZE = "24px";

    private final Button buttonEsptoolHomePath = new Button("esptool.py home path");
    private final Button buttonManageSettings = new Button("Manage settings...");
    private final Button buttonPassword = new Button("Password");
    private final Button buttonNotifications = new Button("Notifications");
    private final Button buttonCheckUpdates = new Button("Check updates...");
    private final Button buttonToggle = new Button(VaadinIcon.MENU.create());
    private final Layout mainLayout = new Layout();
    private final WizardEspService wizardFlashEspRepository;
    private final SettingsEsptoolHomePathContent settingsEsptoolHomePathContent;

    @PostConstruct
    public void init() {
        this.initListeners();
    }

    private void configureDialog(String ref) {
        super.setCloseOnOutsideClick(true);
        super.setMaxWidth("700px");
        super.setWidth("700px");
        super.setMaxHeight("500px");
        super.setHeight("500px");
        super.setHeaderTitle("Settings");
        this.setBackGroundOnClick(buttonEsptoolHomePath);

        final Button closeButton = new Button(new Icon("lumo", "cross"));
        closeButton.addClickListener(event -> {
            closeButton.getUI().ifPresent(ui -> {
                ui.getPage().fetchCurrentURL(url -> {
                    ui.getPage().getHistory().replaceState(null, url.getPath());
                });
            });
            super.close();
        });
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

        super.getHeader().removeAll();
        super.getHeader().add(closeButton);

        final Main main = new Main();
        main.setId("main-settings");
        main.addClassName("main-settings");
        main.addClassNames(Display.FLEX, LumoUtility.FlexDirection.ROW, LumoUtility.Height.FULL);

        final Hr hr = new Hr();
        hr.addClassName("hr-header-settings");

        var nav = this.createButtonsItemsMenu();

        UI.getCurrent().getPage().addBrowserWindowResizeListener(e -> {
            if (e.getWidth() > 660) {
                nav.addClassNames(NAV_SETTINGS);
            }
        });

        buttonToggle.addClickListener(buttonClickEvent -> {
            if (buttonClickEvent.isFromClient()) {
                nav.getStyle().setWidth("80%");
            }
        });

        main.add(this.createButtonToggle(), nav, this.createForm(ref));

        super.add(hr, main);
        super.addClassName("settings-content-dialog");
    }

    private Button createButtonToggle() {
        buttonToggle.addClassName("settings-button-toggle");
        buttonToggle.setTooltipText("Show menu");
        return buttonToggle;
    }

    /**
     * We invoke them within the onAttach, to correctly create the component.
     */
    private void initListeners() {
        var bell = SvgFactory.createIconFromSvg("bell.svg", ITEM_ICON_SIZE, null);
        bell.addClassNames("black-to-white", "svg-icon-settings");
        buttonNotifications.setPrefixComponent(bell);
        buttonNotifications.addClickListener(event -> {
            this.mainLayout.removeAll();
            this.mainLayout.add(this.createNotifications());
            this.setBackGroundOnClick(buttonNotifications);
            this.updateFragment(NOTIFICATION);
        });

        var iconEspressifSvg = SvgFactory.createIconFromSvg("espressif-logo.svg", "26px", null);
        iconEspressifSvg.addClassName("svg-icon-settings");
        buttonEsptoolHomePath.setPrefixComponent(iconEspressifSvg);
        buttonEsptoolHomePath.addClickListener(event -> {
            this.mainLayout.removeAll();
            this.mainLayout.add(createEsptoolHomePathContent());
            this.setBackGroundOnClick(buttonEsptoolHomePath);
            this.updateFragment(ESPTOOL_HOMEPATH);
        });
        buttonManageSettings.setPrefixComponent(VaadinIcon.ARCHIVE.create());
        buttonManageSettings.addClickListener(event -> {
            Notification.show(THIS_FEATURE_HAS_NOT_BEEN_IMPLEMENTED_YET, 2000, Notification.Position.MIDDLE);
        });
        buttonPassword.setPrefixComponent(VaadinIcon.PASSWORD.create());
        buttonPassword.addClickListener(event -> {
            Notification.show(THIS_FEATURE_HAS_NOT_BEEN_IMPLEMENTED_YET, 2000, Notification.Position.MIDDLE);
        });
        buttonCheckUpdates.addClickListener(event -> Notification.show(THIS_FEATURE_HAS_NOT_BEEN_IMPLEMENTED_YET, 2000, Notification.Position.MIDDLE));
    }

    private void updateFragment(String contentName) {
        getUI().ifPresent(ui -> {
            ui.getPage().fetchCurrentURL(url -> {
                String ref = StringUtils.defaultString(url.getRef());
                final String pathWithUrlParameters = DialogUtilsReplaceUri.INSTANCE.replaceOrConcatFragment(url.getPath(), ref, contentName);
                ui.getPage().getHistory().replaceState(null, pathWithUrlParameters);
            });
        });
    }

    /**
     * @param ref
     */
    public void open(final String ref) {
        this.removeAll();
        this.mainLayout.removeAll();
        this.configureDialog(ref);
    }

    public Component createForm(String ref) {

        String newLocation = DialogUtilsReplaceUri.INSTANCE.parseUriToCreateTheContentForm(ref);

        switch (newLocation) {
            case ESPTOOL_HOMEPATH, SETTINGS -> {
                this.mainLayout.add(this.createEsptoolHomePathContent());
                this.setBackGroundOnClick(buttonEsptoolHomePath);
                this.openAndDisableModeless();
            }
            case CONTACT_INFORMATION -> {
                this.mainLayout.add(this.createContactInformation());
                this.setBackGroundOnClick(buttonManageSettings);
                this.openAndDisableModeless();
            }
            case PASSWORD -> {
                this.mainLayout.add(this.createPassword());
                this.setBackGroundOnClick(buttonPassword);
                this.openAndDisableModeless();
            }
            case NOTIFICATION -> {
                this.mainLayout.add(this.createNotifications());
                this.setBackGroundOnClick(buttonNotifications);
                this.openAndDisableModeless();
            }
            case StringUtils.EMPTY -> {
                this.mainLayout.add(this.createEsptoolHomePathContent());
                this.setBackGroundOnClick(buttonEsptoolHomePath);
            }
            default -> {
                //Do nothing
            }
        }
        this.mainLayout.addClassNames(BoxSizing.BORDER, MaxWidth.SCREEN_SMALL, Padding.LARGE);
        this.mainLayout.setFlexDirection(Layout.FlexDirection.COLUMN);
        return mainLayout;
    }

    /**
     * You must disable the server-side modality each time after opening the dialog
     * https://github.com/vaadin/web-components/issues/7778#issuecomment-2334597476
     */
    public void openAndDisableModeless() {
        getUI().ifPresent(ui -> {
            super.open();
            ui.setChildComponentModal(this, false);
        });
    }

    public Component createEsptoolHomePathContent() {
        return settingsEsptoolHomePathContent.createEsptoolHomePathContent();
    }

    public Component createContactInformation() {
        H2 title = new H2("Contact information");
        title.addClassNames(LumoUtility.FontSize.XLARGE, LumoUtility.Margin.Top.MEDIUM);
        title.setId(title.getText().replace(" ", "-").toLowerCase());

        Paragraph description = new Paragraph("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");
        description.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);

        TextField address = new TextField("Address");
        TextField city = new TextField("City");
        ComboBox<String> state = new ComboBox<>("State");
        TextField zip = new TextField("ZIP");

        TextField phone = new TextField("Phone");
        phone.setPrefixComponent(LineAwesomeIcon.PHONE_SOLID.create());

        EmailField email = new EmailField("Email");
        email.setPrefixComponent(LineAwesomeIcon.ENVELOPE.create());

        Layout layout = new Layout(title, description, address, city, state, zip, phone, email);
        // Viewport < 1024px
        layout.setFlexDirection(Layout.FlexDirection.COLUMN);
        // Viewport > 1024px
        layout.setDisplay(Breakpoint.LARGE, Layout.Display.GRID);
        layout.setColumns(Layout.GridColumns.COLUMNS_4);
        layout.setColumnGap(Layout.Gap.MEDIUM);
        layout.setColumnSpan(Layout.ColumnSpan.COLUMN_SPAN_2, city, phone, email);
        layout.setColumnSpan(Layout.ColumnSpan.COLUMN_SPAN_FULL, title, description, address);

        return layout;
    }

    public Component createPassword() {
        H2 title = new H2("Password");
        title.addClassNames(LumoUtility.FontSize.XLARGE, LumoUtility.Margin.Top.MEDIUM);
        title.setId(title.getText().replace(" ", "-").toLowerCase());

        Paragraph description = new Paragraph("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");
        description.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);

        TextField currentPassword = new TextField("Current password");
        TextField newPassword = new TextField("New password");
        TextField confirmPassword = new TextField("Confirm password");

        Layout layout = new Layout(title, description, currentPassword, newPassword, confirmPassword);
        layout.setFlexDirection(Layout.FlexDirection.COLUMN);
        return layout;
    }

    public Component createNotifications() {
        H2 title = new H2("Notifications");
        title.addClassNames(LumoUtility.FontSize.XLARGE, LumoUtility.Margin.Top.MEDIUM);
        title.setId(title.getText().replace(" ", "-").toLowerCase());

        Paragraph description = new Paragraph("Enable and disable push notifications to the top right panel, as well as the wizards.");
        description.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);

        CheckboxGroup<String> emailNotifications = new CheckboxGroup<>("Email notifications");
        emailNotifications.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
        emailNotifications.setItems("Newsletters", "Promotional offers", "Account updates", "New messages or activities", "Events or upcoming appointments");

        final Span spanWizardsNotifications = new Span("Wizards notifications");

        final Paragraph paragraphEnableAllNotifications = new Paragraph("Enable notifications in bell");

        paragraphEnableAllNotifications.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);
        final ToggleButton toggleButtonNotifications = new ToggleButton();
        final HorizontalLayout rowPushNotificationsBell = new HorizontalLayout(paragraphEnableAllNotifications, toggleButtonNotifications);
        rowPushNotificationsBell.setWidthFull();
        rowPushNotificationsBell.setJustifyContentMode(JustifyContentMode.BETWEEN);
        rowPushNotificationsBell.setAlignItems(Alignment.CENTER);
        rowPushNotificationsBell.setVisible(false);

        final Paragraph paragraphEnableAllWizards = new Paragraph("Enable all wizards dialogs");
        paragraphEnableAllWizards.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);

        final ToggleButton toggleButtonEnableAllWizards = new ToggleButton();
        final ToggleButton toggleButtonFlashEsp = new ToggleButton();
        final ToggleButton toggleButtonReadFlashEsp = new ToggleButton();

        toggleButtonEnableAllWizards.addValueChangeListener(event -> {
            if (event.isFromClient()) {
                if (event.getValue()) {
                    this.updateAllWizardStatus(event.getValue());
                    toggleButtonFlashEsp.setValue(true);
                    toggleButtonReadFlashEsp.setValue(true);
                } else {
                    this.updateAllWizardStatus(event.getValue());
                    toggleButtonFlashEsp.setValue(false);
                    toggleButtonReadFlashEsp.setValue(false);
                }
            }
        });
        final HorizontalLayout rowEnableInitialWizardsDialogs = new HorizontalLayout(paragraphEnableAllWizards, toggleButtonEnableAllWizards);
        rowEnableInitialWizardsDialogs.setWidthFull();
        rowEnableInitialWizardsDialogs.setJustifyContentMode(JustifyContentMode.BETWEEN);
        rowEnableInitialWizardsDialogs.setAlignItems(Alignment.CENTER);

        final Paragraph spanEnableWizardFlashEsp = new Paragraph("Enable wizard Flash Esp32-ESP8266 view");
        spanEnableWizardFlashEsp.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);
        toggleButtonFlashEsp.addValueChangeListener(event -> {
            if (event.isFromClient()) {
                if (event.getValue()) {
                    updateWizardView(WIZARD_FLASH_ESP_VIEW, true);
                } else {
                    updateWizardView(WIZARD_FLASH_ESP_VIEW, false);
                }
            }
        });
        final Paragraph spanEnableWizardReadFlashEsp = new Paragraph("Enable wizard Read flash/firmware view");
        spanEnableWizardReadFlashEsp.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);
        toggleButtonReadFlashEsp.addValueChangeListener(event -> {
            if (event.isFromClient()) {
                if (event.getValue()) {
                    updateWizardView(WIZARD_READ_FLASH_ESP_VIEW, true);
                } else {
                    updateWizardView(WIZARD_READ_FLASH_ESP_VIEW, false);
                }
            }
        });

        final HorizontalLayout rowWizardFlashView = new HorizontalLayout(spanEnableWizardFlashEsp, toggleButtonFlashEsp);
        rowWizardFlashView.setWidthFull();
        rowWizardFlashView.setJustifyContentMode(JustifyContentMode.BETWEEN);
        rowWizardFlashView.setAlignItems(Alignment.CENTER);

        final HorizontalLayout rowWizardReadFlashView = new HorizontalLayout(spanEnableWizardReadFlashEsp, toggleButtonReadFlashEsp);
        rowWizardReadFlashView.setWidthFull();
        rowWizardReadFlashView.setJustifyContentMode(JustifyContentMode.BETWEEN);
        rowWizardReadFlashView.setAlignItems(Alignment.CENTER);

        final VerticalLayout verticalLayout = new VerticalLayout(rowPushNotificationsBell, new Hr(), rowEnableInitialWizardsDialogs,
                new Hr(), rowWizardFlashView, new Hr(), rowWizardReadFlashView);
        verticalLayout.setPadding(false);
        verticalLayout.setSpacing(false);
        verticalLayout.getStyle().setOverflow(Overflow.HIDDEN);

        final Layout layout = new Layout(title, description, spanWizardsNotifications, verticalLayout);
        layout.setFlexDirection(Layout.FlexDirection.COLUMN);

        if (this.wizardFlashEspRepository.areAllWizardsEnabled()) {
            log.info("Count all wizard enable {}", this.wizardFlashEspRepository.areAllWizardsEnabled());
            toggleButtonEnableAllWizards.setValue(true);
            toggleButtonFlashEsp.setValue(true);
            toggleButtonReadFlashEsp.setValue(true);
        }

        this.wizardFlashEspRepository.findByName(WIZARD_FLASH_ESP_VIEW)
                .ifPresent(entityIfPresent -> {
                    log.info("wizardFlashEspView {}", entityIfPresent);
                    toggleButtonFlashEsp.setValue(entityIfPresent.isWizardEnabled());
                });

        this.wizardFlashEspRepository.findByName(WIZARD_READ_FLASH_ESP_VIEW)
                .ifPresent(entityIfPresent -> {
                    log.info("wizardReadFlashEspView {}", entityIfPresent);
                    toggleButtonReadFlashEsp.setValue(entityIfPresent.isWizardEnabled());
                });

        final Scroller scroller = new Scroller(layout);
        scroller.setWidthFull();
        scroller.setScrollDirection(Scroller.ScrollDirection.VERTICAL);
        scroller.getElement().executeJs(SCROLLBAR_CUSTOM_STYLE);
        return scroller;
    }

    /**
     * @param value status of the wizard
     */
    private void updateAllWizardStatus(final boolean value) {

        this.wizardFlashEspRepository.findByName(WIZARD_FLASH_ESP_VIEW)
                .ifPresent(entityIfPresent -> {
                    final WizardEspDto wizardFlashEsp = WizardEspDto.builder()
                            .id(entityIfPresent.id())
                            .name(WIZARD_FLASH_ESP_VIEW)
                            .isWizardEnabled(value)
                            .build();
                    this.wizardFlashEspRepository.save(wizardFlashEsp);
                });

        this.wizardFlashEspRepository.findByName(WIZARD_READ_FLASH_ESP_VIEW)
                .ifPresent(entityIfPresent -> {
                    final WizardEspDto wizardReadFlash = WizardEspDto.builder()
                            .id(entityIfPresent.id())
                            .name(WIZARD_READ_FLASH_ESP_VIEW)
                            .isWizardEnabled(value)
                            .build();
                    this.wizardFlashEspRepository.save(wizardReadFlash);
                });
    }

    private void updateWizardView(final String viewName, final boolean value) {
        this.wizardFlashEspRepository.findByName(viewName)
                .ifPresent(entityIfPresent -> {
                    final WizardEspDto wizardFlashEsp = WizardEspDto.builder()
                            .id(entityIfPresent.id())
                            .name(viewName)
                            .isWizardEnabled(value)
                            .build();
                    this.wizardFlashEspRepository.save(wizardFlashEsp);
                });
    }

    public Component createButtonsItemsMenu() {

        Stream.of(buttonNotifications, buttonPassword, buttonEsptoolHomePath, buttonManageSettings, buttonCheckUpdates)
                .forEach(button -> {
                    button.getStyle().setCursor(EspFlowConstants.CURSOR_POINTER);
                    button.addClassNames("settings-buttons");
                    button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
                });

        final Button button = new Button(VaadinIcon.CLOSE.create());
        //button.getStyle().set("display","none");
        button.setWidth("10px");
        button.addClassName("button-cross-nav-settings");

        final Div div = new Div(button, buttonEsptoolHomePath, buttonManageSettings, buttonPassword, buttonNotifications, buttonCheckUpdates);
        div.setId("div-item-container");
        div.addClassNames(Display.FLEX, LumoUtility.FlexDirection.COLUMN,
                LumoUtility.Margin.Vertical.XLARGE, Padding.Horizontal.LARGE);

        final Nav nav = new Nav(div);
        nav.addClassNames(NAV_SETTINGS);

        button.addClickListener(event -> {
            nav.getStyle().setWidth("0");
        });

        return nav;
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

        Stream.of(buttonPassword, buttonNotifications, buttonManageSettings, buttonEsptoolHomePath)
                .filter(ignoreTheParameterButtonSoAsNotToChangeItsStyle)
                .forEach(buttonItem -> buttonItem.getStyle().remove(backgroundColorStyle));

    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        if (attachEvent.isInitialAttach()) {
            final UI ui = attachEvent.getUI();
            ui.getPage().fetchCurrentURL(url -> {
                final String ref = url.getRef();
                this.configureDialog(ref);
            });
        }
    }

}