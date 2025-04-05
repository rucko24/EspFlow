package com.esp.espflow.service.hashservice;

import com.esp.espflow.configuration.ComputeDigestAlgorithmConfiguration;
import com.esp.espflow.entity.dto.EsptoolSha256Dto;
import com.esp.espflow.service.respository.impl.EsptoolSha256Service;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import java.util.Optional;

import static com.esp.espflow.util.EspFlowConstants.CAN_NOT_COMPUTE_SHA_256;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * - https://stackoverflow.com/a/77075867/7267818
 *
 * @author rub'n
 */
@ExtendWith(MockitoExtension.class)
class ComputeSha256ServiceTest {

    @InjectMocks
    private ComputeSha256Service computeSha256Service;

    @Mock
    private EsptoolSha256Service esptoolSha256Service;

    @Mock
    private ComputeDigestAlgorithmConfiguration computeDigestAlgorithmConfiguration;

    private static final String COMPUTED_SHA_256 = "ae1a3fe6eed5bf7e5dbaee78aea868c5e62f80dd43e13a2f69016da86387a194";

    @Test
    @DisplayName("Input file is empty")
    void computeSha256FailureEmpty() {

        StepVerifier.create(computeSha256Service.computeSha256(""))
                .expectErrorMatches(error -> error.getMessage().contains(CAN_NOT_COMPUTE_SHA_256))
                .verify();

        verifyNoInteractions(esptoolSha256Service);
    }

    @Test
    @DisplayName("Input file is null")
    void computeSha256FailureNull() {

        StepVerifier.create(computeSha256Service.computeSha256(null))
                .expectErrorMatches(error -> error.getMessage().contains(CAN_NOT_COMPUTE_SHA_256))
                .verify();

        verifyNoInteractions(esptoolSha256Service);

    }

    @Test
    @DisplayName("Input esptool file with SHA-256")
    void computeSha256Success() {
        String property = System.getProperty("os.arch");
        System.setProperty("os.arch", "amd64");

        EsptoolSha256Dto actualEsptoolSha256Dto = EsptoolSha256Dto.builder()
                .osArch("amd64")
                .esptoolVersion("v4.7.0")
                .sha256(COMPUTED_SHA_256)
                .build();

        when(computeDigestAlgorithmConfiguration.getDigestAlgorithm()).thenReturn("SHA-256");
        when(esptoolSha256Service.findBySha256(COMPUTED_SHA_256)).thenReturn(Optional.of(actualEsptoolSha256Dto));

        EsptoolSha256Dto expectedEsptoolSha256Dto = EsptoolSha256Dto.builder()
                .osArch("amd64")
                .esptoolVersion("v4.7.0")
                .sha256(COMPUTED_SHA_256)
                .build();

        StepVerifier.create(computeSha256Service.computeSha256("src/test/resources/esptool/esptool-linux-amd64/esptool"))
                .expectNext(expectedEsptoolSha256Dto)
                .verifyComplete();

        verify(esptoolSha256Service).findBySha256(COMPUTED_SHA_256);

        System.clearProperty("os.arch");
        System.setProperty("os.arch", property);

    }

    @Test
    @DisplayName("Input esptool file with SHA-256 but arch does not match!")
    void computeSha256Failure_emptyMono() {
        String property = System.getProperty("os.arch");
        System.setProperty("os.arch", "amd64");

        EsptoolSha256Dto actualEsptoolSha256Dto = EsptoolSha256Dto.builder()
                .osArch("amd63")
                .esptoolVersion("v4.7.0")
                .sha256(COMPUTED_SHA_256)
                .build();

        when(computeDigestAlgorithmConfiguration.getDigestAlgorithm()).thenReturn("SHA-256");
        when(esptoolSha256Service.findBySha256(COMPUTED_SHA_256)).thenReturn(Optional.of(actualEsptoolSha256Dto));

        StepVerifier.create(computeSha256Service.computeSha256("src/test/resources/esptool/esptool-linux-amd64/esptool"))
                .expectErrorMatches(error -> error.getMessage().contains(CAN_NOT_COMPUTE_SHA_256))
                .verify();

        verify(esptoolSha256Service).findBySha256(COMPUTED_SHA_256);

        System.clearProperty("os.arch");
        System.setProperty("os.arch", property);

    }

    @Test
    @DisplayName("Input esptool, java.security.NoSuchAlgorithmException: shaWtF MessageDigest not available")
    void computeSha256Failure_NoSuchAlgorithmException() {

        when(computeDigestAlgorithmConfiguration.getDigestAlgorithm()).thenReturn("shaWtF");

        StepVerifier.create(computeSha256Service.computeSha256("src/test/resources/esptool/esptool-linux-amd64/esptool"))
                .expectErrorMatches(error -> error.getMessage().contains(CAN_NOT_COMPUTE_SHA_256))
                .verify();

        verifyNoInteractions(esptoolSha256Service);

    }

}