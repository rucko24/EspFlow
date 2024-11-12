package com.esp.espflow.views.wizard;

import com.esp.espflow.util.svgfactory.SvgFactory;
import com.esp.espflow.views.MainLayout;
import com.esp.espflow.views.settings.SettingsDialogView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.RoutePrefix;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;
import lombok.extern.log4j.Log4j2;

import static com.esp.espflow.util.EspFlowConstants.FREE_BSD_ICON;
import static com.esp.espflow.util.EspFlowConstants.LINUX_ICON;
import static com.esp.espflow.util.EspFlowConstants.MACOS_ICON;
import static com.esp.espflow.util.EspFlowConstants.OK;
import static com.esp.espflow.util.EspFlowConstants.WINDOWS_ICON;

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

