package com.esp.espflow.views.settings;

import com.esp.espflow.views.Layout;
import com.esp.espflow.views.MainLayout;
import com.esp.espflow.views.steppers.Step;
import com.esp.espflow.views.steppers.Stepper;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.RoutePrefix;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.BoxSizing;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.MaxWidth;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * Crear navegacion con / sin el hash #, con las uri / en cada componente del layout no navega cuando entramos por F5 GET al Chrome
 * raro, pero si desde el SideNav
 */
@Log4j2
@UIScope
@SpringComponent
@PageTitle("Settings")
@ParentLayout(MainLayout.class)
@RoutePrefix(value = "settings")
@RolesAllowed("ADMIN")
public class SettingsDialogView extends Dialog implements RouterLayout, AfterNavigationObserver {

    private Div content;

    public SettingsDialogView() {
        //addClassNames(AlignItems.START, Display.FLEX);
        addClassNames(Display.FLEX, LumoUtility.FlexDirection.COLUMN, LumoUtility.Height.FULL);
        this.setHeaderTitle("Settings");
        final Button closeButton = new Button(new Icon("lumo", "cross"),
                (event) -> super.close());
        super.getHeader().add(closeButton);

        super.add(createLinks(), createContent());
        open();
    }

//    public Component createForm() {
//        //Not the first time
//        //this.mainLayout.add(createPublicInformation());
//        this.mainLayout.addClassNames(BoxSizing.BORDER, MaxWidth.SCREEN_SMALL, Padding.LARGE);
//        this.mainLayout.setFlexDirection(Layout.FlexDirection.COLUMN);
//        return mainLayout;
//    }

    private Div createContent() {
        this.content = new Div();
        this.content.addClassNames("flex-1", LumoUtility.Overflow.AUTO);
        return this.content;
    }


//    private Div createContent() {
//        this.content = new Div();
//        this.content.addClassNames("flex-1", LumoUtility.Overflow.AUTO);
//        return this.content;
//    }

//    public Component createPublicInformation() {
//        H2 title = new H2("Public information");
//        title.addClassNames(FontSize.XLARGE, Margin.Top.MEDIUM);
//        title.setId(title.getText().replace(" ", "-").toLowerCase());
//
//        Paragraph description = new Paragraph("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");
//        description.addClassNames(FontSize.SMALL, TextColor.SECONDARY);
//
//        Avatar avatar = new Avatar("Emily Johnson");
//        avatar.addThemeVariants(AvatarVariant.LUMO_XLARGE);
//        avatar.setImage("https://images.unsplash.com/photo-1529626455594-4ff0802cfb7e?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=facearea&facepad=2&w=256&h=256&q=80");
//
//        Button uploadButton = new Button("Upload");
//        uploadButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
//
//        Upload upload = new Upload();
//        upload.setDropAllowed(false);
//        upload.setMaxFiles(1);
//        upload.setUploadButton(uploadButton);
//
//        Button delete = new Button("Delete");
//        delete.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
//
//        Layout avatarLayout = new Layout(avatar, upload, delete);
//        avatarLayout.addClassNames(Margin.Bottom.XSMALL, Margin.Top.MEDIUM);
//        avatarLayout.setAlignItems(Layout.AlignItems.CENTER);
//        avatarLayout.setGap(Layout.Gap.LARGE);
//
//        TextField username = new TextField("Username");
//        TextField firstName = new TextField("First name");
//        TextField lastName = new TextField("Last name");
//
//        Layout layout = new Layout(title, description, avatarLayout, username, firstName, lastName);
//        // Viewport < 1024px
//        layout.setFlexDirection(Layout.FlexDirection.COLUMN);
//        // Viewport > 1024px
//        layout.setDisplay(Breakpoint.LARGE, Layout.Display.GRID);
//        layout.setColumns(Layout.GridColumns.COLUMNS_2);
//        layout.setColumnGap(Layout.Gap.MEDIUM);
//        layout.setColumnSpan(Layout.ColumnSpan.COLUMN_SPAN_FULL, title, description, avatarLayout, username);
//        return layout;
//    }
//
//    public Component createContactInformation() {
//        H2 title = new H2("Contact information");
//        title.addClassNames(FontSize.XLARGE, Margin.Top.MEDIUM);
//        title.setId(title.getText().replace(" ", "-").toLowerCase());
//
//        Paragraph description = new Paragraph("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");
//        description.addClassNames(FontSize.SMALL, TextColor.SECONDARY);
//
//        TextField address = new TextField("Address");
//        TextField city = new TextField("City");
//        ComboBox<String> state = new ComboBox<>("State");
//        TextField zip = new TextField("ZIP");
//
//        TextField phone = new TextField("Phone");
//        phone.setPrefixComponent(LineAwesomeIcon.PHONE_SOLID.create());
//
//        EmailField email = new EmailField("Email");
//        email.setPrefixComponent(LineAwesomeIcon.ENVELOPE.create());
//
//        Layout layout = new Layout(title, description, address, city, state, zip, phone, email);
//        // Viewport < 1024px
//        layout.setFlexDirection(Layout.FlexDirection.COLUMN);
//        // Viewport > 1024px
//        layout.setDisplay(Breakpoint.LARGE, Layout.Display.GRID);
//        layout.setColumns(Layout.GridColumns.COLUMNS_4);
//        layout.setColumnGap(Layout.Gap.MEDIUM);
//        layout.setColumnSpan(Layout.ColumnSpan.COLUMN_SPAN_2, city, phone, email);
//        layout.setColumnSpan(Layout.ColumnSpan.COLUMN_SPAN_FULL, title, description, address);
//        String baseUrl = RouteConfiguration.forSessionScope().getUrl(getClass());
//        String urlWithParameters = baseUrl + "/contact-information";
//        getUI().ifPresent(ui -> {
//            ui.getPage().getHistory().replaceState(null, urlWithParameters);
//        });
//        return layout;
//    }
//
//    public Component createPassword() {
//        H2 title = new H2("Password");
//        title.addClassNames(FontSize.XLARGE, Margin.Top.MEDIUM);
//        title.setId(title.getText().replace(" ", "-").toLowerCase());
//
//        Paragraph description = new Paragraph("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");
//        description.addClassNames(FontSize.SMALL, TextColor.SECONDARY);
//
//        TextField currentPassword = new TextField("Current password");
//        TextField newPassword = new TextField("New password");
//        TextField confirmPassword = new TextField("Confirm password");
//
//        Layout layout = new Layout(title, description, currentPassword, newPassword, confirmPassword);
//        layout.setFlexDirection(Layout.FlexDirection.COLUMN);
//        return layout;
//    }
//
//    public Component createNotifications() {
//        H2 title = new H2("Notifications");
//        title.addClassNames(FontSize.XLARGE, Margin.Top.MEDIUM);
//        title.setId(title.getText().replace(" ", "-").toLowerCase());
//
//        Paragraph description = new Paragraph("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");
//        description.addClassNames(FontSize.SMALL, TextColor.SECONDARY);
//
//        CheckboxGroup<String> emailNotifications = new CheckboxGroup<>("Email notifications");
//        emailNotifications.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
//        emailNotifications.setItems("Newsletters", "Promotional offers", "Account updates", "New messages or activities", "Events or upcoming appointments");
//
//        CheckboxGroup<String> pushNotifications = new CheckboxGroup<>("Push notifications");
//        pushNotifications.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
//        pushNotifications.setItems("New messages", "Friend requests", "Activity updates", "Order status updates", "Reminders or alerts");
//
//        Layout layout = new Layout(title, description, emailNotifications, pushNotifications);
//        layout.setFlexDirection(Layout.FlexDirection.COLUMN);
//        return layout;
//    }

    public Component createLinks() {

//        Button publicInformationButton = new Button("Public Information");
//        publicInformationButton.setPrefixComponent(VaadinIcon.INFO.create());
//        publicInformationButton.addClickListener(event -> {
//            this.mainLayout.removeAll();
//            this.mainLayout.add(this.publicInformationView.createPublicInformation());
//
//            String baseUrl = RouteConfiguration.forSessionScope().getUrl(getClass());
//            String urlWithParameters = baseUrl + "/public-information";
//            getUI().ifPresent(ui -> {
//                ui.getPage().getHistory().replaceState(null, urlWithParameters);
//            });
//
//        });
//
//        Button contactInformationButton = new Button("Contact information");
//        contactInformationButton.setPrefixComponent(VaadinIcon.ARCHIVE.create());
//        contactInformationButton.addClickListener(event -> {
//            this.mainLayout.removeAll();
//            this.mainLayout.add(createContactInformation());
//
//            String baseUrl = RouteConfiguration.forSessionScope().getUrl(getClass());
//            String urlWithParameters = baseUrl + "/contact-information";
//            getUI().ifPresent(ui -> {
//                ui.getPage().getHistory().replaceState(null, urlWithParameters);
//            });
//
//        });
//
//        Button passwordButton = new Button("Password");
//        passwordButton.setPrefixComponent(VaadinIcon.PASSWORD.create());
//        passwordButton.addClickListener(event -> {
//            this.mainLayout.removeAll();
//            this.mainLayout.add(createPassword());
//
//            String baseUrl = RouteConfiguration.forSessionScope().getUrl(getClass());
//            String urlWithParameters = baseUrl + "/password";
//            getUI().ifPresent(ui -> {
//                ui.getPage().getHistory().replaceState(null, urlWithParameters);
//            });
//
//        });
//
//        Button notificationsButton = new Button("Notifications");
//        notificationsButton.setPrefixComponent(VaadinIcon.BELL_SLASH_O.create());
//        notificationsButton.addClickListener(event -> {
//            this.mainLayout.removeAll();
//            this.mainLayout.add(createNotifications());
//
//            String baseUrl = RouteConfiguration.forSessionScope().getUrl(getClass());
//            String urlWithParameters = baseUrl + "/notifications";
//            getUI().ifPresent(ui -> {
//                ui.getPage().getHistory().replaceState(null, urlWithParameters);
//            });
//        });
//
//        Stream.of(notificationsButton, passwordButton, publicInformationButton, contactInformationButton)
//                .forEach(button -> button.addClassNames(EspFlowConstants.BOX_SHADOW_VAADIN_BUTTON));
//
////        final VerticalLayout verticalLayout = new VerticalLayout(publicInformationButton, contactInformationButton, passwordButton, notificationsButton);
////        verticalLayout.addClassNames(Margin.Vertical.XLARGE, Padding.Horizontal.LARGE);
//
//        final Div div = new Div(publicInformationButton, contactInformationButton, passwordButton, notificationsButton);
//        div.addClassNames(Display.FLEX, LumoUtility.FlexDirection.COLUMN, Margin.Vertical.XLARGE, Padding.Horizontal.LARGE);

        Step step1 = new Step("Public information", PublicInformationView.class);
        Step step2 = new Step("Contact Information", ContactInformationView.class);
        Step step3 = new Step("Password", PasswordView.class);
        Step step4 = new Step("Notifications", NotificationsView.class);

        final Stepper stepper = new Stepper(step1, step2, step3, step4);
        stepper.addClassNames(BoxSizing.BORDER, MaxWidth.SCREEN_SMALL, Padding.MEDIUM);
        stepper.setOrientation(Stepper.Orientation.VERTICAL);
        stepper.setSmall(true);
        //final Nav nav = new Nav(div);
        //nav.addClassNames(Display.HIDDEN, Display.Breakpoint.Small.FLEX, FontSize.SMALL, Position.STICKY, "top-0");


//        final SideNav sideNav = new SideNav();
//        SideNavItem sideNavItem = new SideNavItem("Public Information","profile#public-information");
//        SideNavItem sideNavItem1 = new SideNavItem("Contact information","profile#contact-information");
//        SideNavItem sideNavItem2 = new SideNavItem("Password","profile#password");
//        SideNavItem sideNavItem3 = new SideNavItem("Notifications","profile#notifications");
//        sideNav.addItem(sideNavItem, sideNavItem1, sideNavItem2, sideNavItem3);
//        sideNav.addClassNames(Display.HIDDEN, Display.Breakpoint.Small.FLEX, FontSize.SMALL, Position.STICKY, "top-0");

        return stepper;
    }

    @Override
    public void showRouterLayoutContent(HasElement content) {
        if (content != null) {
            this.content.removeAll();
            this.content.getElement().appendChild(content.getElement());
        }
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        if (event.getLocation().getPath().contains("settings/public-information")) {
            //this.previous.getElement().removeAttribute("href");
            //this.next.setRoute(Step2View.class);

        } else if (event.getLocation().getPath().contains("settings/contact-information")) {
            //this.previous.setRoute(Step1View.class);
            //this.next.setRoute(Step3View.class);

        } else if (event.getLocation().getPath().contains("settings/password")) {
            //this.previous.setRoute(Step2View.class);
            //this.next.setRoute(Step4View.class);

        } else if (event.getLocation().getPath().contains("settings/notifications")) {
            //this.previous.setRoute(Step3View.class);
            //this.next.setRoute(FlashEspView.class);
        }
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
            //this.mainLayout.add(createPublicInformation());

        }
    }

}