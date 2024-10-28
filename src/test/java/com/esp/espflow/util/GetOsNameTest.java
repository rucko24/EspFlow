package com.esp.espflow.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.ClearSystemProperty;

import static com.esp.espflow.util.EspFlowConstants.BIN_BASH_C;
import static com.esp.espflow.util.EspFlowConstants.BIN_SH_C;
import static com.esp.espflow.util.EspFlowConstants.CMD_C;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author rubn
 */
@ClearSystemProperty(key = "os.name")
class GetOsNameTest {

    @Test
    @DisplayName("Check OS, Windows")
    void checkOsWindows() {

        System.setProperty("os.name", "window");

        assertThat(GetOsName.getOsName()).isEqualTo(GetOsName.WINDOWS);

    }

    @Test
    @DisplayName("Check OS, Linux")
    void checkOsLinux() {

        System.setProperty("os.name", "linux");

        assertThat(GetOsName.getOsName()).isEqualTo(GetOsName.LINUX);

    }

    @Test
    @DisplayName("Check OS, mac")
    void checkOsMac() {

        System.setProperty("os.name", "mac os");

        assertThat(GetOsName.getOsName()).isEqualTo(GetOsName.MAC);

    }

    @Test
    @DisplayName("Check OS, freebsd")
    void checkOsFreeBSD() {

        System.setProperty("os.name", "freebsd");

        assertThat(GetOsName.getOsName()).isEqualTo(GetOsName.FREEBSD);

    }

    @Test
    @DisplayName("Check shellOsName(), windows")
    void checkShellWindow() {

        System.setProperty("os.name", "window");

        assertThat(GetOsName.shellOsName()).isEqualTo(CMD_C);

    }

    @Test
    @DisplayName("Check shellOsName(), linux")
    void checkShellLinux() {

        System.setProperty("os.name", "linux");

        assertThat(GetOsName.shellOsName()).isEqualTo(BIN_BASH_C);

    }

    @Test
    @DisplayName("Check shellOsName(), macOs")
    void checkShellMacOs() {

        System.setProperty("os.name", "mac os");

        assertThat(GetOsName.shellOsName()).isEqualTo(BIN_BASH_C);

    }

    @Test
    @DisplayName("Check shellOsName(), FreeBSD")
    void checkShellFreeBSD() {

        System.setProperty("os.name", "freebsd");

        assertThat(GetOsName.shellOsName()).isEqualTo(BIN_SH_C);

    }

    @Test
    @DisplayName("Os not found")
    void osNotFound() {

        System.setProperty("os.name", "agaga");

        assertThat(GetOsName.getOsName()).isEqualTo(GetOsName.OTHER);

    }

    @Test
    @DisplayName("Shell OS not found")
    void shellOsNotFound() {

        System.setProperty("os.name", "no shell");

        assertThat(GetOsName.shellOsName()).isEqualTo(EspFlowConstants.OTHER);

    }

}