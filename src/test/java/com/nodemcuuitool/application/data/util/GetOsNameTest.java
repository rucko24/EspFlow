package com.nodemcuuitool.application.data.util;


import com.nodemcuui.tool.data.util.GetOsName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GetOsNameTest {

    @Test
    @DisplayName("Check OS")
    void test() {
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

}