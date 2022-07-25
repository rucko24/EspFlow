package com.nodemcutools.application.data.util;

/**
 * @author rubn
 */
public class UiToolConstants {

    public static final String BIN_SH = "/bin/sh";
    public static final String[] DMESG_TTY = new String[]{BIN_SH, "-c","dmesg","grep","tty"};
    public static final String DMESG_GREP_TTY = "dmesg | grep tty";

    public static final String BOX_SHADOW_PROPERTY = "box-shadow";

    public static final String BOX_SHADOW_VALUE = "0 4px 8px 0 rgba(0, 0, 0, 0.2), 0 6px 20px 0 rgba(0, 0, 0, 0.19)";

    public static final String MARGIN_TOP = "margin-top";
    public static final String MARGIN_LEFT = "margin-left";
    public static final String MARGIN = "margin";
    public static final String MARGIN_10_PX = "10px";
    public static final String AUTO = "auto";
    public static final String DISPLAY = "display";

}
