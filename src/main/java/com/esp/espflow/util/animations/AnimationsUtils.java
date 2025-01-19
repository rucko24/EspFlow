package com.esp.espflow.util.animations;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.server.Command;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.logging.Logger;

/**
 * Delay from muscle backend :D
 */
public interface AnimationsUtils {

    Logger log = Logger.getLogger(AnimationsUtils.class.getSimpleName());

    /**
     * @param command   to be executed with the logic to be applied in the element before Stream
     * @param component to remove the css class
     * @param delay     before removing the css class
     */
    default void removesClassWithDelay(Command command, Component component, Duration delay) {
        Mono.just(component)
                .subscribeOn(Schedulers.boundedElastic())
                .delaySubscription(delay)
                .doOnNext(onNext -> log.info("onNext " + Thread.currentThread()))
                .subscribe(e -> this.execute(component, command));
    }

    private void execute(final Component component, final Command command) {
        component.getUI().ifPresent(ui -> ui.access(command));
    }

}
