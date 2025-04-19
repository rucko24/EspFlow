package com.esp.espflow.configuration;

import com.esp.espflow.entity.event.EspflowMessageListItemEvent;
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
public class EspFlowGenericMessageReactivePublisher {

    @Bean(value = "publishEspflowMessageEvent")
    public Sinks.Many<EspflowMessageListItemEvent> publisher() {
        return Sinks
                .many()
                .multicast()
                .onBackpressureBuffer(10, false);
    }

    @Bean(value = "subscribersEspflowMessageEvent")
    public Flux<EspflowMessageListItemEvent> subscribers(Sinks.Many<EspflowMessageListItemEvent> publisher) {
        return publisher
                .asFlux();
    }

}
