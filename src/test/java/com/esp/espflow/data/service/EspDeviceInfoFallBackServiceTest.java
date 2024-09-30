package com.esp.espflow.data.service;

import com.esp.espflow.data.entity.EspDeviceInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

/**
 * @author rubn
 */
@ExtendWith(MockitoExtension.class)
class EspDeviceInfoFallBackServiceTest {

    @InjectMocks
    private EspDeviceInfoFallBackService service;

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