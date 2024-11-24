package com.esp.espflow.service;

import com.esp.espflow.entity.EspDeviceInfoRecord;
import com.esp.espflow.exceptions.CanNotBeReadDeviceException;
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("An EspDeviceInfo is returned with the port only, no exception is returned.")
    void fallback() {

        var espDeviceInfo = EspDeviceInfoRecord.builder()
                .port("COM3")
                .build();

        StepVerifier.create(this.service.fallback("COM3"))
                .expectNext(espDeviceInfo)
                .verifyComplete();

    }

    @Test
    @DisplayName("When the service returns an empty Set, because there are no available ports, this is not the case when the ports do not have read permissions.")
    void fallbackEmptyPorts() {

        StepVerifier.create(this.service.fallbackEmptyPorts())
                .expectError(CanNotBeReadDeviceException.class)
                .verify();

        StepVerifier.create(this.service.fallbackEmptyPorts())
                .expectErrorMatches(error -> error.getMessage().contains("Possibly empty ports"))
                .verify();

    }



}