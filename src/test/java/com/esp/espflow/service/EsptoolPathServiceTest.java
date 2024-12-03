package com.esp.espflow.service;

import com.esp.espflow.entity.dto.EsptoolExecutableDto;
import com.esp.espflow.service.respository.impl.EsptoolExecutableServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.SetSystemProperty;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EsptoolPathServiceTest {

    @InjectMocks
    private EsptoolPathService esptoolPathService;

    @Mock
    private EsptoolExecutableServiceImpl esptoolExecutableService;


    @Test
    @SetSystemProperty(key = "os.name", value = "window")
    @DisplayName("Checking temporary directory with esptool Windows")
    void esptoolPathWin() {

        Optional<EsptoolExecutableDto> optionalEsptoolExecutableDto = Optional.of(EsptoolExecutableDto.builder()
                .absolutePathEsptool("/tmp/esptool-bundle-dir/esptool-winx64/esptool.exe")
                .build());

        when(esptoolExecutableService.findByIsSelectedToTrue()).thenReturn(optionalEsptoolExecutableDto);

        assertThat(esptoolPathService.esptoolPath())
                .isEqualTo("/tmp/esptool-bundle-dir/esptool-winx64/esptool.exe");

    }

    @Test
    @SetSystemProperty(key = "os.name", value = "linux")
    @DisplayName("Checking temporary directory with esptool Linux")
    void esptoolPathLinux() {

        Optional<EsptoolExecutableDto> optionalEsptoolExecutableDto = Optional.of(EsptoolExecutableDto.builder()
                .absolutePathEsptool("/tmp/esptool-bundle-dir/esptool-linux-amd64/esptool")
                .build());

        when(esptoolExecutableService.findByIsSelectedToTrue()).thenReturn(optionalEsptoolExecutableDto);

        assertThat(esptoolPathService.esptoolPath())
                .isEqualTo("/tmp/esptool-bundle-dir/esptool-linux-amd64/esptool");

    }

    @Test
    @SetSystemProperty(key = "os.name", value = "mac os")
    @DisplayName("Checking esptool.py MacOs")
    void esptoolPathMac() {

        Optional<EsptoolExecutableDto> optionalEsptoolExecutableDto = Optional.of(EsptoolExecutableDto.builder()
                .absolutePathEsptool("esptool.py")
                .build());

        when(esptoolExecutableService.findByIsSelectedToTrue()).thenReturn(optionalEsptoolExecutableDto);

        assertThat(esptoolPathService.esptoolPath())
                .isEqualTo("esptool.py");

    }

    @Test
    @SetSystemProperty(key = "os.name", value = "freebsd")
    @DisplayName("Checking esptool.py FreeBSD")
    void esptoolPathFreeBSD() {

        Optional<EsptoolExecutableDto> optionalEsptoolExecutableDto = Optional.of(EsptoolExecutableDto.builder()
                .absolutePathEsptool("esptool.py")
                .build());

        when(esptoolExecutableService.findByIsSelectedToTrue()).thenReturn(optionalEsptoolExecutableDto);

        assertThat(esptoolPathService.esptoolPath())
                .isEqualTo("esptool.py");

    }

    @Test
    @SetSystemProperty(key = "os.name", value = "WTFOS")
    @DisplayName("OS is not recognized")
    void esptoolPathWtfOs() {

        Optional<EsptoolExecutableDto> optionalEsptoolExecutableDto = Optional.of(EsptoolExecutableDto.builder()
                .absolutePathEsptool(StringUtils.EMPTY)
                .build());

        when(esptoolExecutableService.findByIsSelectedToTrue()).thenReturn(optionalEsptoolExecutableDto);

        assertThat(esptoolPathService.esptoolPath())
                .isEqualTo(StringUtils.EMPTY);

    }

}