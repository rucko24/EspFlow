package com.esp.espflow.views.wizard;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import jakarta.annotation.security.RolesAllowed;

@PageTitle("Step 1 - Wizard")
@Route(value = "1", layout = WizardDiaglogLayout.class)
@RolesAllowed("ADMIN")
public class Step1View extends Div {

    public Step1View() {
        addClassNames(Padding.Horizontal.LARGE, Padding.Vertical.MEDIUM);

        H2 heading = new H2("Step 1");
        add(heading);
    }


}