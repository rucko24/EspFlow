package com.esp.espflow.views.wizard;

import com.esp.espflow.views.settings.SettingsDialogView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RoutePrefix;
import com.vaadin.flow.router.RouterLayout;
import jakarta.annotation.security.RolesAllowed;
import lombok.extern.log4j.Log4j2;

/**
 * @author rub`n
 */
@Log4j2
@RoutePrefix("settings")
@RolesAllowed("ADMIN")
public class TestMainDialog extends VerticalLayout implements RouterLayout {

    private Div content;


    public TestMainDialog() {
        //super.setModal(true);

        //final Button buttonOk = new Button(OK, (event -> super.close()));
        //buttonOk.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        //super.getFooter().add(buttonOk);

        super.add(new SettingsDialogView());
    }

//    @Override
//    public void showRouterLayoutContent(HasElement content) {
//        if (content != null) {
//            this.content.removeAll();
//            this.content.getElement().appendChild(content.getElement());
//        }
//    }

}

