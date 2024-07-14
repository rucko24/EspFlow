package com.nodemcuui.tool.views.about;

import com.nodemcuui.tool.views.MainLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@PageTitle("About")
@Route(value = "about", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class AboutView extends VerticalLayout {

    private static final String TARGET_BLANK = "_blank";
    private static final String INNER_HTML = "innerHTML";

    public AboutView() {
        super.setSpacing(false);

        final Image img = new Image("images/ESP8285H08-2MB.jpeg", "placeholder plant");
        img.setWidth("30%");
        img.getStyle().set("border-radius", "2em");
        img.getStyle().set("border", "2px solid red");
        super.add(img);

        super.add(new H2("Some tools for esp32 family"));
        super.add(row());

        super.setSizeFull();
        super.setJustifyContentMode(JustifyContentMode.CENTER);
        super.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        super.getStyle().set("text-align", "center");
    }

    private Div row() {
        final Div row = new Div();
        row.addClassName("row-about");
        row.getStyle().set("display", "flex");
        row.getStyle().set("align-items", "baseline");
        row.getStyle().set("justify-content", "center");
        row.getStyle().set("gap", "5px");
        final Div div = new Div();
        div.getElement().setProperty(INNER_HTML, "<p>flashing tools such as</p>");
        row.add(div);
        row.add(this.esptool());
        row.add(new Span(" and "));
        row.add(this.jSerialComm());
        return row;
    }

    private Div esptool() {
        final Anchor esptoolpy = new Anchor("https://github.com/espressif/esptool");
        esptoolpy.getElement().setProperty(INNER_HTML, "<strong>esptool</strong>");
        esptoolpy.setTarget(TARGET_BLANK);
        final Div div = new Div();
        div.add(esptoolpy);
        return div;
    }

    private Div jSerialComm() {
        final Anchor jSerialComm = new Anchor("https://github.com/Fazecast/jSerialComm");
        jSerialComm.getElement().setProperty(INNER_HTML, "<strong>jSerialComm 🔥</strong>");
        jSerialComm.setTarget(TARGET_BLANK);
        final Div div = new Div();
        div.add(jSerialComm);
        return div;
    }

}
