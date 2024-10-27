package com.esp.espflow.util;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;

import java.util.stream.Stream;

/**
 * Responsive header
 */
public interface ResponsiveHeaderDiv {

    /**
     *
     * @param titulo, h2
     * @return Div con h2 centrado
     */
    default Div createDiv( final Component component, final String titulo ) {
        final Div div = new Div();
        div.addClassName("center-titulos");
        final Div divWithBorder = new Div(component);
        component.getElement().getStyle().set("margin","10px 10px");
        final H3 h3Titulo = new H3(titulo);
        h3Titulo.addClassName("color-titles");
        div.add(h3Titulo,divWithBorder);
        return div;
    }

    /**
     *
     * @return Div with space around
     */
    default Div createHeaderDivWithSpaceAround(final Component... components) {
        final Div mainDivWithSpaceAround = new Div();
        //Se aplicara estilo en 800px de ancho de pantalla
        mainDivWithSpaceAround.addClassName("div-header-with-space-around");
        Stream.of(components).forEach(mainDivWithSpaceAround::add);
        return mainDivWithSpaceAround;
    }

    default Div createDiv(final Component component, final String margin, String value) {
        final Div div = new Div(component);
        div.getStyle().set(margin, value);
        return div;
    }

}
