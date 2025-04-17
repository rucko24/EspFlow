package com.esp.espflow.configuration;

import com.esp.espflow.enums.RefreshDevicesEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

/**
 * @author rubn
 */
@Configuration
public class EspRefreshEventReactivePublisher {

    @Bean(value = "publishRefreshDevicesEvent")
    public Sinks.Many<RefreshDevicesEvent> publisher() {
        return Sinks
                .many()
                .multicast()
                .onBackpressureBuffer(10, false);
    }

    @Bean(value = "subscribersRefreshDevicesEvent")
    public Flux<RefreshDevicesEvent> subscribers(Sinks.Many<RefreshDevicesEvent> publisher) {
        return publisher
                .asFlux();
    }

}
