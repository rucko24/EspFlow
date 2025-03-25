package com.esp.espflow.views.hexdump;

import com.esp.espflow.views.MainLayout;
import com.infraleap.animatecss.Animated;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import static com.esp.espflow.util.EspFlowConstants.BOX_SHADOW_PROPERTY;
import static com.esp.espflow.util.EspFlowConstants.BOX_SHADOW_VALUE;
import static com.esp.espflow.util.EspFlowConstants.CURSOR_POINTER;
import static com.esp.espflow.util.EspFlowConstants.ESPFLOW_SOURCE_CODE;
import static com.esp.espflow.util.EspFlowConstants.FRONTEND_IMAGES_ABOUT;
import static com.esp.espflow.util.EspFlowConstants.INNER_HTML;

@Log4j2
@UIScope
@SpringComponent
@PageTitle("HexDump")
@Route(value = "hex-dump", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@RequiredArgsConstructor
public class HexDumpView extends VerticalLayout {


    @PostConstruct
    private void init() {
        super.setSizeFull();


        Animated.animate(this, Animated.Animation.FADE_IN);
    }


    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        if(attachEvent.isInitialAttach()) {

        }
    }
}
