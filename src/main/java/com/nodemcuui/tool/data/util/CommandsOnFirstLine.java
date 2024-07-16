package com.nodemcuui.tool.data.util;

import com.nodemcuui.tool.data.util.console.ConsoleOutPut;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * @author rubn
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CommandsOnFirstLine {

    public static void putCommansdOnFirstLine(final String[] commands, final ConsoleOutPut consoleOutPut) {

        final String firstLine = Arrays.toString(commands)
                .replaceAll("\\[+", "")
                .replaceAll("]+", "")
                .replace(",", "")
                .concat("\n\n");

        consoleOutPut.write(firstLine);

    }
}
