package com.esp.espflow.service;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.SetSystemProperty;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class EsptoolPathServiceTest {

    @InjectMocks
    private EsptoolPathService esptoolPathService;

    @Test
    @SetSystemProperty(key = "os.name", value = "window")
    @DisplayName("Checking temporary directory with esptool Windows")
    void esptoolPathWin() {

        assertThat(esptoolPathService.esptoolPath())
                .isEqualTo("/tmp/esptool-bundle-dir/esptool-winx64/esptool.exe");

    }

    @Test
    @SetSystemProperty(key = "os.name", value = "linux")
    @DisplayName("Checking temporary directory with esptool Linux")
    void esptoolPathLinux() {

        assertThat(esptoolPathService.esptoolPath())
                .isEqualTo("/tmp/esptool-bundle-dir/esptool-linux-amd64/esptool");

    }

    @Test
    @SetSystemProperty(key = "os.name", value = "mac os")
    @DisplayName("Checking esptool.py MacOs")
    void esptoolPathMac() {

        assertThat(esptoolPathService.esptoolPath())
                .isEqualTo("esptool.py");

    }

    @Test
    @SetSystemProperty(key = "os.name", value = "freebsd")
    @DisplayName("Checking esptool.py FreeBSD")
    void esptoolPathFreeBSD() {

        assertThat(esptoolPathService.esptoolPath())
                .isEqualTo("esptool.py");

    }

    @Test
    @SetSystemProperty(key = "os.name", value = "WTFOS")
    @DisplayName("OS is not recognized")
    void esptoolPathWtfOs() {

        assertThat(esptoolPathService.esptoolPath())
                .isEqualTo(StringUtils.EMPTY);

    }

}