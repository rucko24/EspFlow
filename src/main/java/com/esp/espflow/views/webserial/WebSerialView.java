package com.esp.espflow.views.webserial;

import com.esp.espflow.util.ConfirmDialogBuilder;
import com.esp.espflow.views.MainLayout;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author rubn
 */
@Log4j2
@UIScope
@SpringComponent
@PageTitle("WebSerial")
@Route(value = "web-serial-api", layout = MainLayout.class)
@JsModule("./scripts/copy_to_clipboard.js")
@CssImport("./styles/hexdump-grid/grid-message-when-empty.css")
@RolesAllowed("ADMIN")
@RequiredArgsConstructor
public class WebSerialView extends VerticalLayout {

    private String chromeVersion = "Desconocida";
    private String firefoxVersion = "Desconocida";

    @PostConstruct
    public void setup() {
        final Button requestPortButton = new Button("Solicitar Puerto Serial");

        VaadinRequest request = VaadinService.getCurrentRequest();
        String userAgent = request.getHeader("User-Agent");

        if (userAgent != null) {
            // El patrón busca "Chrome/" seguido de la versión.
            Pattern pattern = Pattern.compile("Chrome/(\\d+\\.\\d+\\.\\d+\\.\\d+)");
            Matcher matcher = pattern.matcher(userAgent);
            if (matcher.find()) {
                chromeVersion = matcher.group(1);
                getUI().ifPresent(ui -> ConfirmDialogBuilder.showWarningUI("User Agent: " + userAgent + "\nChrome Version: " + chromeVersion, ui));
            }

            // Patrón para encontrar "Firefox/" seguido de la versión
            Pattern patternMozilla = Pattern.compile("Firefox/(\\d+\\.\\d+)");
            Matcher matcherMozilla = patternMozilla.matcher(userAgent);
            if (matcherMozilla.find()) {
                firefoxVersion = matcherMozilla.group(1);
                getUI().ifPresent(ui -> ConfirmDialogBuilder.showWarningUI("User Agent: " + userAgent + "\nVersión de Firefox: " + firefoxVersion, ui));
            }
        }

        // Agregar los listeners para connect y disconnect
        getElement().executeJs(
                """
                        const server = this.$server;
                        if(navigator.serial) {
                            navigator.serial.addEventListener('connect', (e) => {
                                console.log('Conectado:', e.target);
                                // O bien, si quieres comunicarlo al servidor:
                                window.$server.onSerialConnect('Conectado: ' + JSON.stringify(e.target));
                            });
                            navigator.serial.addEventListener('disconnect', (e) => {
                                console.log('Desconectado:', e.target);
                                window.$server.onSerialDisconnect('Desconectado: ' + JSON.stringify(e.target));
                            });
                            navigator.serial.getPorts().then((ports) => {
                                console.log('Puertos disponibles al cargar:', ports);
                            });
                        } else {
                            console.error('Serial API no es compatible en este navegador.');
                        }
                        """
        );

        // Llamada al método que solicita puerto serial cuando se hace clic en el botón
        requestPortButton.addClickListener(e -> {
            getElement().executeJs(
                    """
                               const server = this.$server;
                               if(navigator.serial) {
                                   navigator.serial.requestPort()
                                .then((port) => {
                                    const info = port.getInfo();
                                    console.log('Puerto seleccionado:', info);
                                    server.onPortSelected('Puerto seleccionado sin filtros: ' + JSON.stringify(info));
                                })
                                .catch((e) => {
                                    console.error('No se seleccionó puerto:', e);
                                });
                                } else {
                                    console.error('Serial API no es compatible en este navegador.');
                                    server.onError('Serial API no es compatible en este navegador.');
                                }
                            """
            );
        });

        super.add(requestPortButton);

    }

    @ClientCallable
    public void onError(String message) {
        getUI().ifPresent(ui -> ConfirmDialogBuilder.showWarningUI(message, ui));
    }

    // Métodos que serán llamados desde JS (ClientCallable)
    @ClientCallable
    public void onSerialConnect(String message) {
        Notification.show("Evento: " + message);
    }

    @ClientCallable
    public void onSerialDisconnect(String message) {
        Notification.show("Evento: " + message);
    }

    @ClientCallable
    public void onPortSelected(String message) {
        Notification.show("Puerto: " + message);
    }

}
