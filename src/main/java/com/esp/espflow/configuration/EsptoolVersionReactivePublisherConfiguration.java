package com.esp.espflow.configuration;

import com.esp.espflow.event.EsptoolVersionMessageListItemEvent;
import com.esp.espflow.views.flashesp.DivHeaderPorts;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

/**
 * <p>We publish an EsptoolVersionEvent type event to update the H2 in the view {@link DivHeaderPorts#getH2EsptoolVersion()}  </p>
 *
 * @author rub'n
 */
@Configuration
public class EsptoolVersionReactivePublisherConfiguration {

    @Bean(value = "publishEsptoolVersionEvent")
    public Sinks.Many<EsptoolVersionMessageListItemEvent> publisher() {
        return Sinks
                .many()
                .multicast()
                .onBackpressureBuffer(10, false);
    }

    @Bean(value = "subscribersEsptoolVersionEvent")
    public Flux<EsptoolVersionMessageListItemEvent> subscribers(Sinks.Many<EsptoolVersionMessageListItemEvent> publisher) {
        return publisher
                .asFlux();
    }

}
