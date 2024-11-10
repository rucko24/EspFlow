package com.esp.espflow.views.wizard;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import jakarta.annotation.security.RolesAllowed;

@PageTitle("Step 4 - Wizard")
@Route(value = "4", layout = WizardDiaglogLayout.class)
@RolesAllowed("ADMIN")
public class Step4View extends Div {

    public Step4View() {
        addClassNames(Padding.Horizontal.LARGE, Padding.Vertical.MEDIUM);

        H2 heading = new H2("Step 4");
        add(heading);
    }


}