package com.esp.espflow.configuration;

import com.esp.espflow.entity.event.EsptoolFRWMessageListItemEvent;
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

    @Bean(value = "publishMessageListItemEvent")
    public Sinks.Many<EsptoolFRWMessageListItemEvent> publisher() {
        return Sinks
                .many()
                .multicast()
                .onBackpressureBuffer(10, false);
    }

    @Bean(value = "subscribersMessageListItemsEvent")
    public Flux<EsptoolFRWMessageListItemEvent> subscribers(Sinks.Many<EsptoolFRWMessageListItemEvent> publisher) {
        return publisher
                .asFlux();
    }

}
