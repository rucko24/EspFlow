package com.esp.espflow.configuration;

import com.vaadin.flow.component.messages.MessageListItem;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

/**
 * <p>We publish events inside a reactive Stream, to show in the notification panel an object type MessageListItem,
 *  with LocalDatetime UTC, serial port, chip type, and the type of operation, <strong>write_flash</strong>
 *  <strong>flash_id</strong>, <strong>read_flash</strong></p>
 *
 * @author rubn
 */
@Configuration
public class EspMessageListItemReactivePublisher {

    @Bean(value = "publishMessageListItem")
    public Sinks.Many<MessageListItem> publisher() {
        return Sinks
                .many()
                .multicast()
                .onBackpressureBuffer();
    }

    @Bean(value = "subscribersMessageListItems")
    public Flux<MessageListItem> subscribers(Sinks.Many<MessageListItem> publisher) {
        return publisher
                .asFlux();
    }

}