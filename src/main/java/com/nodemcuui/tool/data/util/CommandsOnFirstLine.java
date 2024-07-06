package com.nodemcuui.tool.data.util;

import java.util.Arrays;

/**
 * @author rubn
 */
public class CommandsOnFirstLine {

    public static String onFirstLine(final String line, final String[] commands) {
        final String firstLine = Arrays.toString(commands)
                .replaceAll("\\[+", "")
                .replaceAll("\\]+", "")
                .replaceAll(",", "")
                .concat("\n\n");
        return new StringBuilder(line)
                .insert(0, "Command: ".concat(firstLine))
                .toString();
    }
}
