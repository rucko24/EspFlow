package com.esp.espflow.views.settings;

import com.esp.espflow.enums.Breakpoint;
import com.esp.espflow.util.svgfactory.SvgFactory;
import com.esp.espflow.views.Layout;
import com.esp.espflow.views.MainLayout;
import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
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
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RoutePrefix;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.theme.lumo.LumoUtility.BoxSizing;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.MaxWidth;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import jakarta.annotation.security.RolesAllowed;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author rub'n
 */
@Log4j2
@Route(value = "", layout = MainLayout.class)
@RoutePrefix(value = "#settings")
@RolesAllowed("ADMIN")
public class SettingsDialogView extends Dialog implements RouterLayout, HasUrlParameter<String> {

    private static final String PASSWORD = "password";
    private static final String CONTACT_INFORMATION = "contact-information";
    private static final String PUBLIC_INFORMATION = "public-information";
    private static final String NOTIFICATION = "notifications";

    private final Layout mainLayout = new Layout();

    public SettingsDialogView() {
        super.setMaxWidth("680px");
        super.setMaxHeight("500px");
        super.setHeight("500px");
        super.setHeaderTitle("Settings");
        super.setModal(true);
        final Button closeButton = new Button(new Icon("lumo", "cross"), (event) -> {
            super.close();
            getUI().ifPresent(ui -> {
                ui.getPage().fetchCurrentURL(url -> {
                    ui.getPage().getHistory().replaceState(null, url.getPath());
                });
            });
        });

        /*
         * Allows that when closing the dialog, we remove the portion of the uri after the and leaving the original path.
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

        super.getHeader().add(closeButton);
        super.setCloseOnOutsideClick(true);

        final Main main = new Main();
        main.setId("main-settings");
        main.addClassNames(Display.FLEX, LumoUtility.FlexDirection.ROW, LumoUtility.Height.FULL);

        final Hr hr = new Hr();
        hr.addClassName("hr-header-settings");

        main.add(this.createLinks(), this.createForm());

        super.add(hr, main);
        super.addClassName("settings-content-dialog");
    }

    public Component createForm() {
        //Not the first time
        this.mainLayout.add(createPublicInformation());
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
        //pushNotifications.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);

        final Paragraph spanEnableAllNotifications = new Paragraph("Enable all notifications");
        spanEnableAllNotifications.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);
        final ToggleButton toggleButtonNotifications = new ToggleButton();
        final HorizontalLayout row1 = new HorizontalLayout(spanEnableAllNotifications, toggleButtonNotifications);
        row1.setWidthFull();
        row1.setJustifyContentMode(JustifyContentMode.BETWEEN);
        row1.setAlignItems(Alignment.CENTER);

        final Paragraph spanEnableInitialDialogs = new Paragraph("Enable initial dialogs");
        spanEnableInitialDialogs.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);
        final ToggleButton toggleButtonEnableInitialDialogs = new ToggleButton();
        final HorizontalLayout row2 = new HorizontalLayout(spanEnableInitialDialogs, toggleButtonEnableInitialDialogs);
        row2.setWidthFull();
        row2.setJustifyContentMode(JustifyContentMode.BETWEEN);
        row2.setAlignItems(Alignment.CENTER);

        final VerticalLayout verticalLayout = new VerticalLayout(row1, new Hr(), row2);
        verticalLayout.setPadding(false);
        verticalLayout.setSpacing(false);
        verticalLayout.getStyle().setOverflow(Overflow.HIDDEN);

        final Layout layout = new Layout(title, description, pushNotifications, verticalLayout);
        layout.setFlexDirection(Layout.FlexDirection.COLUMN);
        return new Scroller(layout);
    }

    public Component createLinks() {
        final Button publicInformationButton = new Button("Public Information");
        final Button contactInformationButton = new Button("Contact information");
        final Button passwordButton = new Button("Password");
        final Button notificationsButton = new Button("Notifications");

        publicInformationButton.setPrefixComponent(VaadinIcon.INFO.create());
        publicInformationButton.addClickListener(event -> {
            this.mainLayout.removeAll();
            this.mainLayout.add(createPublicInformation());

            this.setBackGroundOnClick(publicInformationButton, contactInformationButton,
                    passwordButton, notificationsButton);

            getUI().ifPresent(ui -> {
                ui.getPage().fetchCurrentURL(url -> {

                    String baseUrl = RouteConfiguration.forSessionScope().getUrl(SettingsDialogView.class, PUBLIC_INFORMATION);
                    String urlWithParameters = url.getPath().concat(baseUrl);

                    ui.getPage().getHistory().replaceState(null, urlWithParameters);

                });
            });


        });

        contactInformationButton.setPrefixComponent(VaadinIcon.ARCHIVE.create());
        contactInformationButton.addClickListener(event -> {
            this.mainLayout.removeAll();
            this.mainLayout.add(createContactInformation());

            this.setBackGroundOnClick(contactInformationButton, publicInformationButton,
                    passwordButton, notificationsButton);

            getUI().ifPresent(ui -> {
                ui.getPage().fetchCurrentURL(url -> {

                    String baseUrl = RouteConfiguration.forSessionScope().getUrl(SettingsDialogView.class, CONTACT_INFORMATION);
                    String urlWithParameters = url.getPath().concat(baseUrl);

                    ui.getPage().getHistory().replaceState(null, urlWithParameters);

                });
            });

        });

        passwordButton.setPrefixComponent(VaadinIcon.PASSWORD.create());
        passwordButton.addClickListener(event -> {
            this.mainLayout.removeAll();
            this.mainLayout.add(createPassword());

            this.setBackGroundOnClick(passwordButton, contactInformationButton, publicInformationButton,
                    notificationsButton);

            getUI().ifPresent(ui -> {
                ui.getPage().fetchCurrentURL(url -> {

                    String baseUrl = RouteConfiguration.forSessionScope().getUrl(SettingsDialogView.class, PASSWORD);
                    String urlWithParameters = url.getPath().concat(baseUrl);

                    ui.getPage().getHistory().replaceState(null, urlWithParameters);

                });
            });

        });

        notificationsButton.setPrefixComponent(SvgFactory
                .createIconFromSvg("bell.svg", "16px", null));

        notificationsButton.addClickListener(event -> {
            this.mainLayout.removeAll();
            this.mainLayout.add(new Scroller(createNotifications()));

            this.setBackGroundOnClick(notificationsButton, contactInformationButton, passwordButton, publicInformationButton);

            getUI().ifPresent(ui -> {
                ui.getPage().fetchCurrentURL(url -> {

                    String baseUrl = RouteConfiguration.forSessionScope().getUrl(SettingsDialogView.class, NOTIFICATION);
                    String urlWithParameters = url.getPath().concat(baseUrl);

                    ui.getPage().getHistory().replaceState(null, urlWithParameters);

                });
            });

        });

        Stream.of(notificationsButton, passwordButton, publicInformationButton, contactInformationButton)
                .forEach(button -> {
                    button.getStyle().setCursor("pointer");
                    //button.getStyle().setBorder("1px solid lightgray");
                    //button.getStyle().setBorderRadius("10px");
                    button.addClassNames("settings-buttons");
                    button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
                });

        final Div div = new Div(publicInformationButton, contactInformationButton, passwordButton, notificationsButton);
        div.addClassNames(Display.FLEX, LumoUtility.FlexDirection.COLUMN,
                LumoUtility.Margin.Vertical.XLARGE, Padding.Horizontal.LARGE);

        final Nav nav = new Nav(div);
        nav.addClassNames(Display.HIDDEN, Display.Breakpoint.Small.FLEX, LumoUtility.FontSize.SMALL, LumoUtility.Position.STICKY, "top-0");

        return nav;
    }

    /**
     *
     * @param button that only the background will be set
     * @param removeBackgroundColorForThisButtons to remove the background
     */
    private void setBackGroundOnClick(Button button, Button... removeBackgroundColorForThisButtons) {
        var backgroundColor = button.getStyle().get("background-color");
        if (Objects.isNull(backgroundColor)) {
            button.getStyle().setBackgroundColor("var(--lumo-primary-color-10pct)");
        }

        Stream.of(removeBackgroundColorForThisButtons)
                .forEach(buttonItem -> {
                    buttonItem.getStyle().remove("background-color");
                });

    }

    @Override
    public void showRouterLayoutContent(HasElement content) {
        if (Objects.nonNull(content)) {
            this.mainLayout.removeAll();
            this.mainLayout.getElement().appendChild(content.getElement());
        }
    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        final UI ui = attachEvent.getUI();
        ui.getPage().fetchCurrentURL(url -> {
            log.info("Current URL Setting Dialog SettingsDialog {}", url);

            String baseUrl = RouteConfiguration.forSessionScope()
                    .getUrl(SettingsDialogView.class, "").replace("/", "");
            String urlWithParameters = url.getPath().concat(baseUrl);

            this.recreateContentAndUpdateUrl(ui, urlWithParameters, url.toString());

        });

    }

    private void recreateContentAndUpdateUrl(final UI ui, String urlWithParameters, String fetchUrl) {
        String newLocation = StringUtils.EMPTY;
        if (fetchUrl.contains("#")) {
            try {
                newLocation = fetchUrl.split("#")[1].split("/")[1];
            } catch (Exception ex) {
                log.info("fetchUrl error {} ", fetchUrl);
            }
        }
        log.info("newLocation {}", newLocation);
        switch (newLocation) {
            case PUBLIC_INFORMATION -> {
                this.mainLayout.removeAll();
                this.mainLayout.add(createPublicInformation());
                ui.getPage().getHistory().replaceState(null, urlWithParameters.concat("/" + newLocation));
            }
            case CONTACT_INFORMATION -> {
                this.mainLayout.removeAll();
                this.mainLayout.add(createContactInformation());
                ui.getPage().getHistory().replaceState(null, urlWithParameters.concat("/" + newLocation));
            }
            case PASSWORD -> {
                this.mainLayout.removeAll();
                this.mainLayout.add(createPassword());
                ui.getPage().getHistory().replaceState(null, urlWithParameters.concat("/" + newLocation));
            }
            case NOTIFICATION -> {
                this.mainLayout.removeAll();
                this.mainLayout.add(createNotifications());
                ui.getPage().getHistory().replaceState(null, urlWithParameters.concat("/" + newLocation));
            }
            default -> {
            }
        }
    }

}