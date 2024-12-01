package com.esp.espflow.views.settings;

import com.esp.espflow.entity.dto.EsptoolExecutableDto;
import com.esp.espflow.entity.event.EsptoolVersionMessageListItemEvent;
import com.esp.espflow.enums.Breakpoint;
import com.esp.espflow.service.EsptoolService;
import com.esp.espflow.service.respository.impl.EsptoolExecutableServiceImpl;
import com.esp.espflow.util.ConfirmDialogBuilder;
import com.esp.espflow.util.CreateCustomDirectory;
import com.esp.espflow.util.EspFlowConstants;
import com.esp.espflow.util.svgfactory.SvgFactory;
import com.esp.espflow.views.Layout;
import com.infraleap.animatecss.Animated;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.upload.SucceededEvent;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.UploadI18N;
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.server.Command;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.vaadin.olli.ClipboardHelper;
import reactor.core.publisher.Sinks;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Objects;

import static com.esp.espflow.util.EspFlowConstants.BOX_SHADOW_VAADIN_BUTTON;
import static com.esp.espflow.util.EspFlowConstants.CUSTOM_ESPTOOL;
import static com.esp.espflow.util.EspFlowConstants.ESPFLOW_DIR;
import static com.esp.espflow.util.EspFlowConstants.ESPTOOL;
import static com.esp.espflow.util.EspFlowConstants.ESPTOOL_PY_NOT_FOUND;
import static com.esp.espflow.util.EspFlowConstants.EXECUTABLE_ICON;
import static com.esp.espflow.util.EspFlowConstants.JAVA_IO_USER_HOME_DIR_OS;

/**
 * - No guardar misma entidad con el mismo path con misma version
 * - Obtener la version del esptool del executable cargado
 *
 * @author rub'n
 */
@Log4j2
@UIScope
@SpringComponent
@RequiredArgsConstructor
public class SettingsEsptoolHomePathContent extends Layout implements CreateCustomDirectory {

    private final EsptoolService esptoolService;
    private final EsptoolExecutableServiceImpl esptoolExecutableServiceImpl;

    private final Layout esptoolLayout = new Layout();
    private final Upload upload = new Upload();
    private final FileBuffer buffer = new FileBuffer();
    private final ComboBox<EsptoolExecutableDto> comboBoxEsptoolHome = new ComboBox<>();
    private final Sinks.Many<EsptoolVersionMessageListItemEvent> publishEsptoolVersionEvent;
    private String overlay;

    @PostConstruct
    public void init() {
        this.esptoolLayout.removeAll();
        //Add the listener only once
        this.initListeners();
    }

    /**
     * Add listeners
     */
    private void initListeners() {
        this.upload.addSucceededListener(event -> {
            this.upload.clearFileList();
            final String fileName = JAVA_IO_USER_HOME_DIR_OS.concat(ESPFLOW_DIR).concat(CUSTOM_ESPTOOL).concat(event.getFileName());
            // FIXME Sacar version del esptool.py aqui mismo antes de guardar en db, comprobar hash de version ?
            // Para diferenciar de un ejecutable malo
            // Textfield con hash del executable y que compare con la version online ?
            this.createCustomDirectory(buffer, JAVA_IO_USER_HOME_DIR_OS.concat(ESPFLOW_DIR).concat(CUSTOM_ESPTOOL), event.getFileName());
            log.info("Esptool custom path addSucceededListener {}", fileName);

            final EsptoolExecutableDto esptoolBundleDto = EsptoolExecutableDto.builder()
                    .name(ESPTOOL)
                    .absolutePathEsptool(fileName)
                    .isBundled(false)
                    .esptoolVersion(StringUtils.EMPTY)
                    .isSelected(false)
                    .build();

            final EsptoolExecutableDto savedEsptoolBundleDto = this.esptoolExecutableServiceImpl.save(esptoolBundleDto);
            this.esptoolService.showEsptoolVersion(fileName, false)
                    .subscribe(esptoolVersion -> {
                        if (!esptoolVersion.contains(ESPTOOL_PY_NOT_FOUND)) {
                            this.esptoolExecutableServiceImpl.findByEsptoolVersionWithBundle(esptoolVersion, false)
                                    .ifPresentOrElse(present -> {
                                        this.executeCommandFromCombo(() -> {
                                            ConfirmDialogBuilder.showInformation("The version " + esptoolVersion + " exists");
                                        });
                                    }, () -> {
                                        this.configureDirectoryForTheNewUploadedExecutable(esptoolVersion, fileName,
                                                event, savedEsptoolBundleDto);
                                    });
                        } else {
                            this.executeCommandFromCombo(() -> {
                                ConfirmDialogBuilder.showWarning(event.getFileName() + ", It is not a valid esptool executable");
                                this.esptoolExecutableServiceImpl.deleteById(savedEsptoolBundleDto.id());
                                this.comboBoxEsptoolHome.setItems(this.esptoolExecutableServiceImpl.findAll());
                                try {
                                    Files.deleteIfExists(Path.of(savedEsptoolBundleDto.absolutePathEsptool()));
                                } catch (IOException e) {
                                    throw new RuntimeException("No se puede borrar el directorio custom del esptool.py");
                                }
                            });
                        }

                    });
        });

        this.comboBoxEsptoolHome.setPrefixComponent(SvgFactory.createIconFromSvg(EXECUTABLE_ICON, "20px", null));
        this.comboBoxEsptoolHome.setItemLabelGenerator(EsptoolExecutableDto::displayAbsoluteEsptoolPathForCombo);
        this.comboBoxEsptoolHome.setRenderer(EsptoolExecutableDto.rendererExecutableIcon());
        //this.comboBoxEsptoolHome.getStyle().set("--vaadin-combo-box-overlay-width", "500px");
        //this.comboBoxEsptoolHome.getStyle().set("--vaadin-input-field-border-width", "1px");
        //this.comboBoxEsptoolHome.getStyle().set("--vaadin-input-field-border-color", VAR_LUMO_PRIMARY_COLOR_10_PCT);
        this.comboBoxEsptoolHome.addValueChangeListener(event -> {
            if (event.isFromClient()) {
                final EsptoolExecutableDto esptoolExecutableDtoItem = event.getValue();
                if (Objects.nonNull(esptoolExecutableDtoItem)) {
                    //Hace falta tirar esta puta query ? o si lo es para poder hacer el setValue correctamente ?
                    final EsptoolExecutableDto updatedEntity = EsptoolExecutableDto.builder()
                            .id(esptoolExecutableDtoItem.id())
                            .name(ESPTOOL)
                            .esptoolVersion(esptoolExecutableDtoItem.esptoolVersion())
                            .absolutePathEsptool(esptoolExecutableDtoItem.absolutePathEsptool())
                            .isBundled(esptoolExecutableDtoItem.isBundled())
                            .isSelected(true)
                            .build();

                    this.esptoolExecutableServiceImpl.save(updatedEntity);
                    this.esptoolExecutableServiceImpl.updateAllSelectedToFalseExcept(esptoolExecutableDtoItem.id());

                    //Ya estamos con el select a true y los demas  false, entonces sacamos la version
                    this.esptoolService.showEsptoolVersion()
                            .subscribe(esptoolVersion -> {
                                this.executeCommandFromCombo(() -> {
                                    this.publishEventAndRefreshHelperText(esptoolExecutableDtoItem);
                                });
                            });
                }
            } else {
                Notification.show("Not from client()");
            }

        });

    }

    /**
     *
     */
    private void configureDirectoryForTheNewUploadedExecutable(final String esptoolVersion, final String fileName,
                                                               final SucceededEvent event,
                                                               final EsptoolExecutableDto savedEsptoolBundleDto) {
        this.executeCommandFromCombo(() -> {
            try {
                final String version = esptoolVersion.split(" ")[1];
                // home/user/.espflow/1.0.0/esptool/v4.x.x/esptool
                final Path newEsptoolVersionDir = Path.of(JAVA_IO_USER_HOME_DIR_OS.concat(ESPFLOW_DIR)
                        .concat(CUSTOM_ESPTOOL) + version + "/" + event.getFileName());
                Files.createDirectories(newEsptoolVersionDir.getParent());
                // target /home/rubn/.espflow/1.0.0/esptool/esptool
                Files.move(Path.of(fileName), newEsptoolVersionDir, StandardCopyOption.REPLACE_EXISTING);

                final EsptoolExecutableDto entityToUpdate = EsptoolExecutableDto.builder()
                        .id(savedEsptoolBundleDto.id())
                        .name(ESPTOOL)
                        .absolutePathEsptool(newEsptoolVersionDir.toAbsolutePath().toString())
                        .isBundled(false)
                        .esptoolVersion(esptoolVersion)
                        .isSelected(false)
                        .build();

                this.esptoolExecutableServiceImpl.save(entityToUpdate);
                this.comboBoxEsptoolHome.setItems(this.esptoolExecutableServiceImpl.findAll());
                this.comboBoxEsptoolHome.setValue(entityToUpdate);
                int overlayLength = esptoolVersion.concat(fileName).length();
                this.overlay = (overlayLength * 9) + "px";
                //TODO recalculate the width more precisely
                this.comboBoxEsptoolHome.getStyle().set("--vaadin-combo-box-overlay-width", overlay);

                this.publishEventAndRefreshHelperText(entityToUpdate);

            } catch (IOException e) {
                throw new RuntimeException("Error al crear o mover el directorio custom del esptool");
            }
        });
    }

    /**
     * @param textEsptoolVersion
     * @param textAbsolutePathEsptool
     * @return A {@link Component}
     */
    private Component createHelperText(final String textEsptoolVersion, final String textAbsolutePathEsptool) {
        final Span estoolVersionSpan = new Span(textEsptoolVersion);
        estoolVersionSpan.addClickListener(event -> Notification.show("Copied " + textEsptoolVersion, 2000, Notification.Position.MIDDLE));
        Tooltip.forComponent(estoolVersionSpan).setText(textEsptoolVersion);
        final ClipboardHelper clipboard1 = new ClipboardHelper(textEsptoolVersion, estoolVersionSpan);
        final Div div1 = new Div(clipboard1);
        div1.getElement().getThemeList().add("badge pill");

        final Span estoolAbsolutePathSpan = new Span(textAbsolutePathEsptool);
        estoolAbsolutePathSpan.setWidth("200px");
        estoolAbsolutePathSpan.getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        estoolAbsolutePathSpan.addClassNames(
                LumoUtility.TextOverflow.ELLIPSIS,
                LumoUtility.Whitespace.NOWRAP,
                LumoUtility.Overflow.HIDDEN);

        estoolAbsolutePathSpan.addClickListener(event -> Notification.show("Copied " + textAbsolutePathEsptool, 2000, Notification.Position.MIDDLE));
        Tooltip.forComponent(estoolAbsolutePathSpan).setText(textAbsolutePathEsptool);

        final ClipboardHelper clipboard2 = new ClipboardHelper(textAbsolutePathEsptool, estoolAbsolutePathSpan);

        clipboard2.getStyle().setWidth("200px");
        clipboard2.addClassNames(LumoUtility.Display.INLINE_BLOCK,
                LumoUtility.TextOverflow.ELLIPSIS,
                LumoUtility.Whitespace.NOWRAP,
                LumoUtility.Overflow.HIDDEN);

        final Div div2 = new Div(clipboard2);
        div2.getElement().getThemeList().add("badge pill");

        final HorizontalLayout row = new HorizontalLayout(div1, div2);
        row.setWidthFull();
        row.getStyle().setCursor(EspFlowConstants.CURSOR_POINTER);
        row.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);
        Animated.animate(row, Animated.Animation.FADE_IN);

        return row;
    }

    /**
     * Simple utility
     *
     * @param command
     */
    public void executeCommandFromCombo(Command command) {
        this.comboBoxEsptoolHome.getUI().ifPresent(ui -> {
            ui.access(command);
        });
    }

    /**
     * @return A {@link Layout}
     */
    public Layout createEsptoolHomePathContent() {
        this.esptoolLayout.removeAll();
        super.removeAll();
        H2 title = new H2("Esptool home path");
        title.addClassNames(LumoUtility.FontSize.XLARGE, LumoUtility.Margin.Top.MEDIUM);
        title.setId(title.getText().replace(" ", "-").toLowerCase());

        Paragraph description = new Paragraph("By default it uses a version of esptool.py from the system's temporary directory, we can establish one by selecting it from here");
        description.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);

        this.comboBoxEsptoolHome.setWidthFull();
        this.comboBoxEsptoolHome.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);
        this.comboBoxEsptoolHome.setItems(this.esptoolExecutableServiceImpl.findAll());
        this.setValueForCombo();

        this.configureUploadButton();

        final Div divCombo = new Div(comboBoxEsptoolHome, upload);
        comboBoxEsptoolHome.addClassName(LumoUtility.Margin.Right.MEDIUM);
        divCombo.addClassNames(LumoUtility.Display.FLEX, LumoUtility.FlexDirection.ROW);
        divCombo.setWidthFull();

        this.esptoolLayout.add(divCombo);
        this.esptoolLayout.addClassNames(LumoUtility.Margin.Bottom.XSMALL, LumoUtility.Margin.Top.MEDIUM);
        this.esptoolLayout.setColumnSpan(Layout.ColumnSpan.COLUMN_SPAN_FULL, divCombo);
        this.esptoolLayout.setAlignItems(Layout.AlignItems.CENTER);
        this.esptoolLayout.setGap(Layout.Gap.SMALL);

        super.add(title, description, esptoolLayout);
        // Viewport < 1024px
        super.setFlexDirection(Layout.FlexDirection.COLUMN);
        // Viewport > 1024px
        super.setDisplay(Breakpoint.LARGE, Layout.Display.GRID);
        super.setColumns(Layout.GridColumns.COLUMNS_2);
        super.setColumnGap(Layout.Gap.MEDIUM);
        super.setColumnSpan(Layout.ColumnSpan.COLUMN_SPAN_FULL, title, description, esptoolLayout);
        return this;
    }

    /**
     * Sets the value of the combo if previously selected to true, initially the default is bundled to true.
     */
    private void setValueForCombo() {
        this.esptoolExecutableServiceImpl.findByIsSelectedToTrue()
                .ifPresent(esptoolExecutableDto -> {
                    this.comboBoxEsptoolHome.setValue(esptoolExecutableDto);
                    this.comboBoxEsptoolHome.setHelperComponent(createHelperText(esptoolExecutableDto.esptoolVersion(),
                            esptoolExecutableDto.absolutePathEsptool()));
                });
    }

    /**
     * Configuration for upload
     */
    public void configureUploadButton() {
        upload.setDropAllowed(false);
        upload.setMaxFiles(10);
        upload.setReceiver(buffer);
        upload.addClassName("esptool-homepath-upload");
        Tooltip.forComponent(upload).setText("Load another esptool from some directory.");
        this.i18N(upload);
    }

    /**
     * @param esptoolExecutableDto
     */
    private void publishEventAndRefreshHelperText(EsptoolExecutableDto esptoolExecutableDto) {
        if (esptoolExecutableDto.isBundled()) {

            this.publishEsptoolVersionEvent.tryEmitNext(new EsptoolVersionMessageListItemEvent(
                    EsptoolVersionMessageListItemEvent.EsptoolVersionEventEnum.BUNDLED
                    , esptoolExecutableDto.esptoolVersion(),
                    esptoolExecutableDto.absolutePathEsptool()));

            log.info("publish bundled esptool event {}", esptoolExecutableDto);

        } else {

            this.publishEsptoolVersionEvent.tryEmitNext(new EsptoolVersionMessageListItemEvent(
                    EsptoolVersionMessageListItemEvent.EsptoolVersionEventEnum.CUSTOM
                    , esptoolExecutableDto.esptoolVersion(),
                    esptoolExecutableDto.absolutePathEsptool()));

            log.info("publish custom esptool event {}", esptoolExecutableDto);

        }

        this.comboBoxEsptoolHome.setHelperComponent(createHelperText(
                esptoolExecutableDto.esptoolVersion(), esptoolExecutableDto.absolutePathEsptool()));

    }

    /**
     * @param comboBoxEsptoolPath
     */
    private void fillComboBox(UI ui, ComboBox<EsptoolExecutableDto> comboBoxEsptoolPath) {
        this.esptoolService.showEsptoolVersion()
                .doOnError(error -> {
                    //ESPTOOL_PY_NOT_FOUND
                    ui.access(() -> {
                        comboBoxEsptoolPath.clear();
                        EsptoolExecutableDto executableDto = EsptoolExecutableDto.builder()
                                .absolutePathEsptool(ESPTOOL_PY_NOT_FOUND)
                                .build();
                        comboBoxEsptoolPath.setItems(executableDto);
                    });
                })
                .doOnTerminate(() -> {
                    ui.access(() -> {
                    });
                })
                .subscribe(espToolVersion -> {
                    ui.access(() -> {
                        comboBoxEsptoolHome.setItems(this.esptoolExecutableServiceImpl.findAll());
                        this.setValueForCombo();
                    });
                });
    }

    /**
     * @param upload to configure
     */
    private void i18N(final Upload upload) {
        final UploadExamplesI18N uploadI18N = new UploadExamplesI18N();
        uploadI18N.getAddFiles().setOne("...");
        uploadI18N.getError().setIncorrectFileType("The provided file doesn't have the correct format. Please provide a [exe,py] file.");
        upload.setI18n(uploadI18N);
        upload.getUploadButton().addClassName(BOX_SHADOW_VAADIN_BUTTON);
        upload.getUploadButton().getElement().getStyle().set("min-width", "40px");
    }

    /**
     * Class for traslation
     */
    private static class UploadExamplesI18N extends UploadI18N {
        public UploadExamplesI18N() {
            setDropFiles(new DropFiles()
                    .setOne("Drop file here")
                    .setMany("Drop files here"));
            setAddFiles(new AddFiles()
                    .setOne("Upload File...")
                    .setMany("Upload Files..."));
            setError(new Error()
                    .setTooManyFiles("Too Many Files.")
                    .setFileIsTooBig("File is Too Big.")
                    .setIncorrectFileType("Incorrect File Type."));
            setUploading(new Uploading()
                    .setStatus(new Uploading.Status()
                            .setConnecting("Connecting...")
                            .setStalled("Stalled")
                            .setProcessing("Processing File...")
                            .setHeld("Queued"))
                    .setRemainingTime(new Uploading.RemainingTime()
                            .setPrefix("remaining time: ")
                            .setUnknown("unknown remaining time"))
                    .setError(new Uploading.Error()
                            .setServerUnavailable("Upload failed, please try again later")
                            .setUnexpectedServerError("Upload failed due to server error")
                            .setForbidden("Upload forbidden")));
            setUnits(new Units()
                    .setSize(Arrays.asList("kB", "MB")));
        }
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        if (attachEvent.isInitialAttach()) {
            final UI ui = attachEvent.getUI();
            this.fillComboBox(ui, comboBoxEsptoolHome);
        }
    }
}
