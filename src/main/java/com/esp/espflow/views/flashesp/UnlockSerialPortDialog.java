package com.esp.espflow.views.flashesp;

import com.esp.espflow.data.exceptions.CanNotBeReadDeviceException;
import com.esp.espflow.data.util.ConfirmDialogBuilder;
import com.esp.espflow.data.util.EspFlowConstants;
import com.esp.espflow.data.util.GetOsName;
import com.esp.espflow.data.util.ProcessCommandsInternals;
import com.esp.espflow.data.util.svgfactory.SvgFactory;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ArrayUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.time.Duration;

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

    private final ProgressBar progressBar = new ProgressBar();
    private final Button change = new Button("Change permissions", VaadinIcon.UNLOCK.create());
    private final ComboBox<String> copyComboBox = new ComboBox<>();
    private final PasswordField passwordField = new PasswordField();
    private final VerticalLayout verticalLayout = new VerticalLayout();
    private final ProcessCommandsInternals processCommandsInternals;

    @PostConstruct
    public void init() {
        this.config();
    }

    /**
     * It is invoked in the class {@link DivHeaderPorts#initListeners()}
     */
    public void open(ComboBox<String> comboBoxSerialPort) {
        super.open();

        this.verticalLayout.addComponentAtIndex(1, this.fillCopyComboBox(comboBoxSerialPort));
    }

    /**
     *
     * A new ComboBox
     *
     * @param comboBoxSerialPort from {@link DivHeaderPorts#initListeners()}
     * @return A {@link ComboBox<String>}
     */
    private ComboBox<String> fillCopyComboBox(ComboBox<String> comboBoxSerialPort) {
        copyComboBox.setClearButtonVisible(true);
        copyComboBox.setPlaceholder("com port");
        copyComboBox.setWidthFull();
        copyComboBox.setPrefixComponent(SvgFactory.OsIcon("30px", null));
        copyComboBox.setItems(comboBoxSerialPort.getListDataView().getItems().toList());
        return copyComboBox;
    }

    /**
     * Initial configuration for the Dialog
     */
    private void config() {
        super.setHeaderTitle("Unlock Serial port");
        super.setModal(true);
        super.setCloseOnEsc(true);
        final Button closeButton = new Button(new Icon("lumo", "cross"),
                (event) -> super.close());
        super.getHeader().add(closeButton);
        super.add(createContent());
    }

    /**
     * Content Vertical for the dialog
     *
     * @return A {@link VerticalLayout} with components
     */
    private VerticalLayout createContent() {

        final SvgIcon icon = SvgFactory.OsIcon("70px", null);
        verticalLayout.setHeight("200px");
        verticalLayout.setWidth("300px");
        passwordField.setWidthFull();
        passwordField.setClearButtonVisible(true);
        passwordField.setPlaceholder("input password");
        change.setWidthFull();
        verticalLayout.add(icon, passwordField);
        verticalLayout.setAlignSelf(Alignment.CENTER, icon);

        change.addClickShortcut(Key.ENTER);
        change.addClassName(EspFlowConstants.BOX_SHADOW_VAADIN_BUTTON);
        change.setTooltipText("Change permissions");
        change.addThemeVariants(ButtonVariant.LUMO_ERROR);
        change.addClickListener(event -> this.changeSerialPortPermissionsTo666());

        progressBar.setVisible(false);
        super.getFooter().add(progressBar, change);

        return verticalLayout;
    }

    /**
     * Change the number of permit posts to 666.
     */
    private void changeSerialPortPermissionsTo666() {
        String ports = copyComboBox.getValue();
        if (GetOsName.getOsName() == GetOsName.LINUX) {

            Flux.defer(() -> this.execute(ports, passwordField.getValue()))
                    .subscribeOn(Schedulers.boundedElastic())
                    .switchIfEmpty(Mono.error(new CanNotBeReadDeviceException("Serial port permissions change failed!")))
                    .doOnError(error -> {
                        getUI().ifPresent((ui) -> {
                            ui.access(() -> {
                                ConfirmDialogBuilder.showWarning(error.getMessage());
                                this.updateProgressBar(false);
                            });
                        });
                    })
                    .subscribe(result -> {
                        getUI().ifPresent((ui) -> {
                            ui.access(() -> {
                                if(result == 0) {
                                    ConfirmDialogBuilder.showInformation("Permission successfully Changed!");
                                    this.updateProgressBar(false);
                                }
                            });
                        });
                    });
        }
    }

    /**
     *
     * Return 0 if permissions were changed successfully, otherwise 1
     *
     * @param port the port
     * @param password the password
     */
    private Mono<Integer> execute(final String port, String password) {
        try {
            this.updateProgressBar(true);
            final String echo = "echo " + password + " | sudo -S chmod 666 " + port;
            final String[] commands = ArrayUtils.addAll(GetOsName.shellOsName(), echo);
            int resultCode = processCommandsInternals.execute(commands).waitFor();
            if (resultCode == 0) {
                log.info("Command executed successfully.");
                return Mono.just(resultCode);
            }
            log.info("Command executed failed.");
            return Mono.empty();

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            log.debug("Error when trying to change permissions on the port {} {}", e, port);
            return Mono.empty();
        }
    }

    /**
     *
     * Show the progress bar to the user and hide it in case of error when changing permissions
     *
     * @param value for progressBar
     */
    private void updateProgressBar(final boolean value) {
        getUI().ifPresent(ui -> ui.access(() -> {
            this.progressBar.setVisible(value);
            this.progressBar.setIndeterminate(value);
        }));

    }

}
