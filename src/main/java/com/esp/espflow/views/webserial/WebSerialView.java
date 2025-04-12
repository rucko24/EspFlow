package com.esp.espflow.views.webserial;

import com.esp.espflow.entity.dto.WebSerialClientDto;
import com.esp.espflow.views.MainLayout;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.ui.LoadMode;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.security.RolesAllowed;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author rubn
 */
@Log4j2
@UIScope
@SpringComponent
@PageTitle("WebSerial")
@Route(value = "web-serial-api", layout = MainLayout.class)
@JsModule("./scripts/copy_to_clipboard.js")
@JavaScript(value = "./scripts/esptool-wrapper.js", loadMode = LoadMode.EAGER)
@RolesAllowed("ADMIN")
public class WebSerialView extends VerticalLayout {

    // Constantes para las llamadas JavaScript
    private static final String JS_CONNECT = "return window.espConnect($0, $1, $2)";
    private static final String JS_HARD_RESET = "window.testHardReset()";
    private static final String JS_FLASH_ID = "return window.espFlashId()";
    private static final String JS_READ_FLASH = "return window.espReadFlash($0, $1)";
    private static final String JS_WRITE_FLASH = "return window.espWriteFlash($0, $1, $2)";
    private static final String JS_CHANGE_BAUDRATE = "return window.espChangeBaudrate($0)";

    // UI Components
    private final NumberField baudRateField = new NumberField("Baud Rate");
    private final ComboBox<String> chipTypeComboBox = new ComboBox<>("Tipo de Chip");
    private final NumberField readAddressField = new NumberField("Dirección (hex)");
    private final NumberField readSizeField = new NumberField("Tamaño (bytes)");
    private final TextArea resultArea = new TextArea("Resultados");

    private byte[] fileContent = null;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public WebSerialView() {
        // Esto se podría hacer una sola vez, en el constructor de la vista:
        getElement().executeJs("window.setEspTerminalServerRef(this.$server);");

        setPadding(true);
        setSpacing(true);

        // Configuración de campos
        H2 title = new H2("ESP Tool Interface");

        baudRateField.setValue(115200d);
        chipTypeComboBox.setItems("ESP8266", "ESP32", "ESP32-S2", "ESP32-C3");
        chipTypeComboBox.setValue("ESP32");

        readAddressField.setValue(0d);
        readSizeField.setValue(1024d);

        // Configuración del área de resultados
        resultArea.setReadOnly(true);
        resultArea.setWidth("100%");
        resultArea.setHeight("200px");

        // Configuración de carga de archivos
        Upload fileUpload = new Upload();
        MemoryBuffer buffer = new MemoryBuffer();
        fileUpload.setReceiver(buffer);
        fileUpload.setAcceptedFileTypes(".bin", ".hex");

        fileUpload.addSucceededListener(event -> {
            try (InputStream is = buffer.getInputStream()) {
                fileContent = is.readAllBytes();
                Notification.show("Archivo cargado: " + event.getFileName() +
                        " (" + fileContent.length + " bytes)");
            } catch (IOException e) {
                Notification.show("Error al cargar el archivo: " + e.getMessage());
                fileContent = null;
            }
        });

        // Botones de operación
        Button connectButton = new Button("Conectar", e -> {

            UI.getCurrent()
                    .getElement()
                    .executeJs(JS_CONNECT, baudRateField.getValue(), true)
                    .then(String.class, result -> {
                        try {

                            WebSerialClientDto resultValue = objectMapper.readValue(result, WebSerialClientDto.class);

                            String message = resultValue.message();
                            String chip = resultValue.chip();
                            String error = resultValue.error();

                            if (resultValue.success()) {


                                log.info("Operación exitosa: {}", message);
                                resultArea.setValue(resultArea.getValue().concat("\n") + "Operación exitosa: " + message);
                            } else {
                                log.error("Error {}", error);
                                resultArea.setValue("Error: " + error);
                            }

//                            JsonObject jsonResult = JsonParser.parseString(result).getAsJsonObject();
//                            if (jsonResult.get("success").getAsBoolean()) {
//                                log.info("Operación exitosa: {}", (jsonResult.has("message")
//                                        ? jsonResult.get("message").getAsString()
//                                        : ""));
//                                resultArea.setValue(resultArea.getValue().concat("\n") +
//                                        "Operación exitosa: " + (jsonResult.has("message")
//                                        ? jsonResult.get("message").getAsString()
//                                        : ""));
//                            } else {
//                                log.error("Error {}", jsonResult.get("error").getAsString());
//                                resultArea.setValue("Error: " + jsonResult.get("error").getAsString());
//                            }
                        } catch (Exception ex) {
                            log.error("Error {}", ex.getMessage());
                            resultArea.setValue("Error al procesar el resultado: " + ex.getMessage());
                        }
                    });

        });

        Button hardReset = new Button("Hard reset", e -> {
            getElement().executeJs(JS_HARD_RESET);
        });

        Button flashIdButton = new Button("Obtener Flash ID", e -> {
            //executeJsFunction(JS_FLASH_ID);
        });

        Button readButton = new Button("Leer Flash", e -> {
            //executeJsFunction(JS_READ_FLASH, readAddressField.getValue().intValue(), readSizeField.getValue().intValue());
        });

        Button writeButton = new Button("Escribir Flash", e -> {
            if (fileContent == null) {
                Notification.show("Por favor, cargue un archivo primero");
                return;
            }

            String hexString = bytesToHex(fileContent);
            executeJsFunction(JS_WRITE_FLASH, 0, hexString, true);
        });

        // Layout para campos de configuración
        HorizontalLayout configLayout = new HorizontalLayout(
                baudRateField, chipTypeComboBox
        );

        // Layout para operaciones básicas
        HorizontalLayout operationsLayout = new HorizontalLayout(
                connectButton, hardReset, flashIdButton
        );

        // Layout para lectura
        HorizontalLayout readLayout = new HorizontalLayout(
                readAddressField, readSizeField, readButton
        );

        // Layout para escritura
        VerticalLayout writeLayout = new VerticalLayout(
                fileUpload, writeButton
        );

        // Agregar todo a la vista principal
        add(
                title,
                new H3("Configuración"),
                configLayout,
                new H3("Operaciones"),
                operationsLayout,
                new H3("Lectura de Flash"),
                readLayout,
                new H3("Escritura de Flash"),
                writeLayout,
                new H3("Resultados"),
                resultArea
        );
    }

    private void executeJsFunction2(String jsCode, String baudRate, String chipType) {
        UI.getCurrent()
                .getElement()
                .executeJs(jsCode, baudRate, chipType)
                .then(String.class, result -> {
                    try {
//                        JsonObject jsonResult = JsonParser.parseString(result).getAsJsonObject();
//                        boolean success = jsonResult.get("success").getAsBoolean();
//
//                        if (success) {
//                            if (jsonResult.has("data")) {
//                                resultArea.setValue("Operación exitosa: " + jsonResult.get("data").toString());
//                            } else if (jsonResult.has("message")) {
//                                resultArea.setValue(jsonResult.get("message").getAsString());
//                            } else {
//                                resultArea.setValue("Operación completada con éxito");
//                            }
//                        } else {
//                            resultArea.setValue("Error: " + jsonResult
//                                    .get("error").getAsString());
//                        }
                    } catch (Exception e) {
                        resultArea.setValue("Error al procesar el resultado: " + result);
                        e.printStackTrace();
                    }
                });
    }

    private void executeJsFunction(String jsCode, Object... params) {
        UI.getCurrent()
                .getElement()
                .executeJs(jsCode, params)
                .then(String.class, result -> {
                    try {
//                        JsonObject jsonResult = JsonParser.parseString(result).getAsJsonObject();
//                        boolean success = jsonResult.get("success").getAsBoolean();
//
//                        if (success) {
//                            if (jsonResult.has("data")) {
//                                resultArea.setValue("Operación exitosa: " + jsonResult.get("data").toString());
//                            } else if (jsonResult.has("message")) {
//                                resultArea.setValue(jsonResult.get("message").getAsString());
//                            } else {
//                                resultArea.setValue("Operación completada con éxito");
//                            }
//                        } else {
//                            resultArea.setValue("Error: " + jsonResult
//                                    .get("error").getAsString());
//                        }
                    } catch (Exception e) {
                        resultArea.setValue("Error al procesar el resultado: " + result);
                        e.printStackTrace();
                    }
                });
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    @ClientCallable
    public void cleanLog() {
        resultArea.setValue("");
    }

    @ClientCallable
    public void writeLogLine(String msg) {
        resultArea.setValue(resultArea.getValue().concat(msg.concat("\n")));
        log.info("writeLogLine() {}", msg);
    }

    @ClientCallable
    public void writeLog(String msg) {
        resultArea.setValue(resultArea.getValue().concat(msg.concat("\n")));
        log.info("writeLog() {}", msg);
    }


}
