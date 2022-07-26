package com.nodemcutools.application.data.util;

/**
 * @author rubn
 */
public class UiToolConstants {

    public static final String BIN_SH = "/bin/sh";
    // "bin/bash", "-c" caso particular en dmesg
    public static final String[] BIN_SH_C = new String[]{"/bin/sh", "-c"};
    public static final String[] SH_C = new String[]{"sh", "-c"};
    public static final String[] DMESG_TTY = new String[]{BIN_SH, "-c", "dmesg", "grep", "tty"};
    public static final String DMESG_GREP_TTY = "dmesg | grep tty";
    public static final String[] CMD_C = new String[]{"cmd", "/c"};
    public static final String BOX_SHADOW_PROPERTY = "box-shadow";
    public static final String BOX_SHADOW_VALUE = "0 4px 8px 0 rgba(0, 0, 0, 0.2), 0 6px 20px 0 rgba(0, 0, 0, 0.19)";
    public static final String MARGIN_TOP = "margin-top";
    public static final String MARGIN_LEFT = "margin-left";
    public static final String MARGIN = "margin";
    public static final String MARGIN_10_PX = "10px";
    public static final String AUTO = "auto";
    public static final String DISPLAY = "display";
    public static final String ESPTOOL_PY_NOT_FOUND = "esptool.py not found!";
    public static final String[] ESPTOOL_PY_VERSION = new String[]{"esptool.py","version"};
    public static final String NOT_FOUND = "not found!";
    public static final String COMMAND_NOT_FOUND = "command not found!";
    public static final String CHARSET_CP850 = "CP850";
    public static final String ESPTOOL_NOT_INSTALLED_ON_LINUX = "/bin/sh: 1: esptool.py: not found";
    public static final String ESPTOOL_NOT_INSTALLED_ON_FREE_BSD = "/bin/sh: esptool.py: Command not found.";
    public static final String ESPTOOL_NOT_INSTALLED_ON_WINDOWS = "\"esptool.py\" no se reconoce como un comando interno o externo";

}
