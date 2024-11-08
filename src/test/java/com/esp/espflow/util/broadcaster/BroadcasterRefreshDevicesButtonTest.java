package com.esp.espflow.util.broadcaster;

import com.esp.espflow.enums.RefreshDevicesEvent;
import com.vaadin.flow.function.SerializableConsumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class BroadcasterRefreshDevicesButtonTest {

    private BroadcasterRefreshDevicesButton broadcaster = BroadcasterRefreshDevicesButton.INSTANCE;

    private SerializableConsumer<Boolean> listenerMock;

    private RefreshDevicesEvent eventMock;

    @BeforeEach
    void setUp() {

        listenerMock = Mockito.mock(SerializableConsumer.class);
        eventMock = Mockito.mock(RefreshDevicesEvent.class);

    }

    @Test
    void testRegisterListener() {
        // Registrar listener y verificar comportamiento esperado
        var registration = broadcaster.register(listenerMock);
        
        // Ejecutar broadcast para asegurarse de que el listener registrado reciba el evento

        broadcaster.broadcast(eventMock);
        verify(listenerMock, timeout(1000)).accept(any(Boolean.class));

        // Eliminar listener y verificar que no recibe más eventos
        registration.remove();
        broadcaster.broadcast(eventMock);
        verifyNoMoreInteractions(listenerMock);
    }

    @Test
    void testBroadcastExecutesListeners() {
        // Registrar un listener y verificar que recibe el evento
        broadcaster.register(listenerMock);
        
        // Ejecutar broadcast
        broadcaster.broadcast(eventMock);

        // Verificar que el listener recibe el evento
        verify(listenerMock, timeout(1000)).accept(any(Boolean.class));

        // Eliminar para limpieza
        broadcaster.register((remove) -> broadcaster.getListeners().remove(listenerMock));
    }

    @Test
    void testBroadcastExecutesMultipleListeners() {
        // Crear y registrar múltiples listeners
        SerializableConsumer<Boolean> listenerMock1 = mock(SerializableConsumer.class);
        SerializableConsumer<Boolean> listenerMock2 = mock(SerializableConsumer.class);
        
        broadcaster.register(listenerMock1);
        broadcaster.register(listenerMock2);

        // Ejecutar broadcast
        broadcaster.broadcast(eventMock);

        // Verificar que ambos listeners reciban el evento
        verify(listenerMock1, timeout(1000)).accept(any(Boolean.class));
        verify(listenerMock2, timeout(1000)).accept(any(Boolean.class));

        // Limpieza
        broadcaster.register((remove) -> {
            broadcaster.getListeners().remove(listenerMock1);
            broadcaster.getListeners().remove(listenerMock2);
        });
    }
}
