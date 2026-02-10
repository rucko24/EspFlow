package com.esp.espflow.service;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.react.ReactAdapterComponent;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.shared.Registration;

@NpmPackage(value = "esptool-react", version = "0.1.3")
@JsModule("./esptool-wrapper.tsx")
@Tag("esptool-wrapper")
public class EsptoolJsComponent extends ReactAdapterComponent {

    private int commandCounter = 0;

    public EsptoolJsComponent() {
        super();
        setBaudRate(115200);
        setDebugLogging(false);
    }

    /**
     * Define la velocidad en baudios.
     * Ejemplo: 115200, 921600, etc.
     */
    public void setBaudRate(int baudRate) {
        setState("baudRate", baudRate);
    }

    public void setDebugLogging(boolean enabled) {
        setState("debugLogging", enabled);
    }

    public void connect() {
        sendCommand("connect");
    }

    public void disconnect() {
        sendCommand("disconnect");
    }

    private void sendCommand(String action) {
        commandCounter++;
        setState("command", action);
        setState("commandId", commandCounter);
    }

    public Registration addStatusChangeListener(SerializableConsumer<String> listener) {
        return addStateChangeListener("status", String.class, listener);
    }
}