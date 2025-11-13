package com.esp.espflow.configuration;

import com.esp.espflow.event.EspflowMessageListItemEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

/**
 * <p>With this we can publish more generic messages</p>
 *
 * @author rubn
 */
@Configuration
public class EspFlowGenericMessageReactivePublisherConfiguration {

    @Bean(value = "publishEspflowMessageListItemEvent")
    public Sinks.Many<EspflowMessageListItemEvent> publishEspflowMessageListItemEvent() {
        return Sinks
                .many()
                .multicast()
                .onBackpressureBuffer(10, false);
    }

    @Bean(value = "subscribersEspflowMessageListItemEvent")
    public Flux<EspflowMessageListItemEvent> subscribersEspflowMessageListItemEvent(Sinks.Many<EspflowMessageListItemEvent> publishEspflowMessageListItemEvent) {
        return publishEspflowMessageListItemEvent
                .asFlux();
    }

}
