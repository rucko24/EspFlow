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

    private SerialPort serialPort;

    public void debugSerialPort(final UI ui, final OutPutConsole outPutConsole, final String portParam) {
        Mono.fromCallable(() -> this.debugSerialPortWithJSerialComm(ui, outPutConsole, portParam))
                .subscribeOn(Schedulers.boundedElastic())
                .switchIfEmpty(Mono.error(new CanNotBeReadDeviceException("Error al debuguear puerto serial")))
                .doOnError(onError -> log.info("onError debug Serial: {}", onError.getMessage()))
                .subscribe();
    }

    private Mono<String> debugSerialPortWithJSerialComm(final UI ui, final OutPutConsole outPutConsole, final String portParam) {
        log.info("\n=== Lectura Serial con Eventos ===");
        this.serialPort = SerialPort.getCommPort(portParam); // Primer puerto disponible
        // Configurar el puerto
        serialPort.setBaudRate(BaudRatesEnum.BAUD_RATE_115200.getBaudRate());
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 0, 0);
        if (serialPort.openPort()) {
            log.info("Puerto abierto para lectura con eventos");
            serialPort.addDataListener(new EspFlowSerialPortDataListener(ui, outPutConsole));
            ui.access(outPutConsole::writePrompt);
            return Mono.just("Port Open");
        }
        return Mono.empty();
    }

    public boolean closePort() {
        return serialPort.closePort();
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
