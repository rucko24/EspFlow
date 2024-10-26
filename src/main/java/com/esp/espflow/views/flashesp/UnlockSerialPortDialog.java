package com.esp.espflow.views.flashesp;

import com.esp.espflow.data.service.ComPortService;
import com.esp.espflow.data.util.GetOsName;
import com.esp.espflow.data.util.ProcessCommandsInternals;
import com.esp.espflow.data.util.svgfactory.SvgFactory;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.util.List;

/**
 * It will allow us to change the permissions to the serial port
 *
 * @author rubn
 */
@Log4j2
@Getter
@UIScope
@SpringComponent
@RequiredArgsConstructor
public class UnlockSerialPortDialog extends Dialog {

    private final PasswordField passwordField = new PasswordField();
    private final VerticalLayout verticalLayout = new VerticalLayout();
    private final ComPortService comPortService;
    private final ProcessCommandsInternals processCommandsInternals;

    @PostConstruct
    public void init() {
        this.config();
    }

    /**
     * It is invoked in the class {@link DivHeaderPorts#initListeners(UI)}
     */
    public void open() {
        super.open();
    }

    /**
     * Initial configuration for the super
     */
    private void config() {
        super.setHeaderTitle("Unlock serial port");
        super.setModal(true);
        super.setCloseOnEsc(true);
        final Button closeButton = new Button(new Icon("lumo", "cross"),
                (event) -> super.close());
        super.getHeader().add(closeButton);
        super.add(createContent());
    }

    private VerticalLayout createContent() {

        final SvgIcon icon = SvgFactory.OsIcon();
        verticalLayout.setHeight("200px");
        verticalLayout.setWidth("200px");
        passwordField.setClearButtonVisible(true);
        passwordField.setPlaceholder("input password");
        verticalLayout.add(icon, passwordField);

        return verticalLayout;
    }

    private void checkOs() {
        List<String> ports = comPortService.getOnlyPortsList();
        if (GetOsName.getOsName() == GetOsName.LINUX) {

            this.execute(ports, passwordField.getValue());
        }
    }

    private void execute(List<String> ports, String password) {
        ports.forEach(port -> {
            try {
                final String echo = "echo " + password + " | sudo -S chmod 666 " + port;
                final String[] commands = ArrayUtils.addAll(GetOsName.shellOsName(), echo);
                final int resultCode = processCommandsInternals.execute(commands).waitFor();
                if (resultCode == 0) {
                    System.out.println("Command executed successfully.");

                } else {
                    System.out.println("Command executed failed.");
                }
            } catch (IOException | InterruptedException e) {
                Thread.currentThread().interrupt();
                log.debug("Error al intentar cambiar los permisos en el puerto {} {}", e, port);
            }
        });

    }

}
