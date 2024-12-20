package com.esp.espflow.views.settings;

import com.esp.espflow.entity.dto.WizardEspDto;
import com.esp.espflow.enums.Breakpoint;
import com.esp.espflow.service.respository.impl.WizardEspService;
import com.esp.espflow.util.EspFlowConstants;
import com.esp.espflow.util.svgfactory.SvgFactory;
import com.esp.espflow.views.Layout;
import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
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
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
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

import static com.esp.espflow.util.EspFlowConstants.SETTINGS;
import static com.esp.espflow.util.EspFlowConstants.WIZARD_FLASH_ESP_VIEW;
import static com.esp.espflow.util.EspFlowConstants.WIZARD_READ_FLASH_ESP_VIEW;

/**
 * @author rub'n
 */
@UIScope
@SpringComponent
@Log4j2
@RolesAllowed("ADMIN")
@RequiredArgsConstructor
public class SettingsDialogView extends Dialog {

    private static final String PASSWORD = "password";
    private static final String CONTACT_INFORMATION = "contact-information";
    private static final String PUBLIC_INFORMATION = "public-information";
    private static final String NOTIFICATION = "notifications";
    private static final String SIZE = "16px";

    private final Button publicInformationButton = new Button("esptool.py home path");
    private final Button contactInformationButton = new Button("Manage settings...");
    private final Button updates = new Button("Check updates...");
    private final Button passwordButton = new Button("Password");
    private final Button notificationsButton = new Button("Notifications");

    private final Layout mainLayout = new Layout();
    private final WizardEspService wizardFlashEspRepository;

    @PostConstruct
    public void init() {

    }

    /**
     * We invoke them within the onAttach, to correctly create the component.
     */
    private void initListeners() {
        notificationsButton.setPrefixComponent(SvgFactory.createIconFromSvg("bell.svg", SIZE, null));
        notificationsButton.addClickListener(event -> {
            this.mainLayout.removeAll();
            this.mainLayout.add(this.createNotifications());
            this.setBackGroundOnClick(notificationsButton);
            this.updateFragment(NOTIFICATION);
        });
        publicInformationButton.setPrefixComponent(VaadinIcon.INFO.create());
        publicInformationButton.addClickListener(event -> {
            this.mainLayout.removeAll();
            this.mainLayout.add(createPublicInformation());
            this.setBackGroundOnClick(publicInformationButton);
            this.updateFragment(PUBLIC_INFORMATION);
        });
        contactInformationButton.setPrefixComponent(VaadinIcon.ARCHIVE.create());
        contactInformationButton.addClickListener(event -> {
            this.mainLayout.removeAll();
            this.mainLayout.add(createContactInformation());
            this.setBackGroundOnClick(contactInformationButton);
            this.updateFragment(CONTACT_INFORMATION);
        });
        passwordButton.setPrefixComponent(VaadinIcon.PASSWORD.create());
        passwordButton.addClickListener(event -> {
            this.mainLayout.removeAll();
            this.mainLayout.add(createPassword());
            this.setBackGroundOnClick(passwordButton);
            this.updateFragment(PASSWORD);
        });
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
        this.initConfiguration(ref);
    }

    private void initConfiguration(String ref) {
        super.setMaxWidth("680px");
        super.setMaxHeight("500px");
        super.setHeight("500px");
        super.setHeaderTitle("Settings");
        this.setBackGroundOnClick(publicInformationButton);
        final Button closeButton = new Button(new Icon("lumo", "cross"), (event) -> {
            super.close();
            getUI().ifPresent(ui -> {
                ui.getPage().fetchCurrentURL(url -> {
                    ui.getPage().getHistory().replaceState(null, url.getPath());
                });
            });
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
        super.setCloseOnOutsideClick(true);

        final Main main = new Main();
        main.setId("main-settings");
        main.addClassNames(Display.FLEX, LumoUtility.FlexDirection.ROW, LumoUtility.Height.FULL);

        final Hr hr = new Hr();
        hr.addClassName("hr-header-settings");

        main.add(this.createLinks(), this.createForm(ref));

        super.add(hr, main);
        super.addClassName("settings-content-dialog");
    }

    public Component createForm(String ref) {

        String newLocation = DialogUtilsReplaceUri.INSTANCE.parseUriToCreateTheContentForm(ref);

        switch (newLocation) {
            case PUBLIC_INFORMATION -> {
                this.mainLayout.add(createPublicInformation());
                super.open();
            }
            case CONTACT_INFORMATION -> {
                this.mainLayout.add(createContactInformation());
                this.setBackGroundOnClick(contactInformationButton);
                super.open();
            }
            case PASSWORD -> {
                this.mainLayout.add(createPassword());
                this.setBackGroundOnClick(passwordButton);
                super.open();
            }
            case NOTIFICATION -> {
                this.mainLayout.add(createNotifications());
                this.setBackGroundOnClick(notificationsButton);
                super.open();
            }
            case SETTINGS -> {
                this.mainLayout.add(createPublicInformation());
                this.setBackGroundOnClick(publicInformationButton);
                super.open();
            }
            case StringUtils.EMPTY -> {
                this.mainLayout.add(createPublicInformation());
                this.setBackGroundOnClick(publicInformationButton);
            }
            default -> {
                //Do nothing
            }
        }
        this.mainLayout.addClassNames(BoxSizing.BORDER, MaxWidth.SCREEN_SMALL, Padding.LARGE);
        this.mainLayout.setFlexDirection(Layout.FlexDirection.COLUMN);
        return mainLayout;
    }

    public Component createPublicInformation() {
        H2 title = new H2("Public information");
        title.addClassNames(LumoUtility.FontSize.XLARGE, LumoUtility.Margin.Top.MEDIUM);
        title.setId(title.getText().replace(" ", "-").toLowerCase());

        Paragraph description = new Paragraph("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");
        description.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);

        Avatar avatar = new Avatar("Emily Johnson");
        avatar.addThemeVariants(AvatarVariant.LUMO_XLARGE);
        avatar.setImage("https://images.unsplash.com/photo-1529626455594-4ff0802cfb7e?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=facearea&facepad=2&w=256&h=256&q=80");

        Button uploadButton = new Button("Upload");
        uploadButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        Upload upload = new Upload();
        upload.setDropAllowed(false);
        upload.setMaxFiles(1);
        upload.setUploadButton(uploadButton);

        Button delete = new Button("Delete");
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);

        Layout avatarLayout = new Layout(avatar, upload, delete);
        avatarLayout.addClassNames(LumoUtility.Margin.Bottom.XSMALL, LumoUtility.Margin.Top.MEDIUM);
        avatarLayout.setAlignItems(Layout.AlignItems.CENTER);
        avatarLayout.setGap(Layout.Gap.LARGE);

        TextField username = new TextField("Username");
        TextField firstName = new TextField("First name");
        TextField lastName = new TextField("Last name");

        Layout layout = new Layout(title, description, avatarLayout, username, firstName, lastName);
        // Viewport < 1024px
        layout.setFlexDirection(Layout.FlexDirection.COLUMN);
        // Viewport > 1024px
        layout.setDisplay(Breakpoint.LARGE, Layout.Display.GRID);
        layout.setColumns(Layout.GridColumns.COLUMNS_2);
        layout.setColumnGap(Layout.Gap.MEDIUM);
        layout.setColumnSpan(Layout.ColumnSpan.COLUMN_SPAN_FULL, title, description, avatarLayout, username);
        return layout;
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

        Paragraph description = new Paragraph("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");
        description.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);

        CheckboxGroup<String> emailNotifications = new CheckboxGroup<>("Email notifications");
        emailNotifications.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
        emailNotifications.setItems("Newsletters", "Promotional offers", "Account updates", "New messages or activities", "Events or upcoming appointments");

        final Span pushNotifications = new Span("Push notifications");

        final Paragraph paragraphEnableAllNotifications = new Paragraph("Enable notifications in bell");

        paragraphEnableAllNotifications.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);
        final ToggleButton toggleButtonNotifications = new ToggleButton();
        final HorizontalLayout row1 = new HorizontalLayout(paragraphEnableAllNotifications, toggleButtonNotifications);
        row1.setWidthFull();
        row1.setJustifyContentMode(JustifyContentMode.BETWEEN);
        row1.setAlignItems(Alignment.CENTER);

        final Paragraph paragraphEnableAllWizards = new Paragraph("Enable all wizards dialogs");
        paragraphEnableAllWizards.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);

        final ToggleButton toggleButtonEnableAllWizards = new ToggleButton();
        final ToggleButton toggleButtonFlashEsp = new ToggleButton();
        final ToggleButton toggleButtonReadFlashEsp = new ToggleButton();

        toggleButtonEnableAllWizards.addValueChangeListener(event -> {
            if (event.getValue()) {
                this.updateAllWizardStatus(event.getValue());
                toggleButtonFlashEsp.setValue(true);
                toggleButtonReadFlashEsp.setValue(true);
            } else {
                this.updateAllWizardStatus(event.getValue());
                toggleButtonFlashEsp.setValue(false);
                toggleButtonReadFlashEsp.setValue(false);
            }
        });
        final HorizontalLayout rowEnableInitialWizardsDialogs = new HorizontalLayout(paragraphEnableAllWizards, toggleButtonEnableAllWizards);
        rowEnableInitialWizardsDialogs.setWidthFull();
        rowEnableInitialWizardsDialogs.setJustifyContentMode(JustifyContentMode.BETWEEN);
        rowEnableInitialWizardsDialogs.setAlignItems(Alignment.CENTER);

        final Paragraph spanEnableWizardFlashEsp = new Paragraph("Enable wizard Flash Esp32-ESP8266 view");
        spanEnableWizardFlashEsp.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);
        toggleButtonFlashEsp.addValueChangeListener(event -> {
            if(event.getValue()) {
                updateWizardView(WIZARD_FLASH_ESP_VIEW, true);
            } else {
                updateWizardView(WIZARD_FLASH_ESP_VIEW, false);
            }
        });
        final Paragraph spanEnableWizardReadFlashEsp = new Paragraph("Enable wizard Read flash/firmware view");
        spanEnableWizardReadFlashEsp.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);
        toggleButtonReadFlashEsp.addValueChangeListener(event -> {
            if(event.getValue()) {
                updateWizardView(WIZARD_READ_FLASH_ESP_VIEW, true);
            } else {
                updateWizardView(WIZARD_READ_FLASH_ESP_VIEW, false);
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

        final VerticalLayout verticalLayout = new VerticalLayout(row1, new Hr(), rowEnableInitialWizardsDialogs,
                new Hr(), rowWizardFlashView, new Hr(), rowWizardReadFlashView);
        verticalLayout.setPadding(false);
        verticalLayout.setSpacing(false);
        verticalLayout.getStyle().setOverflow(Overflow.HIDDEN);

        final Layout layout = new Layout(title, description, pushNotifications, verticalLayout);
        layout.setFlexDirection(Layout.FlexDirection.COLUMN);

        if(this.wizardFlashEspRepository.areAllWizardsEnabled()) {
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
        scroller.setScrollDirection(Scroller.ScrollDirection.VERTICAL);
        scroller.getStyle().set("scrollbar-width", "thin");
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

    public Component createLinks() {

        Stream.of(notificationsButton, passwordButton, publicInformationButton, contactInformationButton, updates)
                .forEach(button -> {
                    button.getStyle().setCursor(EspFlowConstants.CURSOR_POINTER);
                    button.addClassNames("settings-buttons");
                    button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
                });

        final Div div = new Div(publicInformationButton, contactInformationButton, passwordButton, notificationsButton, updates);
        div.addClassNames(Display.FLEX, LumoUtility.FlexDirection.COLUMN,
                LumoUtility.Margin.Vertical.XLARGE, Padding.Horizontal.LARGE);

        final Nav nav = new Nav(div);
        nav.addClassNames(Display.HIDDEN, Display.Breakpoint.Small.FLEX, LumoUtility.FontSize.SMALL, LumoUtility.Position.STICKY, "top-0");

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

        Stream.of(passwordButton, notificationsButton, contactInformationButton, publicInformationButton)
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
                log.info("onAttach {}", url.getPath());
                String ref = url.getRef();
                this.initConfiguration(ref);
                this.initListeners();
            });
        }
    }

}