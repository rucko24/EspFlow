package com.esp.espflow.data.service;

import com.esp.espflow.data.entity.EspDeviceInfo;
import com.esp.espflow.data.mappers.EspDeviceInfoMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @author rubn
 */
@Service
public class EspDeviceInfoFallBackService {

    /**
     *
     * Execute fallback as long as the <strong>flashSize is null</strong> {@link EspDeviceInfoMapper#mapToEspDeviceInfo(Map, String)}, with new EspDeviceInfo and parsed port.
     *
     * @param parsedPort parsed port
     *
     * @see EspDeviceInfoMapper#mapToEspDeviceInfo(Map, String)
     *
     * @return A {@link Mono} with the {@link EspDeviceInfo} configured with each line of the inputstream
     */
    public Mono<EspDeviceInfo> fallback(String parsedPort) {
        return Mono.just(EspDeviceInfo.builder()
                .port(parsedPort)
                .build());
    }
}
