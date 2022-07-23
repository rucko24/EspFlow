package com.nodemcutools.application.views.about;

import com.nodemcutools.application.views.MainLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@PageTitle("About")
@Route(value = "about", layout = MainLayout.class)
@AnonymousAllowed
public class AboutView extends VerticalLayout {

    public AboutView() {
        setSpacing(false);

        Image img = new Image("images/ESP8285H08.jpeg", "placeholder plant");
        img.setWidth("30%");
        img.getStyle().set("border-radius","2em");
        img.getStyle().set("border","2px solid red");
        add(img);

        add(new H2("Some tools for esp32 family"));
        add(new Paragraph("like flashing tool with esptool.py and JSSC for com port 🔥"));

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
    }

}
