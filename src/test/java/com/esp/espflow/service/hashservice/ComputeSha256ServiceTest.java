package com.esp.espflow.service.hashservice;

import com.esp.espflow.entity.dto.EsptoolSha256Dto;
import com.esp.espflow.service.respository.impl.EsptoolSha256ServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import java.util.Optional;

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
    private EsptoolSha256ServiceImpl esptoolSha256Service;

    @Test
    @DisplayName("Input file is empty")
    void computeSha256FailureEmpty() {

        StepVerifier.create(computeSha256Service.computeSha256(""))
                .expectErrorMatches(error -> error.getMessage().contains("Can not compute sha256"))
                .verify();
    }

    @Test
    @DisplayName("Input file is null")
    void computeSha256FailureNull() {

        StepVerifier.create(computeSha256Service.computeSha256(null))
                .expectErrorMatches(error -> error.getMessage().contains("Can not compute sha256"))
                .verify();
    }

    @Test
    @DisplayName("Input esptool file with sha256")
    void computeSha256Success() {
        String property = System.getProperty("os.arch");
        System.setProperty("os.arch", "amd64");

        EsptoolSha256Dto actualEsptoolSha256Dto = EsptoolSha256Dto.builder()
                .osArch("amd64")
                .esptoolVersion("v4.7.0")
                .sha256("ae1a3fe6eed5bf7e5dbaee78aea868c5e62f80dd43e13a2f69016da86387a194")
                .build();

        when(esptoolSha256Service.findBySha256("ae1a3fe6eed5bf7e5dbaee78aea868c5e62f80dd43e13a2f69016da86387a194")).thenReturn(Optional.of(actualEsptoolSha256Dto));

        EsptoolSha256Dto expectedEsptoolSha256Dto = EsptoolSha256Dto.builder()
                .osArch("amd64")
                .esptoolVersion("v4.7.0")
                .sha256("ae1a3fe6eed5bf7e5dbaee78aea868c5e62f80dd43e13a2f69016da86387a194")
                .build();

        StepVerifier.create(computeSha256Service.computeSha256("src/test/resources/esptool/esptool-linux-amd64/esptool"))
                .expectNext(expectedEsptoolSha256Dto)
                .verifyComplete();

        System.clearProperty("os.arch");
        System.setProperty("os.arch", property);

    }

}