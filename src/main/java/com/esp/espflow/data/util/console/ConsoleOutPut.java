package com.esp.espflow.data.util.console;

import com.flowingcode.vaadin.addons.xterm.ITerminalOptions;
import com.flowingcode.vaadin.addons.xterm.XTerm;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.FlexDirection;
import com.vaadin.flow.theme.lumo.LumoUtility.JustifyContent;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin.Bottom;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin.Left;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin.Right;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin.Top;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import static com.esp.espflow.data.util.EspFlowConstants.AUTO;
import static com.esp.espflow.data.util.EspFlowConstants.BOX_SHADOW_PROPERTY;
import static com.esp.espflow.data.util.EspFlowConstants.BOX_SHADOW_VALUE;
import static com.esp.espflow.data.util.EspFlowConstants.HIDDEN;
import static com.esp.espflow.data.util.EspFlowConstants.OVERFLOW_Y;

/**
 * @author rubn
 */
@Log4j2
@Getter
public class ConsoleOutPut extends Div {

    private static final String CONTROL_C = "\u0003";
    private static final String CLEAR_CURRENT_LINE = "\r\u001b[K";
    private final XTerm xterm = new XTerm();
    private Button buttonClear = new Button(VaadinIcon.TRASH.create());
    private Button buttonDownScroll = new Button(VaadinIcon.ARROW_CIRCLE_DOWN_O.create());
    private Div divLeftMenuItems = new Div(buttonDownScroll, buttonClear);

    public ConsoleOutPut() {

        this.createDivLeftMenuItems();
        this.getConsole();

    }

    private void createDivLeftMenuItems() {
        buttonClear.setTooltipText("Clear All");
        buttonDownScroll.setTooltipText("Scroll to End");
        divLeftMenuItems.addClassNames(Display.FLEX, FlexDirection.COLUMN, AlignItems.END, JustifyContent.END);
        divLeftMenuItems.getStyle().set("padding", "10px");

        buttonClear.addClickListener(event -> this.clear());
        buttonDownScroll.addClickListener(event -> this.scrollToBottom());
    }

    private void getConsole() {
        final H3 h3 = new H3("Console");
        h3.getStyle().set(Top.AUTO, AUTO);

        xterm.setId("id-for-xterm");
        xterm.addClassName("xterm");
//        preserveStateAddon.writeln("esptool terminal!");
//        preserveStateAddon.setPrompt("root@esptool $ ");
//        preserveStateAddon.writePrompt();
        xterm.setPrompt("root@esptool $ ");
        xterm.writePrompt();
        xterm.getElement().setProperty("readonly", Boolean.TRUE);
        xterm.setCursorBlink(true);
        xterm.setPasteWithRightClick(true);
        xterm.setCopySelection(true);

        xterm.setCursorStyle(ITerminalOptions.CursorStyle.UNDERLINE);
        //xterm.setUseSystemClipboard(UseSystemClipboard.READWRITE);

        //xterm.addThemeVariants(TextAreaVariant.LUMO_SMALL);

        //xterm.getStyle().set(OVERFLOW_Y, AUTO);
        xterm.getElement().setAttribute("title", "output");
        xterm.getStyle().set(BOX_SHADOW_PROPERTY, BOX_SHADOW_VALUE);
        //xterm.addClassName("child-text-area-console");
        xterm.focus();
        xterm.fit();

        final Div divTextArea = new Div(xterm);
        divTextArea.setId("divXterm");
        divTextArea.setSizeFull();
        divTextArea.addClassNames(Left.LARGE, Right.LARGE, "text-area-console-div");

        super.add(divTextArea);
        super.setWidthFull();
        //TODO FIXME esto afecta al la consola en la parte de Read Flash
        super.addClassNames(Display.FLEX, Left.SMALL, Bottom.SMALL);
        super.getStyle().set(OVERFLOW_Y, HIDDEN);
        super.addClassName("h3-text-area-div");

    }

    public void writePrompt() {
        this.xterm.writePrompt();
    }

    public void writeln(String line) {
        this.xterm.writeln(line);
    }

    public void readFlash(final String inputLine) {
        if (!(inputLine.contains("%"))) {
            this.writeln(inputLine);
        } else {
            this.clearCurrentLine();
            this.write(inputLine);
        }
    }

    public void write(String line) {
        this.xterm.write(line);
    }

    public void clear() {
        this.xterm.clear();
    }

    public void clearCurrentLine() {
        this.xterm.write(CLEAR_CURRENT_LINE);
    }

    public void scrollToBottom() {
        this.xterm.scrollToBottom();
    }

    public void fit() {
        this.xterm.fit();
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        if (attachEvent.isInitialAttach()) {
            this.xterm.fit();
            this.xterm.focus();
        }
    }
}
