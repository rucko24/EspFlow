package com.esp.espflow.data.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static com.esp.espflow.data.util.UiToolConstants.BIN_SH_C;
import static com.esp.espflow.data.util.UiToolConstants.CMD_C;
import static com.esp.espflow.data.util.UiToolConstants.SH_C;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author rubn
 */
class GetOsNameTest {

    @Test
    @DisplayName("Check OS")
    void checkOs() {
        GetOsName os = GetOsName.getOsName();

        if (os == GetOsName.WINDOWS) {

            assertThat(os.getName()).contains(GetOsName.WINDOWS.getName());

        } else if (os == GetOsName.LINUX) {

            assertThat(os.getName()).contains(GetOsName.LINUX.getName());

        } else if (os == GetOsName.MAC) {

            assertThat(os.getName()).contains(GetOsName.MAC.getName());

        } else if (os == GetOsName.FREEBSD) {

            assertThat(os.getName()).contains(GetOsName.FREEBSD.getName());

        } else {

            assertThat(os.getName()).contains(GetOsName.OTHER.getName());
        }
    }

    @Test
    @DisplayName("Check shellOsName() type from this OS")
    void checkShell() {
        final String[] shellType = GetOsName.shellOsName();

        if (Arrays.equals(shellType, CMD_C)) { //Windows

            assertThat(shellType).contains(CMD_C);

        } else if(Arrays.equals(shellType, BIN_SH_C)) {//Linux, FreeBSD

            assertThat(shellType).contains(BIN_SH_C);

        } else if(Arrays.equals(shellType, SH_C)) {//MaOS

            assertThat(shellType).contains(SH_C);

        } else { //Other

            assertThat(shellType).contains(GetOsName.OTHER.getName());
        }
    }

}