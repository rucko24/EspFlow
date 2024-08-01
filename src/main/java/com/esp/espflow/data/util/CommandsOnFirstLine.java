package com.esp.espflow.data.util;

import com.esp.espflow.data.util.console.OutPutConsole;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

import static com.esp.espflow.data.util.EspFlowConstants.ESPTOOL_PY;

/**
 * @author rubn
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CommandsOnFirstLine {

    /**
     *  Cleaning of characters to be displayed in XTerm
     *
     *
     * @param commands the arrays string[] with commnads
     * @param outPutConsole is the xTerm
     */
    public static void putCommansdOnFirstLine(final String[] commands, final OutPutConsole outPutConsole) {

        final String firstLine = Arrays.toString(esptoolPyOnFirstIndex(commands))
                .replaceAll("\\[+", "")
                .replaceAll("]+", "")
                .replace(",", "")
                .concat("\n\n");

        outPutConsole.write(firstLine);

    }

    /**
     *  <p>Only the bundle's esptool.py is shown in the first index.</p>
     *
     * @param newCommands A copy of initial string of commands
     * @return A {@link String}
     */
    private static String[] esptoolPyOnFirstIndex(String[] newCommands) {
        var updateCommnds = Arrays.copyOf(newCommands, newCommands.length);
        updateCommnds[0] = ESPTOOL_PY;
        return updateCommnds;
    }
}
