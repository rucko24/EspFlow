package com.esp.espflow.views.settings;

import com.esp.espflow.enums.Breakpoint;
import com.esp.espflow.util.EspFlowConstants;
import com.esp.espflow.views.Layout;
import com.esp.espflow.views.MainLayout;
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
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
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
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.stream.Stream;

import static com.esp.espflow.util.EspFlowConstants.OK;

/**
 * Crear navegacion con / sin el hash #, con las uri / en cada componente del layout no navega cuando entramos por F5 GET al Chrome
 * raro, pero si desde el SideNav
 * <p>
 * https://vaadin.com/docs/latest/flow/routing/updating-url-parameters
 */
@Log4j2
//@PageTitle("Settings")
@Route(value = "", layout = MainLayout.class)
@RoutePrefix(value = "#settings")
@RolesAllowed("ADMIN")
public class SettingsDialogView extends Dialog implements RouterLayout, AfterNavigationObserver, BeforeEnterObserver, HasUrlParameter<String> {

    private Layout mainLayout = new Layout();

    public SettingsDialogView() {
        init();
    }

    private void init() {
        this.setHeaderTitle("Settings");
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
        final Button buttonOk = new Button(OK, (event -> super.close()));
        buttonOk.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        super.getFooter().add(buttonOk);
        super.setCloseOnOutsideClick(true);

        final Main main = new Main();
        main.addClassNames(Display.FLEX, LumoUtility.FlexDirection.ROW, LumoUtility.Height.FULL);
        main.add(createLinks(), createForm());

        super.add(main);
    }


    public Component createForm() {
        //Not the first time
        this.mainLayout.add(createPublicInformation());
        this.mainLayout.addClassNames(BoxSizing.BORDER, MaxWidth.SCREEN_SMALL, Padding.LARGE);
        this.mainLayout.setFlexDirection(Layout.FlexDirection.COLUMN);
        return mainLayout;
    }


//    private Div createContent() {
//        this.content = new Div();
//        this.content.addClassNames("flex-1", LumoUtility.Overflow.AUTO);
//        return this.content;
//    }

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

        CheckboxGroup<String> pushNotifications = new CheckboxGroup<>("Push notifications");
        pushNotifications.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
        pushNotifications.setItems("New messages", "Friend requests", "Activity updates", "Order status updates", "Reminders or alerts");

        Layout layout = new Layout(title, description, emailNotifications, pushNotifications);
        layout.setFlexDirection(Layout.FlexDirection.COLUMN);
        return layout;
    }

    public Component createLinks() {

        Button publicInformationButton = new Button("Public Information");
        publicInformationButton.setPrefixComponent(VaadinIcon.INFO.create());
        publicInformationButton.addClickListener(event -> {
            this.mainLayout.removeAll();
            this.mainLayout.add(createPublicInformation());

            getUI().ifPresent(ui -> {
                ui.getPage().fetchCurrentURL(url -> {

                    String baseUrl = RouteConfiguration.forSessionScope().getUrl(SettingsDialogView.class, "public-information");
                    String urlWithParameters = url.getPath().concat(baseUrl);

                    ui.getPage().getHistory().replaceState(null, urlWithParameters);

                });
            });


        });

        Button contactInformationButton = new Button("Contact information");
        contactInformationButton.setPrefixComponent(VaadinIcon.ARCHIVE.create());
        contactInformationButton.addClickListener(event -> {
            this.mainLayout.removeAll();
            this.mainLayout.add(createContactInformation());

            getUI().ifPresent(ui -> {
                ui.getPage().fetchCurrentURL(url -> {

                    String baseUrl = RouteConfiguration.forSessionScope().getUrl(SettingsDialogView.class, "contact");
                    String urlWithParameters = url.getPath().concat(baseUrl);

                    ui.getPage().getHistory().replaceState(null, urlWithParameters);

                });
            });

        });

        Button passwordButton = new Button("Password");
        passwordButton.setPrefixComponent(VaadinIcon.PASSWORD.create());
        passwordButton.addClickListener(event -> {
            this.mainLayout.removeAll();
            this.mainLayout.add(createPassword());

            getUI().ifPresent(ui -> {
                ui.getPage().fetchCurrentURL(url -> {

                    String baseUrl = RouteConfiguration.forSessionScope().getUrl(SettingsDialogView.class, "password");
                    String urlWithParameters = url.getPath().concat(baseUrl);

                    ui.getPage().getHistory().replaceState(null, urlWithParameters);

                });
            });

        });

        Button notificationsButton = new Button("Notifications");
        notificationsButton.setPrefixComponent(VaadinIcon.BELL_SLASH_O.create());
        notificationsButton.addClickListener(event -> {
            this.mainLayout.removeAll();
            this.mainLayout.add(createNotifications());

            getUI().ifPresent(ui -> {
                ui.getPage().fetchCurrentURL(url -> {

                    String baseUrl = RouteConfiguration.forSessionScope().getUrl(SettingsDialogView.class, "notifications");
                    String urlWithParameters = url.getPath().concat(baseUrl);

                    ui.getPage().getHistory().replaceState(null, urlWithParameters);

                });
            });

        });

        Stream.of(notificationsButton, passwordButton, publicInformationButton, contactInformationButton)
                .forEach(button -> button.addClassNames(EspFlowConstants.BOX_SHADOW_VAADIN_BUTTON));

        final Div div = new Div(publicInformationButton, contactInformationButton, passwordButton, notificationsButton);
        div.addClassNames(Display.FLEX, LumoUtility.FlexDirection.COLUMN, LumoUtility.Margin.Vertical.XLARGE, Padding.Horizontal.LARGE);

        final Nav nav = new Nav(div);
        nav.addClassNames(Display.HIDDEN, Display.Breakpoint.Small.FLEX, LumoUtility.FontSize.SMALL, LumoUtility.Position.STICKY, "top-0");

        return nav;
    }

    @Override
    public void showRouterLayoutContent(HasElement content) {
        if (content != null) {
            this.mainLayout.removeAll();
            this.mainLayout.getElement().appendChild(content.getElement());
        }
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        String path = event.getLocation().getPath();
        if ((!path.contains("read-flash")) && (!path.contains("flash-esp")) && !(path.contains("about"))) {
            UI.getCurrent().getPage().executeJs(
                    "if (window.location.hash) { " +
                            "  var hash = window.location.hash.substring(1); " +  // Elimina el carácter '#'
                            "  return hash; " +
                            "} else { " +
                            "  return ''; " +  // Devuelve una cadena vacía si no hay fragmento
                            "}"
            ).then(String.class, hash -> {
                System.out.println("Fragmento de URI SettingsDialogView: " + hash);
                System.out.println("Path de URI SettingsDialogView: " + path);
                // Aquí puedes usar el fragmento 'hash' según lo necesites

                switch (hash) {
                    case "public-information" -> {
                        this.mainLayout.removeAll();
                        this.mainLayout.add(createPublicInformation());
                    }
                    case "contact-information" -> {
                        this.mainLayout.removeAll();
                        this.mainLayout.add(createContactInformation());
                    }
                    case "password" -> {
                        this.mainLayout.removeAll();
                        this.mainLayout.add(createPassword());
                    }
                    case "notifications" -> {
                        this.mainLayout.removeAll();
                        this.mainLayout.add(createNotifications());
                    }
                }

            });
        }


    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        //if (attachEvent.isInitialAttach()) {
        final UI ui = attachEvent.getUI();

        ui.getPage().fetchCurrentURL(url -> {
            System.out.println("Current URL Setting Dialog SettingsDialog " + url);

            String baseUrl = RouteConfiguration.forSessionScope().getUrl(SettingsDialogView.class, "")
                    .replace("/","");
            String urlWithParameters = url.getPath().concat(baseUrl);

            ui.getPage().getHistory().replaceState(null, urlWithParameters);

            //super.open();

        });

    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        log.info("Before enter SettingsDialogs{} ", event.getLocation());
    }
}