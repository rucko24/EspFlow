package com.esp.espflow.util;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 * @author rubn
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class EspFlowConstants {

    public static final String BIN_SH = "/bin/sh";
    // "bin/bash", "-c" caso particular en dmesg
    public static final String[] BIN_SH_C = new String[]{BIN_SH, "-c"};
    public static final String[] SH_C = new String[]{"sh", "-c"};
    public static final String[] BIN_BASH_C = new String[]{"/bin/bash", "-c"};
    public static final String[] DMESG_TTY = new String[]{BIN_SH, "-c", "dmesg | grep tty"};
    public static final String DMESG_GREP_TTY = "dmesg | grep tty";
    public static final String[] CMD_C = new String[]{"cmd", "/c"};
    public static final String[] OTHER = new String[]{"other"};
    public static final String BOX_SHADOW_PROPERTY = "box-shadow";
    public static final String BOX_SHADOW_VALUE = "0 4px 8px 0 rgba(0, 0, 0, 0.2), 0 6px 20px 0 rgba(0, 0, 0, 0.19)";
    public static final String BOX_SHADOW_VAADIN_BUTTON= "custom-box-shadow-vaadin-button";
    public static final String MARGIN_TOP = "margin-top";
    public static final String MARGIN_LEFT = "margin-left";
    public static final String MARGIN = "margin";
    public static final String MARGIN_10_PX = "10px";
    public static final String AUTO = "auto";
    public static final String DISPLAY = "display";
    public static final String ESPTOOL = "esptool";
    public static final String ESPTOOL_PY_V = "esptool.py v";
    public static final String ESPTOOL_PY_NOT_FOUND = "esptool.py not found!";
    public static final String[] ESPTOOL_PY_VERSION = new String[]{"esptool.py","version"};
    public static final String VERSION = "version";
    public static final int FIRST_LINE_TO_CHECK_IF_IT_EXISTS = 1;
    public static final String NOT_FOUND = "not found!";
    public static final String COMMAND_NOT_FOUND = "command not found! ";
    public static final String CHARSET_CP850 = "CP850";
    public static final String OVERFLOW_X = "overflow-x";
    public static final String OVERFLOW_Y = "overflow-y";
    public static final String HIDDEN = "hidden";

    public static final String SERIAL_PORT = "Serial port";
    public static final String MAC = "MAC";
    public static final String CHIP_TYPE = "Detecting chip type";
    public static final String CHIP_IS = "Chip is";
    public static final String DETECTED_FLASH_SIZE = "Detected flash size";
    public static final String CRYSTAL_IS = "Crystal is";

    public static final String OK = "OK";
    public static final String DIALOG_DISABLED = "Hide";
    public static final String INFORMATION = "Information";
    public static final String WARNING = "Warning";
    public static final String FRONTEND_ICON = "icons/icon.png";
    public static final String FRONTEND_IMAGES_ESPDEVICES = "frontend/images/espdevices/";
    public static final String FRONTEND_IMAGES_LOGO = "frontend/images/logo/";
    public static final String FRONTEND_IMAGES_AVATAR_USER = "frontend/images/avatar-user/";
    public static final String FRONTEND_IMAGES_ABOUT = "frontend/images/about/";
    public static final String FRONTEND_IMAGES_CUSTOM = "frontend/images/custom-images/";
    public static final String FRONTEND_IMAGES_SVG_ICONS = "/META-INF/resources/frontend/images/svg-icons/";
    public static final String META_INF_RESOURCES_ESPTOOL_BUNDLE = "/META-INF/resources/esptool-bundled/";
    public static final String ESPTOOL_BUNDLE_DIR = "/esptool-bundled-dir/";
    public static final String EXECUTABLE_ICON = "executable.svg";
    public static final String SLASH = "/";
    public static final String SET_CHMOD_X = "rwx--x--x";
    public static final String PORT = "--port";
    public static final String ESPTOOL_PY = "esptool.py";
    public static final String BAUD_RATE = "--baud";
    public static final String READ_FLASH = "read_flash";
    public static final String FLASH_ID = "flash_id";
    public static final String FLASH_MODE = "--flash_mode";
    public static final String FLASH_SIZE = "--flash_size";
    public static final String ERASE_FLASH = "--erase_flash ";
    public static final String DEFAULT_INIT_ADDRESS_SIZE_TO_WRITE_0x_00000 = "0x00000";
    public static final String WRITE_FLASH = "write_flash";
    public static final String FLASH_ON_SVG = "flash-on.svg";
    public static final String FLASH_OFF_SVG = "flash-off.svg";
    public static final String SIZE_25_PX = "25px";
    public static final String ROTATE_0_DEGREE = "rotate(0deg)";
    public static final String TRANSFORM = "transform";
    public static final String CURSOR_POINTER = "pointer";

    public static final String THIS_FEATURE_HAS_NOT_BEEN_IMPLEMENTED_YET = "This feature has not been implemented yet!";

    public static final String JAVA_IO_TEMPORAL_DIR_OS = System.getProperty("java.io.tmpdir");
    public static final String JAVA_IO_USER_HOME_DIR_OS = System.getProperty("user.home");
    public static final String ESPFLOW_DIR = "/.espflow/1.0.0/";
    public static final String CUSTOM_ESPTOOL = "esptool/";

    public static final String NO_DEVICES_SHOWN = "No devices shown!";
    public static final String LOADING = "Loading...";
    public static final String CHANGE_SERIAL_PORT_PERMISSIONS = "Change serial port permissions!";
    public static final String PORT_FAILURE = "Port failure: ";

    public static final String WINDOWS_ICON = "windows.svg";
    public static final String LINUX_ICON = "linux.svg";
    public static final String MACOS_ICON = "mac.svg";
    public static final String FREE_BSD_ICON = "freebsd.svg";
    public static final String NO_OS_ICON = "no-os.svg";
    public static final String ESPFLOW_SOURCE_CODE = "https://github.com/rucko24/EspFlow";
    /*
     * for wizard
     */
    public static final String WIZARD_FLASH_ESP_VIEW = "wizardFlashEspView";
    public static final String WIZARD_READ_FLASH_ESP_VIEW = "wizardReadFlashEspView";
    public static final String INNER_HTML = "innerHTML";
    public static final String AVATAR_STEP_ACTIVE = "avatar-step-active";
    public static final String AVATAR_STEP_INACTIVE = "avatar-step-inactive";
    public static final String STEP1 = "step1";
    public static final String STEP2 = "step2";
    public static final String STEP3 = "step3";
    public static final String SETTINGS = "settings";
    public static final String VAR_LUMO_PRIMARY_COLOR_10_PCT = "var(--lumo-primary-color-10pct)";


}
