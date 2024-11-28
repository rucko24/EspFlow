package com.esp.espflow.views.settings;

import com.esp.espflow.entity.dto.EsptoolExecutableDto;
import com.esp.espflow.entity.event.EsptoolVersionEvent;
import com.esp.espflow.enums.Breakpoint;
import com.esp.espflow.service.EsptoolService;
import com.esp.espflow.service.respository.impl.EsptoolExecutableServiceImpl;
import com.esp.espflow.util.ConfirmDialogBuilder;
import com.esp.espflow.util.CreateCustomDirectory;
import com.esp.espflow.util.svgfactory.SvgFactory;
import com.esp.espflow.views.Layout;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.UploadI18N;
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.vaadin.olli.ClipboardHelper;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.Arrays;
import java.util.Objects;

import static com.esp.espflow.util.EspFlowConstants.BOX_SHADOW_VAADIN_BUTTON;
import static com.esp.espflow.util.EspFlowConstants.CUSTOM_ESPTOOL;
import static com.esp.espflow.util.EspFlowConstants.ESPTOOL;
import static com.esp.espflow.util.EspFlowConstants.ESPTOOL_PY_NOT_FOUND;
import static com.esp.espflow.util.EspFlowConstants.EXECUTABLE_ICON_SVG;
import static com.esp.espflow.util.EspFlowConstants.JAVA_IO_USER_HOME_DIR_OS;

/**
 * - No guardar misma entidad con el mismo path, casi, falta añadir otro parametro
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
    private final Button copyButton = new Button();
    private final SvgIcon copySvgButton = SvgFactory.createCopyButtonFromSvg();
    private final ClipboardHelper clipboardHelper = new ClipboardHelper();
    private final Sinks.Many<EsptoolVersionEvent> publishEsptoolVersionEvent;

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
            final String fileName = JAVA_IO_USER_HOME_DIR_OS.concat(CUSTOM_ESPTOOL).concat(event.getFileName());
            this.createCustomDirectory(buffer, JAVA_IO_USER_HOME_DIR_OS.concat(CUSTOM_ESPTOOL), event.getFileName());
            log.info("Esptool custom path addSucceededListener {}", fileName);

            //TODO obtener la version del estool.py subida
            this.esptoolService.showEsptoolVersion()
                    .subscribe(esptoolVersion -> {
                        this.esptoolExecutableServiceImpl.findByAbsolutePathEsptoolAndEsptoolVersion(fileName, esptoolVersion)
                                .ifPresentOrElse(entityPresent -> {
                                    this.comboBoxEsptoolHome.getUI().ifPresent(ui -> {
                                        ui.access(() -> {
                                            ConfirmDialogBuilder.showInformation("This version already exists " + fileName);
                                        });
                                    });
                                }, () -> {
                                    final EsptoolExecutableDto esptoolBundleDto = EsptoolExecutableDto.builder()
                                            .id(null)
                                            .name(ESPTOOL)
                                            .absolutePathEsptool(fileName)
                                            .isBundle(false)
                                            .esptoolVersion(esptoolVersion)
                                            .isSelected(false)
                                            .build();
                                    this.esptoolExecutableServiceImpl.save(esptoolBundleDto);
                                    this.comboBoxEsptoolHome.getUI().ifPresent(ui -> {
                                        ui.access(() -> {
                                            this.comboBoxEsptoolHome.setItems(this.esptoolExecutableServiceImpl.findAll());
                                        });
                                    });
                                });
                    });


            //this.clipboardHelper.setContent(comboBoxEsptoolHome.getValue());
            //this.copyButton.setTooltipText(comboBoxEsptoolHome.getValue());
        });

        this.clipboardHelper.wrap(this.copyButton);
        this.copyButton.setIcon(this.copySvgButton);
        this.copyButton.addClickListener(event -> {
            this.upload.clearFileList();
            Notification.show("Copied " + this.comboBoxEsptoolHome.getValue(), 2500, Notification.Position.MIDDLE);
            this.copyButton.setIcon(VaadinIcon.CHECK.create());
            Mono.just(this.copyButton)
                    .delayElement(Duration.ofMillis(1500))
                    .subscribe(subscribeButton -> {
                        subscribeButton.getUI().ifPresent(ui -> ui.access(() -> {
                            subscribeButton.setIcon(this.copySvgButton);
                            //this.clipboardHelper.setContent(this.comboBoxEsptoolHome.getValue());
                        }));
                    });
        });
        this.comboBoxEsptoolHome.setPrefixComponent(SvgFactory.createIconFromSvg(EXECUTABLE_ICON_SVG, "20px", null));
        this.comboBoxEsptoolHome.setItemLabelGenerator(EsptoolExecutableDto::displayAbsoluteEsptoolPathForCombo);
        this.comboBoxEsptoolHome.setRenderer(EsptoolExecutableDto.rendererExecutableIcon());
        this.comboBoxEsptoolHome.getStyle().set("--vaadin-combo-box-overlay-width", "350px");
        this.comboBoxEsptoolHome.addValueChangeListener(esptoolDtoItem -> {
            if (Objects.nonNull(esptoolDtoItem.getValue())) {
                final EsptoolExecutableDto item = esptoolDtoItem.getValue();
                this.esptoolExecutableServiceImpl.findById(item.id())
                        .ifPresent(entityPresent -> {
                            final EsptoolExecutableDto updatedEntity = EsptoolExecutableDto.builder()
                                    .id(entityPresent.id())
                                    .name(ESPTOOL)
                                    .esptoolVersion(item.esptoolVersion())
                                    .absolutePathEsptool(item.absolutePathEsptool())
                                    .isBundle(item.isBundle())
                                    .isSelected(true)
                                    .build();

                            this.esptoolExecutableServiceImpl.save(updatedEntity);
                            this.esptoolExecutableServiceImpl.updateAllSelectedToFalseExcept(updatedEntity.id());

                            //Ya estamos con el select a true y los demas  false, entonces sacamos la version
                            // y volvemos a actualizar
                            this.esptoolService.showEsptoolVersion()
                                    .subscribe(esptoolVersion -> {
                                        final EsptoolExecutableDto updatedEntityAgain = EsptoolExecutableDto.builder()
                                                .id(entityPresent.id())
                                                .name(ESPTOOL)
                                                .esptoolVersion(esptoolVersion)
                                                .absolutePathEsptool(item.absolutePathEsptool())
                                                .isBundle(item.isBundle())
                                                .isSelected(true)
                                                .build();
                                        this.esptoolExecutableServiceImpl.save(updatedEntity);
                                        this.publishEsptoolVersionEvent.tryEmitNext(new EsptoolVersionEvent(updatedEntityAgain.esptoolVersion()));
                                        this.comboBoxEsptoolHome.getUI().ifPresent(ui -> {
                                            ui.access(() -> {
                                                this.comboBoxEsptoolHome.setHelperText(updatedEntityAgain.esptoolVersion());
                                            });
                                        });
                                    });
                        });
            }
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

        this.configureComboBox();
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
     *
     */
    private void configureComboBox() {
        this.comboBoxEsptoolHome.setWidthFull();
        this.comboBoxEsptoolHome.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);
        this.copyButton.addClassName(BOX_SHADOW_VAADIN_BUTTON);
    }

    /**
     * configuration for upload
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
     *
     * @param comboBoxEsptoolPath
     */
    private void setEsptoolPyVersion(UI ui, ComboBox<EsptoolExecutableDto> comboBoxEsptoolPath) {
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
                        publishEsptoolVersionEvent.tryEmitNext(new EsptoolVersionEvent(espToolVersion));
                        this.comboBoxEsptoolHome.setItems(this.esptoolExecutableServiceImpl.findAll());
                        this.esptoolExecutableServiceImpl.findByIsSelectedToTrue()
                                .ifPresent(dtoIfPresent -> {
                                    comboBoxEsptoolPath.setValue(dtoIfPresent);
                                    log.info("Dto if Present {} ", dtoIfPresent);
                                });
                    });
                });
    }

    /**
     * @param upload
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
            this.setEsptoolPyVersion(ui, comboBoxEsptoolHome);
        }
    }
}
