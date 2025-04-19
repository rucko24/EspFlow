package com.esp.espflow.util;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;

/**
 * Responsive header
 */
public interface ResponsiveHeaderDiv {

    default Div createDiv(final Component component, final String margin, String value) {
        final Div div = new Div(component);
        div.getStyle().set(margin, value);
        return div;
    }

}
