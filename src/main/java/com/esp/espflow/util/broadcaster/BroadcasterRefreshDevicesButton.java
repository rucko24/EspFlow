package com.esp.espflow.util.broadcaster;

import com.esp.espflow.enums.RefreshDevicesEvent;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.shared.Registration;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author rubn
 */
@Log4j2
public class BroadcasterRefreshDevicesButton {

    public static final BroadcasterRefreshDevicesButton INSTANCE = new BroadcasterRefreshDevicesButton();

    private static final List<SerializableConsumer<Boolean>> LISTENERS = new CopyOnWriteArrayList<>();
    
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();

    private BroadcasterRefreshDevicesButton() {}

    public void broadcast(RefreshDevicesEvent event) {
        LISTENERS.forEach(listener -> {
            EXECUTOR_SERVICE.execute(() -> listener.accept(RefreshDevicesEvent.fromEvent(event)));
        });
        log.debug("Notified {} broadcast listeners", LISTENERS.size());
    }
    
    public Registration register(final SerializableConsumer<Boolean> listener) {
        LISTENERS.add(listener);
        return () -> LISTENERS.remove(listener);
    }

    public List<SerializableConsumer<Boolean>> getListeners() {
        return LISTENERS;
    }

}