package com.esp.espflow.service;

import com.esp.espflow.entity.EspDeviceInfoRecord;
import com.esp.espflow.exceptions.CanNotBeReadDeviceException;
import com.esp.espflow.mappers.EspDeviceInfoMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @author rubn
 */
@Service
public class EsptoolFallBackService {

    /**
     *
     * Execute fallback as long as the <strong>flashSize is null</strong> {@link EspDeviceInfoMapper#mapToEspDeviceInfo(Map, String)}, with new EspDeviceInfo and parsed port.
     *
     * @param parsedPort parsed port
     *
     * @see EspDeviceInfoMapper#mapToEspDeviceInfo(Map, String)
     *
     * @return A {@link Mono} with the {@link EspDeviceInfoRecord} configured with each line of the inputstream
     */
    public Mono<EspDeviceInfoRecord> fallback(String parsedPort) {
        return Mono.just(EspDeviceInfoRecord.builder()
                .port(parsedPort)
                .build());
    }

    /**
     * This allows us to raise the exception type {@link CanNotBeReadDeviceException}, when the port list is empty.
     *
     * @return A {@link Mono}
     */
    public Mono<String> fallbackEmptyPorts() {
        return Mono.error(new CanNotBeReadDeviceException("Possibly empty ports"));
    }
}
