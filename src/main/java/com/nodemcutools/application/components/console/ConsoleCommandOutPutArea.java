package com.nodemcutools.application.components.console;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextAreaVariant;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin.Bottom;
import lombok.extern.log4j.Log4j2;

import static com.nodemcutools.application.data.util.UiToolConstants.AUTO;
import static com.nodemcutools.application.data.util.UiToolConstants.BOX_SHADOW_PROPERTY;
import static com.nodemcutools.application.data.util.UiToolConstants.BOX_SHADOW_VALUE;
import static com.nodemcutools.application.data.util.UiToolConstants.DISPLAY;
import static com.nodemcutools.application.data.util.UiToolConstants.HIDDEN;
import static com.nodemcutools.application.data.util.UiToolConstants.MARGIN_10_PX;
import static com.nodemcutools.application.data.util.UiToolConstants.MARGIN_LEFT;
import static com.nodemcutools.application.data.util.UiToolConstants.MARGIN_TOP;
import static com.nodemcutools.application.data.util.UiToolConstants.OVERFLOW_Y;

/**
 * @author rubn
 */
@Log4j2
public class ConsoleCommandOutPutArea extends Div {

    private final TextArea textAreaConsoleOutput;

    public ConsoleCommandOutPutArea() {
        this.textAreaConsoleOutput = new TextArea();
        this.getConsole();
    }

    private void getConsole() {
        final H3 h3 = new H3("Console");
        h3.getStyle().set(MARGIN_TOP, AUTO);
        final Div divH3 = new Div(h3);

        textAreaConsoleOutput.setWidthFull();
        textAreaConsoleOutput.setHeight("98%");
        textAreaConsoleOutput.setLabel("Output");
        textAreaConsoleOutput.setReadOnly(Boolean.TRUE);
        textAreaConsoleOutput.addThemeVariants(TextAreaVariant.LUMO_SMALL);

        textAreaConsoleOutput.getStyle().set("overflow-y", AUTO);
        textAreaConsoleOutput.getElement().setAttribute("title", "output");
//        textAreaConsoleOutput.getStyle().set(BOX_SHADOW_PROPERTY, BOX_SHADOW_VALUE);
        textAreaConsoleOutput.addClassName("child-text-area-console");

        final Div divTextArea = new Div(textAreaConsoleOutput);
        divTextArea.setId("divTextArea");
        divTextArea.setSizeFull();
        divTextArea.getStyle().set(MARGIN_LEFT, MARGIN_10_PX);
        divTextArea.getStyle().set("margin-right", "20px");
        divTextArea.addClassName("text-area-console-div");

        super.add(divTextArea);
        super.setWidthFull();
        super.getStyle().set(DISPLAY, "flex");
        super.getStyle().set(MARGIN_LEFT, MARGIN_10_PX);
        super.addClassName(Bottom.SMALL);
        super.getStyle().set(OVERFLOW_Y, HIDDEN);
        super.addClassName("h3-text-area-div");

    }

    public TextArea getTextArea() {
        return this.textAreaConsoleOutput;
    }

}
