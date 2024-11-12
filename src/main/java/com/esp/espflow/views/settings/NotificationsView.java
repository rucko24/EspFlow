package com.esp.espflow.views.settings;

import com.esp.espflow.views.Layout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;
import lombok.extern.log4j.Log4j2;

@Log4j2
@UIScope
@SpringComponent
@PageTitle("NotificationsView")
//@Route(value = "notifications", layout = SettingsDialogView.class)
@RolesAllowed("ADMIN")
public class NotificationsView extends Div {

    public NotificationsView() {
//        addClassNames(Padding.Horizontal.LARGE, Padding.Vertical.MEDIUM);
//
//        H2 heading = new H2("Step 4");
        add(createNotifications());
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

}