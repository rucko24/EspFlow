package com.esp.espflow.views.settings;

import com.esp.espflow.entity.dto.EsptoolExecutableDto;
import com.esp.espflow.entity.dto.EsptoolSha256Dto;
import com.esp.espflow.entity.event.EsptoolVersionMessageListItemEvent;
import com.esp.espflow.enums.Breakpoint;
import com.esp.espflow.service.EsptoolService;
import com.esp.espflow.service.hashservice.ComputeSha256Service;
import com.esp.espflow.service.respository.impl.EsptoolExecutableServiceImpl;
import com.esp.espflow.util.ConfirmDialogBuilder;
import com.esp.espflow.util.CreateCustomDirectory;
import com.esp.espflow.util.EspFlowConstants;
import com.esp.espflow.util.MakeExecutable;
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
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.textfield.TextField;
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
import org.vaadin.olli.ClipboardHelper;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import static com.esp.espflow.util.EspFlowConstants.BOX_SHADOW_VAADIN_BUTTON;
import static com.esp.espflow.util.EspFlowConstants.CUSTOM_ESPTOOL;
import static com.esp.espflow.util.EspFlowConstants.ESPFLOW_DIR;
import static com.esp.espflow.util.EspFlowConstants.ESPTOOL;
import static com.esp.espflow.util.EspFlowConstants.ESPTOOL_PY_NOT_FOUND;
import static com.esp.espflow.util.EspFlowConstants.EXECUTABLE_ICON;
import static com.esp.espflow.util.EspFlowConstants.INNER_HTML;
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
public class SettingsEsptoolHomePathContent extends Layout implements CreateCustomDirectory, MakeExecutable {

    private final EsptoolService esptoolService;
    private final ComputeSha256Service computeSha256Service;
    private final EsptoolExecutableServiceImpl esptoolExecutableServiceImpl;

    private final Layout esptoolLayout = new Layout();
    private final Upload upload = new Upload();
    private final FileBuffer buffer = new FileBuffer();
    private final ComboBox<EsptoolExecutableDto> comboBoxEsptoolHome = new ComboBox<>();
    private final Sinks.Many<EsptoolVersionMessageListItemEvent> publishEsptoolVersionEvent;
    private final TextField textFieldHash = new TextField();
    private final ProgressBar progressBar = new ProgressBar();
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
            log.info("Esptool custom path addSucceededListener {}", fileName);
            // FIXME Sacar version del esptool.py aqui mismo antes de guardar en db,
            //  1 comprobar hash de version
            // Solo guardar en db si es valido el hash
            // Para diferenciar de un ejecutable infectado
            // Textfield con hash del executable y que compare con la version online ?
            this.createCustomDirectory(buffer, JAVA_IO_USER_HOME_DIR_OS.concat(ESPFLOW_DIR).concat(CUSTOM_ESPTOOL), event.getFileName());
            this.progressBar.setVisible(true);
            this.computeSha256Service.computeSha256(fileName)
                    .doOnError(onError -> {
                        this.executeErrorCommandFromTextField(onError.getMessage());
                        try {
                            Files.deleteIfExists(Path.of(fileName));
                        } catch (IOException e) {
                            throw new RuntimeException("Error when trying to delete invalid loaded executable");
                        }
                    })
                    .flatMap(this::executeCommandFromTextField)
                    .map(esptoolSha256Dto -> {
                        log.info("sha256 inside map() {}", esptoolSha256Dto.sha256());
                        this.makeExecutable(Path.of(fileName));
                        return EsptoolExecutableDto.builder()
                                .name(ESPTOOL)
                                .absolutePathEsptool(fileName)
                                .isBundled(false)
                                .esptoolVersion("esptool.py ".concat(esptoolSha256Dto.esptoolVersion()))
                                .isSelected(false)
                                .sha256(esptoolSha256Dto.sha256())
                                .build();
                    })
                    .subscribe(esptoolExecutableDto -> {
                        final EsptoolExecutableDto savedEsptoolBundleDto = this.esptoolExecutableServiceImpl.save(esptoolExecutableDto);
                        String esptoolVersion = esptoolExecutableDto.esptoolVersion();
                        if (!esptoolVersion.contains(ESPTOOL_PY_NOT_FOUND)) {
                            this.esptoolExecutableServiceImpl.findByEsptoolVersionWithBundle(esptoolVersion, false)
                                    .ifPresentOrElse(present -> {

                                        this.configureDirectoryForTheNewUploadedExecutable(esptoolVersion, fileName,
                                                event, savedEsptoolBundleDto);

                                    }, () -> {

                                        this.executeCommandFromCombo(() -> {
                                            ConfirmDialogBuilder.showInformation("The version " + esptoolVersion + " exists");
                                        });

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

            //Ejecutar comando si el hash es correcto y coincide con el ejecutable legitimo
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
    private void showSha256ForExecutableBundleOnTextField() {
        this.progressBar.setVisible(true);
        this.esptoolExecutableServiceImpl.findById(1L)
                .ifPresent(esptoolExecutableDto -> {
                    this.progressBar.setVisible(false);
                    this.executeCommandFromTextField(esptoolExecutableDto.sha256());
                });
    }

    /**
     * @return A {@link Layout}
     */
    public Scroller createEsptoolHomePathContent() {
        this.esptoolLayout.removeAll();
        super.removeAll();
        final H2 title = new H2("Esptool home path");
        title.addClassNames(LumoUtility.FontSize.XLARGE, LumoUtility.Margin.Top.MEDIUM);
        title.setId(title.getText().replace(" ", "-").toLowerCase());

        final Paragraph description = new Paragraph();
        description.getElement().setProperty(INNER_HTML, "By default it uses a version of <strong>esptool.py</strong> from the system's tmp directory, we can <strong>upload/drag</strong> one custom to the upload button.");
        description.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);

        final Paragraph description2 = new Paragraph();
        description2.addClassName(LumoUtility.Margin.Top.NONE);
        description2.getElement().setProperty(INNER_HTML, "When the executable esptool is uploaded, <strong>sha256</strong> is computed.");
        description2.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);

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

        final Hr separator = new Hr();
        separator.setWidthFull();
        separator.addClassName(LumoUtility.Margin.Top.LARGE);
        this.progressBar.setIndeterminate(true);
        this.progressBar.setWidthFull();
        this.progressBar.setVisible(false);
        this.textFieldHash.setClearButtonVisible(true);
        this.textFieldHash.setHelperComponent(progressBar);
        this.textFieldHash.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);
        this.textFieldHash.setPrefixComponent(new Span("sha256:"));

        super.add(title, description, description2, esptoolLayout, separator, textFieldHash);
        // Viewport < 1024px
        super.setFlexDirection(Layout.FlexDirection.COLUMN);
        // Viewport > 1024px
        super.setDisplay(Breakpoint.LARGE, Layout.Display.GRID);
        super.setColumns(Layout.GridColumns.COLUMNS_2);
        super.setColumnGap(Layout.Gap.MEDIUM);
        super.setColumnSpan(Layout.ColumnSpan.COLUMN_SPAN_FULL, title, description, description2, esptoolLayout,
                separator, textFieldHash);

        final Scroller scroller = new Scroller(this);
        scroller.setScrollDirection(Scroller.ScrollDirection.VERTICAL);
        scroller.getStyle().set("scrollbar-width", "thin");

        return scroller;
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
                        .isSelected(true)
                        .build();

                this.esptoolExecutableServiceImpl.save(entityToUpdate);
                this.esptoolExecutableServiceImpl.updateAllSelectedToFalseExcept(savedEsptoolBundleDto.id());
                this.comboBoxEsptoolHome.setItems(this.esptoolExecutableServiceImpl.findAll());
                this.comboBoxEsptoolHome.setValue(entityToUpdate);
                int overlayLength = esptoolVersion.concat(fileName).length();
                this.overlay = (overlayLength * 9) + "px";
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
    public void executeCommandFromCombo(final Command command) {
        this.comboBoxEsptoolHome.getUI().ifPresent(ui -> {
            ui.access(command);
        });
    }

    /**
     * @param inputHash
     */
    public void executeCommandFromTextField(final String inputHash) {
        this.textFieldHash.getUI().ifPresent(ui -> {
            ui.access(() -> {
                if (inputHash.contains("sha256 does not match!")) {
                    final String sha256 = inputHash.split("!")[1].trim();
                    this.showSpanSha256Result(sha256, "sha256 does not match!", VaadinIcon.WARNING, LumoUtility.TextColor.ERROR);
                } else {
                    this.showSpanSha256Result(inputHash, "sha256 match!", VaadinIcon.INFO, LumoUtility.TextColor.PRIMARY);
                }
            });
        });
    }

    /**
     * @param esptoolSha256Dto
     */
    public Mono<EsptoolSha256Dto> executeCommandFromTextField(final EsptoolSha256Dto esptoolSha256Dto) {
        final AtomicReference<String> atomicReferenceSha256 = new AtomicReference<>();
        this.textFieldHash.getUI().ifPresent(ui -> {
            ui.access(() -> {
                if (esptoolSha256Dto.sha256().contains("sha256 does not match!")) {
                    final String sha256 = esptoolSha256Dto.sha256().split("!")[1].trim();
                    atomicReferenceSha256.set(sha256);
                    this.showSpanSha256Result(sha256, "sha256 does not match!", VaadinIcon.WARNING, LumoUtility.TextColor.ERROR);
                } else {
                    final String sha256 = esptoolSha256Dto.sha256();
                    atomicReferenceSha256.set(sha256);
                    this.showSpanSha256Result(sha256, "sha256 match!", VaadinIcon.INFO, LumoUtility.TextColor.PRIMARY);
                }
            });
        });
        return Mono.just(esptoolSha256Dto);
    }

    /**
     * @param sha256    the processed hash
     * @param spanText  sha256 does not match! or sha256 match!
     * @param icon      warning or info
     * @param textColor error or PRIMARY
     */
    private void showSpanSha256Result(String sha256, String spanText, VaadinIcon icon, String textColor) {
        var copyButtonFromSvg = SvgFactory.createCopyButtonFromSvg();
        final Span span = new Span(copyButtonFromSvg);
        final ClipboardHelper clipboardHelper = new ClipboardHelper(sha256, span);
        span.addClickListener(event -> {
            Notification.show("Copied sha256: " + sha256, 2500, Notification.Position.MIDDLE);
        });
        Tooltip.forComponent(span).setText("Copy sha256");
        this.progressBar.setVisible(false);
        this.textFieldHash.setSuffixComponent(clipboardHelper);
        this.textFieldHash.setValue(sha256);
        this.textFieldHash.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);

        final Span spanSha256HelperText = new Span();
        spanSha256HelperText.add(icon.create());
        spanSha256HelperText.add(spanText);
        spanSha256HelperText.addClassNames(LumoUtility.FontSize.SMALL, textColor);

        this.textFieldHash.setHelperComponent(spanSha256HelperText);
    }

    /**
     * @param error
     */
    public void executeErrorCommandFromTextField(final String error) {
        this.textFieldHash.getUI().ifPresent(ui -> {
            ui.access(() -> {
                var icon = SvgFactory.createCopyButtonFromSvg();
                final Span span = new Span(icon);
                final ClipboardHelper clipboardHelper = new ClipboardHelper("Error ".concat(error), span);
                span.addClickListener(event -> {
                    Notification.show("Copied error: " + error, 2500, Notification.Position.MIDDLE);
                });
                Tooltip.forComponent(span).setText("Copy error");
                this.progressBar.setVisible(false);
                this.textFieldHash.setSuffixComponent(clipboardHelper);
                this.textFieldHash.setValue(error);
                this.textFieldHash.removeClassName(LumoUtility.TextColor.ERROR);
                this.textFieldHash.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.ERROR);
                final Span spanSha256 = new Span(VaadinIcon.WARNING.create());
                spanSha256.add("unexpected error!");
                spanSha256.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.ERROR);
                this.textFieldHash.setHelperComponent(spanSha256);
                ConfirmDialogBuilder.showWarning("This executable cannot be processed, the hashes do not match.");
            });
        });
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

                    this.executeCommandFromTextField(esptoolExecutableDto.sha256());

                });
    }

    /**
     * Configuration for upload
     */
    public void configureUploadButton() {
        upload.setDropAllowed(true);
        upload.setMaxFiles(1);
        upload.setReceiver(buffer);
        upload.addClassName("esptool-homepath-upload");
        Tooltip.forComponent(upload).setText("Drop executable here!");
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
