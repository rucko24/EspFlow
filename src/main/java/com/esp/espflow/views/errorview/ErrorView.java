package com.esp.espflow.views.errorview;

import com.esp.espflow.util.EspFlowConstants;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Error view
 */
@AnonymousAllowed
public class ErrorView extends VerticalLayout implements HasErrorParameter<NotFoundException> {

    private Span explanation;

    public ErrorView() {
        super.setSizeFull();
        final H1 header = new H1("Not found!");
        super.add(header);

        explanation = new Span();

        final Image imageBrokenArdu = new Image(EspFlowConstants.FRONTEND_IMAGES_CUSTOM + "tear.png", "broken-ardu");
        imageBrokenArdu.addClassName("error-image");
        Tooltip.forComponent(imageBrokenArdu).setText("Not found!");
        imageBrokenArdu.getStyle().setCursor("pointer");
        imageBrokenArdu.addClickListener(event -> UI.getCurrent().navigate("/"));

        final Anchor anchor = new Anchor("/", "back to home...");
        anchor.addClassNames(LumoUtility.TextColor.PRIMARY, LumoUtility.FontWeight.BOLD);

        Div div = new Div(imageBrokenArdu);
        div.addClassName("div-error-image");

        super.add(explanation, div, anchor);
        super.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
    }

    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<NotFoundException> parameter) {
        explanation.setText("Could not navigate to '"
                + event.getLocation().getPath() + "'.");
        return HttpServletResponse.SC_NOT_FOUND;
    }
}