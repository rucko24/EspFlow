package com.esp.espflow.data.util.console;

import com.flowingcode.vaadin.addons.xterm.ITerminalOptions;
import com.flowingcode.vaadin.addons.xterm.PreserveStateAddon;
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

import static com.esp.espflow.data.util.EspFlowConstants.*;

/**
 * @author rubn
 */
@Log4j2
@Getter
public class OutPutConsole extends Div {

    private static final String CONTROL_C = "\u0003";
    private static final String CLEAR_CURRENT_LINE = "\r\u001b[K";
    private final XTerm xtermA = new XTerm();
    private final PreserveStateAddon xterm = new PreserveStateAddon(xtermA);
    private Button buttonClear = new Button(VaadinIcon.TRASH.create());
    private Button buttonDownScroll = new Button(VaadinIcon.ARROW_CIRCLE_DOWN_O.create());
    private Div divLeftMenuItems = new Div(buttonDownScroll, buttonClear);

    public OutPutConsole() {

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

        xterm.getXTerm().setId("id-for-xterm");
        xterm.getXTerm().addClassName("xterm");
//        preserveStateAddon.writeln("esptool terminal!");
//        preserveStateAddon.setPrompt("root@esptool $ ");
//        preserveStateAddon.writePrompt();
        xterm.setPrompt("root@esptool $ ");
        xterm.writePrompt();
        xterm.getXTerm().getElement().setProperty("readonly", Boolean.TRUE);
        xterm.setCursorBlink(true);
        xterm.getXTerm().setPasteWithRightClick(true);
        xterm.getXTerm().setCopySelection(true);

        xterm.setCursorStyle(ITerminalOptions.CursorStyle.UNDERLINE);
        //xterm.setUseSystemClipboard(UseSystemClipboard.READWRITE);

        //xterm.addThemeVariants(TextAreaVariant.LUMO_SMALL);

        //xterm.getStyle().set(OVERFLOW_Y, AUTO);
        xterm.getXTerm().getElement().setAttribute("title", "output");
        xterm.getXTerm().getStyle().set(BOX_SHADOW_PROPERTY, BOX_SHADOW_VALUE);
        //xterm.addClassName("child-text-area-console");
        xterm.focus();
        xterm.getXTerm().fit();

        final Div divTextArea = new Div(xterm.getXTerm());
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

    /**
     * Write the prompt "root@esptool $"
     */
    public void writePrompt() {
        this.xterm.writePrompt();
    }

    /**
     * Writing in xterm on the next line
     *
     * @param line the String line
     */
    public void writeln(String line) {
        this.xterm.writeln(line);
    }

    /**
     * String from firmware reading
     *
     * @param inputLine the String inputLine
     */
    public void readFlash(final String inputLine) {
        if (!(inputLine.contains("%"))) {
            this.writeln(inputLine);
        } else {
            this.clearCurrentLine();
            this.write(inputLine);
        }
    }

    /**
     * Line read from the flash to be written to the microcontroller.
     *
     * @param inputLine the String inputLine
     */
    public void writeFlash(final String inputLine) {
        if (!(inputLine.contains("%"))) {
            this.writeln(inputLine);
        } else {
            this.clearCurrentLine();
            this.write(inputLine);
            if(inputLine.contains("100 %")) {
                this.writeln("");//forced for writing on the following line
            }
        }
    }

    /**
     * Writing in xterm on the same line
     *
     * @param line the String line
     */
    public void write(String line) {
        this.xterm.write(line);
    }

    public void clear() {
        this.xterm.clear();
    }

    /**
     * Writing in xterm on the same line, and moving the carriage return to the beginning of the line
     */
    public void clearCurrentLine() {
        this.xterm.write(CLEAR_CURRENT_LINE);
    }

    public void scrollToBottom() {
        this.xterm.scrollToBottom();
    }

    public void fit() {
        this.xterm.getXTerm().fit();
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        if (attachEvent.isInitialAttach()) {
            this.xterm.getXTerm().fit();
            this.xterm.focus();
        }
    }
}
