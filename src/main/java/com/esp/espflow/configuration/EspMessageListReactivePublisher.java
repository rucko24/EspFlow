package com.esp.espflow.configuration;

import com.vaadin.flow.component.messages.MessageListItem;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

/**
 * @author rubn
 */
@Configuration
public class EspMessageListReactivePublisher {

    @Bean
    public Sinks.Many<MessageListItem> publisher() {
        return Sinks
                .many()
                .multicast()
                .onBackpressureBuffer();
    }

    @Bean
    public Flux<MessageListItem> subscribers(Sinks.Many<MessageListItem> publisher) {
        return publisher
                .asFlux();
    }

}
