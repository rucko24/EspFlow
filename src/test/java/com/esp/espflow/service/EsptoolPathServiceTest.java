package com.esp.espflow.service;

import com.esp.espflow.entity.dto.EsptoolExecutableDto;
import com.esp.espflow.service.provider.EsptoolPathServiceArgumentsProvider;
import com.esp.espflow.service.provider.EsptoolPathServiceBundleVersionArgumentsProvider;
import com.esp.espflow.service.respository.impl.EsptoolExecutableService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * @author rubn
 */
@ExtendWith(MockitoExtension.class)
class EsptoolPathServiceTest {

    @InjectMocks
    private EsptoolPathService esptoolPathService;

    @Mock
    private EsptoolExecutableService esptoolExecutableService;

    @ParameterizedTest(name = "Case name: {0}")
    @ArgumentsSource(EsptoolPathServiceArgumentsProvider.class)
    @DisplayName("Checking the esptool path, and if it is bundle or custom.")
    void testEsptoolPath(String testName, String osName, Optional<EsptoolExecutableDto> inputDto, String expectedPath) {
        System.setProperty("os.name", osName);
        when(esptoolExecutableService.findByIsSelectedToTrue()).thenReturn(inputDto);

        assertThat(esptoolPathService.esptoolPath()).isEqualTo(expectedPath);

        verify(esptoolExecutableService).findByIsSelectedToTrue();
        verifyNoMoreInteractions(esptoolExecutableService);
    }

    @ParameterizedTest(name = "Case name: {0}")
    @ArgumentsSource(EsptoolPathServiceBundleVersionArgumentsProvider.class)
    @DisplayName("Checking the estpool path, bundle, and version.")
    void esptoolCheckBundlePathAndVersion(String testName, String osName, boolean isBundle,
                                          Optional<EsptoolExecutableDto> inputDto,
                                          EsptoolExecutableDto actualDto, String expectedPath) {

        System.setProperty("os.name", osName);
        when(esptoolExecutableService.findByAbsolutePathEsptoolAndIsBundleAndVersion(expectedPath, isBundle, "v4.7.0"))
                .thenReturn(inputDto);

        assertThat(esptoolPathService.esptoolPath(actualDto)).isEqualTo(expectedPath);

        verify(esptoolExecutableService).findByAbsolutePathEsptoolAndIsBundleAndVersion(expectedPath, isBundle, "v4.7.0");
        verifyNoMoreInteractions(esptoolExecutableService);

    }

}