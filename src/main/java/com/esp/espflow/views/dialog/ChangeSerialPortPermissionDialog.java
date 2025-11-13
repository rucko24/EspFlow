package com.esp.espflow.views.dialog;

import com.esp.espflow.entity.ChangePasswordBinderRecord;
import com.esp.espflow.enums.GetOsName;
import com.esp.espflow.exceptions.CanNotBeReadDeviceException;
import com.esp.espflow.util.ConfirmDialogBuilder;
import com.esp.espflow.util.EspFlowConstants;
import com.esp.espflow.util.ProcessCommandsInternals;
import com.esp.espflow.util.svgfactory.SvgFactory;
import com.esp.espflow.views.flashesp.DivHeaderPorts;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ArrayUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;

import static com.esp.espflow.util.EspFlowConstants.BLACK_TO_WHITE_ICON;
import static com.esp.espflow.util.EspFlowConstants.CHANGE_SERIAL_PORT_PERMISSIONS;

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
public class ChangeSerialPortPermissionDialog extends Dialog {

    private final H3 userName = new H3(System.getProperty("user.name"));
    private final ProgressBar progressBar = new ProgressBar();
    private final Button writePasswordButton = new Button("Write password!", SvgFactory.createIconFromSvg("unlock-black.svg","30px",null));
    private final ComboBox<String> copyComboBox = new ComboBox<>();
    private final PasswordField passwordField = new PasswordField();
    private final VerticalLayout verticalLayout = new VerticalLayout();
    private final ProcessCommandsInternals processCommandsInternals;

    @PostConstruct
    public void init() {
        this.config();
    }

    /**
     * Pasamos los puertos con errores
     *
     * @param ports
     */
    public void setPortErrors(String[] ports) {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setItems(ports);
        this.setComboBoxWithErrorsInPorts(comboBox);
    }

    /**
     * It is invoked in the class {@link DivHeaderPorts#initListeners()}
     */
    public void setComboBoxWithErrorsInPorts(ComboBox<String> comboBoxSerialPort) {
        super.open();

        this.verticalLayout.addComponentAtIndex(1, userName);
        this.verticalLayout.addComponentAtIndex(2, this.fillCopyComboBox(comboBoxSerialPort));
        this.verticalLayout.setAlignSelf(Alignment.CENTER, userName);

    }

    /**
     * A new ComboBox
     *
     * @param comboBoxSerialPort from {@link DivHeaderPorts#initListeners()}
     * @return A {@link ComboBox<String>}
     */
    private ComboBox<String> fillCopyComboBox(ComboBox<String> comboBoxSerialPort) {
        copyComboBox.setClearButtonVisible(true);
        copyComboBox.setPlaceholder("port");
        copyComboBox.setWidthFull();
        copyComboBox.setPrefixComponent(SvgFactory.OsIcon("30px", null));
        copyComboBox.setItems(comboBoxSerialPort.getListDataView().getItems().toList());
        copyComboBox.setRenderer(rendererIconUsbForEachItem());
        return copyComboBox;
    }

    private ComponentRenderer<Div, String> rendererIconUsbForEachItem() {
        final SerializableBiConsumer<Div, String> serializableBiConsumer = (div, itemName) -> {
            div.addClassNames(LumoUtility.Display.FLEX, LumoUtility.AlignItems.CENTER);
            final SvgIcon icon = SvgFactory.createIconFromSvg("usb-port-black.svg", "25px", null);
            icon.addClassName(BLACK_TO_WHITE_ICON);
            final Span span = new Span(itemName);
            span.addClassNames(LumoUtility.Padding.Left.SMALL);
            div.add(icon, span);
        };
        return new ComponentRenderer<>(Div::new, serializableBiConsumer);
    }

    /**
     * Initial configuration for the Dialog
     */
    private void config() {
        super.addClassNames("change-port-permission-dialog");
        super.setHeaderTitle(CHANGE_SERIAL_PORT_PERMISSIONS);
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

        final SvgIcon icon = SvgFactory.OsIcon("50px", null);
        Div divIcon = new Div(icon);
        divIcon.setWidth("50px");
        divIcon.setHeight("50px");
        verticalLayout.setHeight("200px");
        verticalLayout.setWidthFull();
        passwordField.setWidthFull();
        passwordField.setClearButtonVisible(true);
        passwordField.setPlaceholder("input user system password");
        writePasswordButton.setWidthFull();
        writePasswordButton.getIcon().addClassName(BLACK_TO_WHITE_ICON);
        verticalLayout.add(divIcon, passwordField);
        verticalLayout.setAlignSelf(Alignment.CENTER, divIcon);

        writePasswordButton.addClickShortcut(Key.ENTER);
        writePasswordButton.addClassName(EspFlowConstants.BOX_SHADOW_VAADIN_BUTTON);
        writePasswordButton.setTooltipText("Write password! - ENTER");
        writePasswordButton.getElement().getThemeList().add("badge error primary");

        final Binder<ChangePasswordBinderRecord> binder = new Binder<>();
        binder.forField(copyComboBox)
                .withValidator(item -> item != null && !item.isEmpty(), "Invalid port")
                .bind(ChangePasswordBinderRecord::port, (key, value) -> {});
        binder.forField(passwordField)
                .withValidator(item -> item != null && !item.isEmpty(), "Invalid password")
                .bind(ChangePasswordBinderRecord::password, (key, value) -> {});

        writePasswordButton.addClickListener(event -> {

            var changePasswordRecord = new ChangePasswordBinderRecord(copyComboBox.getValue(), passwordField.getValue());

            if(binder.writeBeanIfValid(changePasswordRecord)) {
                this.changeSerialPortPermissionsTo666();
            } else {
                ConfirmDialogBuilder.showWarning("Invalid input, please check!");
            }

        });

        progressBar.setVisible(false);
        super.getFooter().add(progressBar, writePasswordButton);

        return verticalLayout;
    }

    /**
     * Change the number of permit posts to 666.
     */
    private void changeSerialPortPermissionsTo666() {
        final String ports = copyComboBox.getValue();
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
                            if (result == 0) {
                                ConfirmDialogBuilder.showInformation("Permission successfully Changed!");
                                this.updateProgressBar(false);
                            }
                        });
                    });
                });
    }

    /**
     * Return 0 wrapped to <strong>Mono.empty</strong> if permissions were changed successfully, otherwise 1
     *
     * @param port     the port
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
