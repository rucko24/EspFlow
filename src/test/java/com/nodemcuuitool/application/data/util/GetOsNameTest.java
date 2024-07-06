package com.nodemcuuitool.application.data.util;


import com.nodemcuui.tool.data.util.GetOsName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GetOsNameTest {

    @Test
    @DisplayName("Check OS")
    void test() {
        GetOsName os = GetOsName.getOsInfo();

        if (os == GetOsName.WINDOWS) {

            assertThat(os.getOsName()).contains(GetOsName.WINDOWS.getOsName());

        } else if (os == GetOsName.LINUX) {

            assertThat(os.getOsName()).contains(GetOsName.LINUX.getOsName());

        } else if (os == GetOsName.MAC) {

            assertThat(os.getOsName()).contains(GetOsName.MAC.getOsName());

        } else if (os == GetOsName.FREEBSD) {

            assertThat(os.getOsName()).contains(GetOsName.FREEBSD.getOsName());

        } else {

            assertThat(os.getOsName()).contains(GetOsName.OTHER.getOsName());
        }
    }

}