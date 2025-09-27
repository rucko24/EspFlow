package com.esp.espflow.service;

import com.esp.espflow.enums.BaudRatesEnum;
import com.esp.espflow.exceptions.CanNotBeReadDeviceException;
import com.esp.espflow.util.console.OutPutConsole;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.vaadin.flow.component.UI;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * @author rubn
 */
@Log4j2
@Service
public class DebugSerialPortService {

    public void debugSerialPort(final UI ui, final OutPutConsole outPutConsole, final String portParam) {
        Mono.fromCallable(() -> this.debugSerialPortWithJSerialComm(ui, outPutConsole, portParam))
                .subscribeOn(Schedulers.boundedElastic())
                .switchIfEmpty(Mono.defer(() -> Mono.error(new CanNotBeReadDeviceException("Error al debuguear puerto serial"))))
                .doOnError(onError -> log.info("onError debug Serial: {}", onError.getMessage()))
                .subscribe(log::info);
    }

    private Mono<String> debugSerialPortWithJSerialComm(final UI ui, final OutPutConsole outPutConsole, final String portParam) {
        log.info("\n=== Lectura Serial con Eventos ===");
        final SerialPort port = SerialPort.getCommPort(portParam); // Primer puerto disponible
        // Configurar el puerto
        port.setBaudRate(BaudRatesEnum.BAUD_RATE_115200.getBaudRate());
        port.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 0, 0);
        if (port.openPort()) {
            log.info("Puerto abierto para lectura con eventos");
            // Agregar listener para datos
            port.addDataListener(new EspFlowSerialPortDataListener(ui, outPutConsole));
            // Mantener el programa corriendo por 15 segundos
            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            port.closePort();
            log.info("\nPuerto cerrado");
            ui.access(outPutConsole::writePrompt);
            return Mono.just("Puerto cerrado");
        }
        return Mono.empty();
    }

    private record EspFlowSerialPortDataListener(UI ui, OutPutConsole outPutConsole) implements SerialPortDataListener {

            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }

            @Override
            public void serialEvent(SerialPortEvent event) {
                if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
                    return;
                }
                synchronized (this) {
                    final SerialPort comPort = event.getSerialPort();
                    byte[] buffer = new byte[comPort.bytesAvailable()];
                    int bytesRead = comPort.readBytes(buffer, buffer.length);
                    if (bytesRead > 0) {
                        String data = new String(buffer, 0, bytesRead);
                        log.info("root@esptool $ : {}", data);
                        ui.access(() -> outPutConsole.writeln(data));
                    }
                }
            }

        }

}
