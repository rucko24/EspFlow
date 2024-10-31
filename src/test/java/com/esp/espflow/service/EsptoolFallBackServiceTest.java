package com.esp.espflow.service;

import com.esp.espflow.entity.EspDeviceInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

/**
 * @author rubn
 */
@ExtendWith(MockitoExtension.class)
class EsptoolFallBackServiceTest {

    @InjectMocks
    private EsptoolFallBackService service;

    @Test
    void fallback() {

        var espDeviceInfo = EspDeviceInfo.builder()
                .port("COM3")
                .build();

        StepVerifier.create(this.service.fallback("COM3"))
                .expectNext(espDeviceInfo)
                .verifyComplete();

    }
}