package com.nodemcuui.tool.data.util.console;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextAreaVariant;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin.Bottom;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin.Left;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin.Right;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin.Top;
import lombok.extern.log4j.Log4j2;

import static com.nodemcuui.tool.data.util.UiToolConstants.AUTO;
import static com.nodemcuui.tool.data.util.UiToolConstants.HIDDEN;
import static com.nodemcuui.tool.data.util.UiToolConstants.OVERFLOW_Y;

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
        h3.getStyle().set(Top.AUTO, AUTO);
        final Div divH3 = new Div(h3);

        textAreaConsoleOutput.setWidthFull();
        textAreaConsoleOutput.setHeight("98%");
        textAreaConsoleOutput.setLabel("Output");
        textAreaConsoleOutput.setReadOnly(Boolean.TRUE);
        textAreaConsoleOutput.addThemeVariants(TextAreaVariant.LUMO_SMALL);

        textAreaConsoleOutput.getStyle().set(OVERFLOW_Y, AUTO);
        textAreaConsoleOutput.getElement().setAttribute("title", "output");
//        textAreaConsoleOutput.getStyle().set(BOX_SHADOW_PROPERTY, BOX_SHADOW_VALUE);
        textAreaConsoleOutput.addClassName("child-text-area-console");

        final Div divTextArea = new Div(textAreaConsoleOutput);
        divTextArea.setId("divTextArea");
        divTextArea.setSizeFull();
        divTextArea.addClassNames(Left.LARGE, Right.LARGE, "text-area-console-div");

        super.add(divTextArea);
        super.setWidthFull();
        //TODO FIXME esto afecta al la consola en la parte de Read Flash
        super.addClassNames(Display.FLEX, Left.SMALL, Bottom.SMALL);
        super.getStyle().set(OVERFLOW_Y, HIDDEN);
        super.addClassName("h3-text-area-div");

    }

    public TextArea getTextArea() {
        return this.textAreaConsoleOutput;
    }

}
