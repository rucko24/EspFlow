package com.esp.espflow.views.settings;

import com.esp.espflow.dto.EsptoolExecutableDto;
import com.esp.espflow.dto.EsptoolSha256Dto;
import com.esp.espflow.enums.Breakpoint;
import com.esp.espflow.event.EsptoolVersionMessageListItemEvent;
import com.esp.espflow.mappers.EsptoolExecutableMapper;
import com.esp.espflow.mappers.EsptoolSha256Mapper;
import com.esp.espflow.service.EsptoolService;
import com.esp.espflow.service.hashservice.ComputeSha256Service;
import com.esp.espflow.service.respository.impl.EsptoolExecutableService;
import com.esp.espflow.util.ConfirmDialogBuilder;
import com.esp.espflow.util.EspFlowConstants;
import com.esp.espflow.util.FlashUploadHandler;
import com.esp.espflow.util.IMakeExecutable;
import com.esp.espflow.util.svgfactory.SvgFactory;
import com.esp.espflow.views.Layout;
import com.infraleap.animatecss.Animated;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.JsModule;
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
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.UploadI18N;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.server.Command;
import com.vaadin.flow.server.streams.TransferContext;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.esp.espflow.util.EspFlowConstants.BLACK_TO_WHITE_ICON;
import static com.esp.espflow.util.EspFlowConstants.BOX_SHADOW_VAADIN_BUTTON;
import static com.esp.espflow.util.EspFlowConstants.COPY_TO_CLIPBOARD;
import static com.esp.espflow.util.EspFlowConstants.CUSTOM_ESPTOOL;
import static com.esp.espflow.util.EspFlowConstants.ESPFLOW_DIR;
import static com.esp.espflow.util.EspFlowConstants.ESPTOOL_PY_NOT_FOUND;
import static com.esp.espflow.util.EspFlowConstants.EXECUTABLE_ICON;
import static com.esp.espflow.util.EspFlowConstants.INNER_HTML;
import static com.esp.espflow.util.EspFlowConstants.JAVA_IO_USER_HOME_DIR_OS;
import static com.esp.espflow.util.EspFlowConstants.WINDOW_COPY_TO_CLIPBOARD;

/**
 * @author rub'n
 */
@Log4j2
@UIScope
@SpringComponent
@RequiredArgsConstructor
@JsModule(COPY_TO_CLIPBOARD)
public class SettingsEsptoolHomePathContent extends Layout implements IMakeExecutable {

    private final Layout esptoolLayout = new Layout();
    private final Upload upload = new Upload();
    private final ComboBox<EsptoolExecutableDto> comboBoxEsptoolHome = new ComboBox<>();
    private final TextField textFieldHash = new TextField();
    private final ProgressBar progressBar = new ProgressBar();
    /**
     * Services
     */
    private final EsptoolService esptoolService;
    private final ComputeSha256Service computeSha256Service;
    private final EsptoolExecutableService esptoolExecutableService;
    private final Sinks.Many<EsptoolVersionMessageListItemEvent> publishEsptoolVersionEvent;
    /**
     * Mutable properties
     */
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
        final String fixedDir = JAVA_IO_USER_HOME_DIR_OS.concat(ESPFLOW_DIR).concat(CUSTOM_ESPTOOL);

        final FlashUploadHandler uploadHandler = new FlashUploadHandler(fixedDir, this.upload)
                .whenStart((transferContext) -> {
                    this.upload.clearFileList();
                    final String initCustomFileName = fixedDir.concat(transferContext.fileName());
                    log.info("Upload started custom initial path {}", initCustomFileName);
                })
                .whenComplete(whenCompleteUpload(fixedDir));

        this.upload.setUploadHandler(uploadHandler);
        var executableIcon = SvgFactory.createIconFromSvg(EXECUTABLE_ICON, "20px", null);
        executableIcon.addClassName(BLACK_TO_WHITE_ICON);
        this.comboBoxEsptoolHome.setPrefixComponent(executableIcon);
        this.comboBoxEsptoolHome.setItemLabelGenerator(EsptoolExecutableDto::displayAbsoluteEsptoolPathForCombo);
        this.comboBoxEsptoolHome.setRenderer(EsptoolExecutableDto.rendererExecutableIcon());
        this.comboBoxEsptoolHome.addValueChangeListener(event -> {
            if (event.isFromClient()/*Only run me if it is a click from the client.*/) {
                final EsptoolExecutableDto esptoolExecutableDtoItem = event.getValue();
                if (Objects.nonNull(esptoolExecutableDtoItem)) {
                    this.esptoolExecutableService.selectThisEsptoolExecutableVersion(esptoolExecutableDtoItem);
                    this.esptoolService.showEsptoolVersion(esptoolExecutableDtoItem)
                            .subscribe(this.subscribeComboListener(esptoolExecutableDtoItem));
                }
            }
        });
    }

    private SerializableBiConsumer<TransferContext, Boolean> whenCompleteUpload(String fixedDir) {
        return (transferContext, success) -> {
            if (success) {
                this.progressBar.setVisible(true);
                final String initCustomFileName = fixedDir.concat(transferContext.fileName());
                this.computeSha256Service.computeSha256(initCustomFileName)/*To differentiate from an incorrect executable*/
                        .doOnError(this.errorProcessingWhenComputingSha256(initCustomFileName))
                        .map(this.esptoolSha256DtoToEsptoolExecutableDto(initCustomFileName))
                        .flatMap(this::returnEmptyIfVersionAlreadyExists)
                        .switchIfEmpty(this.fallback())
                        .flatMap(this.configureNewDirectoryAndMakeExecutable(transferContext, initCustomFileName))
                        .doOnTerminate(this.uploadCompletedSuccessFully(transferContext))
                        .subscribe(this.subscribeSaveAndUpdate());
            } else {
                log.error("Upload failed");
                transferContext.getUI().access(() -> ConfirmDialogBuilder.showWarningUI("Upload failed", transferContext.getUI()));
                this.upload.clearFileList();
            }
        };
    }

    private Runnable uploadCompletedSuccessFully(TransferContext transferContext) {
        return () -> {
            log.info("Upload completed successfully");
            transferContext.getUI().access(() -> ConfirmDialogBuilder.showInformationUI("Upload completed successfully", transferContext.getUI()));
        };
    }

    /**
     * <p>The new directory is configured with the uploaded esptool version.</p>
     *
     * @param transferContext    the event when uploading
     * @param fileName the current filename
     * @return a {@link Function} with newEsptoolVersionDir, set to "rwx--x--x"
     */
    private Function<EsptoolExecutableDto, Mono<EsptoolExecutableDto>> configureNewDirectoryAndMakeExecutable(final TransferContext transferContext, String fileName) {
        return esptoolExecutableDto -> {
            if (esptoolExecutableDto.esptoolVersion().isEmpty()) {
                return Mono.empty();
            }
            String version = "";
            Path newEsptoolVersionDir = null;
            try {
                version = esptoolExecutableDto.esptoolVersion().split(" ")[1];
                newEsptoolVersionDir = Path.of(JAVA_IO_USER_HOME_DIR_OS.concat(ESPFLOW_DIR)
                        .concat(CUSTOM_ESPTOOL) + version + "/" + transferContext.fileName());
            } catch (ArrayIndexOutOfBoundsException ex) {
                log.error("Error parsing version! {}", ex.getMessage());
                return Mono.empty();
            }
            try {
                Files.createDirectories(newEsptoolVersionDir.getParent());
            } catch (IOException e) {
                log.error("Error creating directory with new version! {}", e.getMessage());
                return Mono.empty();
            }
            // newEsptoolVersionDir home/user/.espflow/1.0.0/esptool/v4.x.x/esptool
            try {
                Files.move(Path.of(fileName), newEsptoolVersionDir, StandardCopyOption.REPLACE_EXISTING);
                log.info("custom modified path {}", newEsptoolVersionDir.toAbsolutePath().toString());
            } catch (IOException e) {
                log.error("Error when moving executable to new directory! {}", e.getMessage());
                return Mono.empty();
            }
            //"rwx--x--x"
            this.makeExecutable(newEsptoolVersionDir.toString());
            final EsptoolExecutableDto entityToUpdate = EsptoolExecutableMapper.INSTANCE
                    .executableDtoWithNewDirectory(esptoolExecutableDto, newEsptoolVersionDir);

            return Mono.just(entityToUpdate);
        };
    }

    /**
     * @param esptoolExecutableDtoItem select by the user, and will be marked as true for use.
     * @return A {@link Consumer} with String
     */
    private Consumer<String> subscribeComboListener(EsptoolExecutableDto esptoolExecutableDtoItem) {
        return esptoolVersion -> {
            this.executeCommandIfPresent(() -> {
                this.updateTextFieldWithComputeSha256(esptoolExecutableDtoItem.sha256());
                this.publishEventAndRefreshHelperText(esptoolExecutableDtoItem);
            });
        };
    }

    /**
     * @return A {@link Consumer}
     */
    private Consumer<EsptoolExecutableDto> subscribeSaveAndUpdate() {
        return esptoolExecutableDto -> {
            if (isANewVersionOfExecutableEsptool(esptoolExecutableDto)) {
                this.saveAndUpdate(esptoolExecutableDto);
            }
        };
    }

    private boolean isANewVersionOfExecutableEsptool(EsptoolExecutableDto esptoolExecutableDto) {
        return !esptoolExecutableDto.esptoolVersion().isEmpty();
    }

    /**
     * Mapping from EsptoolSha256Dto to EsptoolExecutableDto
     *
     * @param initCustomFileName the String absolute path file home/user/.espflow/1.0.0/esptool/esptool
     * @return A {@link Function}
     */
    private Function<EsptoolSha256Dto, EsptoolExecutableDto> esptoolSha256DtoToEsptoolExecutableDto(String initCustomFileName) {
        return esptoolSha256Dto -> EsptoolSha256Mapper.INSTANCE.esptoolSha256ToEsptoolExecutableDto(initCustomFileName, esptoolSha256Dto,
                false, true);
    }

    /**
     * Fallback with empty esptool version
     *
     * @return A {@link Mono} with EsptoolExecutableDto
     */
    private Mono<EsptoolExecutableDto> fallback() {
        return Mono.defer(() -> Mono.just(EsptoolExecutableDto
                .builder()
                .esptoolVersion(StringUtils.EMPTY)
                .build()));
    }

    /**
     * <p>If the executable exists, we notify, delete from dir, and send empty to trigger the fallback, otherwise we save</p>
     *
     * @return A {@link Function}
     */
    private Mono<EsptoolExecutableDto> returnEmptyIfVersionAlreadyExists(EsptoolExecutableDto esptoolExecutableParam) {
        final EsptoolExecutableDto esptoolExecutableDto = this.esptoolExecutableService
                .findByEsptoolVersionWithBundle(esptoolExecutableParam.esptoolVersion(), false)
                .orElse(null);
        if (Objects.nonNull(esptoolExecutableDto)) {
            this.executeCommandIfPresent(() -> {
                ConfirmDialogBuilder.showWarning("This version of the executable has already been loaded. " +
                        esptoolExecutableParam.esptoolVersion());
            });
            try {
                Files.deleteIfExists(Path.of(esptoolExecutableParam.absolutePathEsptool()));
            } catch (IOException e) {
                log.info("Error when trying to delete invalid loaded executable! {}", e.getMessage());
                this.executeCommandIfPresent(() -> {
                    ConfirmDialogBuilder.showWarning("Error when trying to delete invalid loaded executable!");
                });
            }
            return Mono.empty();
        }
        return Mono.just(this.esptoolExecutableService.save(esptoolExecutableParam));
    }

    /**
     * @param fileName the String absolute path file home/user/.espflow/1.0.0/esptool/esptool
     * @return A {@link Consumer}
     */
    private Consumer<Throwable> errorProcessingWhenComputingSha256(String fileName) {
        return onError -> {
            this.executeErrorCommandFromTextField(onError.getMessage());
            try {
                Files.deleteIfExists(Path.of(fileName));
            } catch (IOException e) {
                log.info("Error when trying to delete invalid loaded executable {}", e.getMessage());
                this.executeCommandIfPresent(() -> {
                    ConfirmDialogBuilder.showWarning("Error when trying to delete invalid loaded executable!");
                });
            }
        };
    }

    /**
     * @return A {@link Scroller} with the layout content
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
        this.comboBoxEsptoolHome.setItems(this.esptoolExecutableService.findAll());
        this.setValueForCombo();

        upload.setDropAllowed(true);
        upload.setMaxFiles(1);
        upload.addClassName("esptool-homepath-upload");
        Tooltip.forComponent(upload).setText("Drop executable here!");
        this.i18N(upload);

        final Div divCombo = new Div(comboBoxEsptoolHome, upload);
        comboBoxEsptoolHome.addClassName(LumoUtility.Margin.Right.MEDIUM);
        divCombo.addClassNames(LumoUtility.Display.FLEX, LumoUtility.FlexDirection.ROW);
        divCombo.setWidthFull();

        this.esptoolLayout.add(divCombo);
        this.esptoolLayout.addClassNames(LumoUtility.Margin.Bottom.XSMALL, LumoUtility.Margin.Top.MEDIUM);
        this.esptoolLayout.setColumnSpan(ColumnSpan.COLUMN_SPAN_FULL, divCombo);
        this.esptoolLayout.setAlignItems(AlignItems.CENTER);
        this.esptoolLayout.setGap(Gap.SMALL);

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
        super.setFlexDirection(FlexDirection.COLUMN);
        // Viewport > 1024px
        super.setDisplay(Breakpoint.LARGE, Display.GRID);
        super.setColumns(GridColumns.COLUMNS_2);
        super.setColumnGap(Gap.MEDIUM);
        super.setColumnSpan(ColumnSpan.COLUMN_SPAN_FULL, title, description, description2, esptoolLayout,
                separator, textFieldHash);

        final Scroller scroller = new Scroller(this);
        scroller.setScrollDirection(Scroller.ScrollDirection.VERTICAL);
        scroller.getStyle().set("scrollbar-width", "thin");

        return scroller;
    }

    /**
     * Create a new directory with the new executable version, select the uploaded executable, and then refresh the other
     * components, as well as notify the main panel.
     *
     * @param entityToUpdate the entity to update
     */
    private void saveAndUpdate(final EsptoolExecutableDto entityToUpdate) {
        this.executeCommandIfPresent(() -> {
            this.esptoolExecutableService.selectThisEsptoolExecutableVersion(entityToUpdate);
            this.comboBoxEsptoolHome.setItems(this.esptoolExecutableService.findAll());
            this.comboBoxEsptoolHome.setValue(entityToUpdate);
            final int overlayLength = entityToUpdate.esptoolVersion().concat(entityToUpdate.absolutePathEsptool()).length();
            this.overlay = (overlayLength * 9) + "px";
            this.comboBoxEsptoolHome.getStyle().set("--vaadin-combo-box-overlay-width", overlay);
            this.updateTextFieldWithComputeSha256(entityToUpdate.sha256());
            this.publishEventAndRefreshHelperText(entityToUpdate);
        });
    }

    /**
     * @param textEsptoolVersion      the String with esptool version
     * @param textAbsolutePathEsptool the String absolute path file
     * @return A {@link Component}
     */
    private Component createHelperText(final String textEsptoolVersion, final String textAbsolutePathEsptool) {
        final Span estoolVersionSpan = new Span(textEsptoolVersion);
        estoolVersionSpan.addClickListener(event -> {
            UI.getCurrent().getElement().executeJs(WINDOW_COPY_TO_CLIPBOARD, textAbsolutePathEsptool);
            Notification.show("Copied " + textEsptoolVersion, 2000, Notification.Position.MIDDLE);
        });
        Tooltip.forComponent(estoolVersionSpan).setText(textEsptoolVersion);
        final Div div1 = new Div(estoolVersionSpan);
        div1.getElement().getThemeList().add("badge pill");

        final Span estoolAbsolutePathSpan = new Span(textAbsolutePathEsptool);
        estoolAbsolutePathSpan.setId("estoolAbsolutePathSpan");
        estoolAbsolutePathSpan.setWidth("200px");
        estoolAbsolutePathSpan.getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        estoolAbsolutePathSpan.addClassNames(
                LumoUtility.TextOverflow.ELLIPSIS,
                LumoUtility.Whitespace.NOWRAP,
                LumoUtility.Overflow.HIDDEN,
                "estool-absolute-path-span");

        estoolAbsolutePathSpan.addClickListener(event -> {
            UI.getCurrent().getElement().executeJs(WINDOW_COPY_TO_CLIPBOARD, textAbsolutePathEsptool);
            Notification.show("Copied " + textAbsolutePathEsptool, 2000, Notification.Position.MIDDLE);
        });
        Tooltip.forComponent(estoolAbsolutePathSpan).setText(textAbsolutePathEsptool);

        estoolAbsolutePathSpan.getStyle().setWidth("200px");
        estoolAbsolutePathSpan.addClassNames(LumoUtility.Display.INLINE_BLOCK,
                LumoUtility.TextOverflow.ELLIPSIS,
                LumoUtility.Whitespace.NOWRAP,
                LumoUtility.Overflow.HIDDEN);

        final Div div2 = new Div(estoolAbsolutePathSpan);
        div2.getElement().getThemeList().add("badge pill");

        final HorizontalLayout row = new HorizontalLayout(div1, div2);
        row.setWidthFull();
        row.getStyle().setCursor(EspFlowConstants.CURSOR_POINTER);
        row.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);
        Animated.animate(row, Animated.Animation.FADE_IN);

        return row;
    }

    /**
     * Simple shortcut or ui.access()
     *
     * @param command
     */
    public void executeCommandIfPresent(final Command command) {
        getUI().ifPresent(ui -> ui.access(command));
    }

    /**
     * @param inputHash the String with sha256
     */
    public void updateTextFieldWithComputeSha256(final String inputHash) {
        if (Objects.nonNull(inputHash)) {
            if (inputHash.contains("sha256 does not match!")) {
                final String sha256 = inputHash.split("!")[1].trim();
                this.showSpanSha256Result(sha256, "sha256 does not match!", VaadinIcon.WARNING, LumoUtility.TextColor.ERROR);
            } else {
                this.showSpanSha256Result(inputHash, "sha256 match!", VaadinIcon.INFO, LumoUtility.TextColor.PRIMARY);
            }
        } else {
            ConfirmDialogBuilder.showWarning("Input hash is null " + inputHash);
        }
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
        span.getStyle().setCursor(EspFlowConstants.CURSOR_POINTER);
        span.addClickListener(event -> {
            span.removeAll();
            span.add(VaadinIcon.CHECK.create());
            UI.getCurrent().getElement().executeJs(WINDOW_COPY_TO_CLIPBOARD, sha256);
            Notification.show("Copied sha256: " + sha256, 2500, Notification.Position.MIDDLE);
            Mono.just(span)
                    .delayElement(Duration.ofMillis(1500))
                    .subscribe(btn -> {
                        span.getUI().ifPresent(ui -> ui.access(() -> {
                            span.removeAll();
                            span.add(copyButtonFromSvg);
                        }));
                    });
        });
        Tooltip.forComponent(span).setText("Copy sha256");
        this.progressBar.setVisible(false);
        this.textFieldHash.setSuffixComponent(span);
        this.textFieldHash.setValue(sha256);
        this.textFieldHash.removeClassName(LumoUtility.TextColor.ERROR);
        this.textFieldHash.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);

        final Span spanSha256HelperText = new Span();
        spanSha256HelperText.add(icon.create());
        spanSha256HelperText.add(spanText);
        spanSha256HelperText.addClassNames(LumoUtility.FontSize.SMALL, textColor);

        this.textFieldHash.setHelperComponent(spanSha256HelperText);
    }

    /**
     * @param error the String with Error
     */
    public void executeErrorCommandFromTextField(final String error) {
        this.executeCommandIfPresent(() -> {
            var icon = SvgFactory.createCopyButtonFromSvg();
            final Span span = new Span(icon);
            span.addClickListener(event -> {
                UI.getCurrent().getElement().executeJs(WINDOW_COPY_TO_CLIPBOARD, error);
                Notification.show("Copied error: " + error, 2500, Notification.Position.MIDDLE);
            });
            Tooltip.forComponent(span).setText("Copy error");
            this.progressBar.setVisible(false);
            this.textFieldHash.setSuffixComponent(span);
            this.textFieldHash.setValue(error);
            this.textFieldHash.removeClassName(LumoUtility.TextColor.ERROR);
            this.textFieldHash.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.ERROR);
            final Span spanSha256 = new Span(VaadinIcon.WARNING.create());
            spanSha256.add("unexpected error!");
            spanSha256.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.ERROR);
            this.textFieldHash.setHelperComponent(spanSha256);
            ConfirmDialogBuilder.showWarning("This executable cannot be processed, the hashes do not match.");
        });
    }

    /**
     * Sets the value of the combo if previously selected to true, initially the default is bundled to true.
     */
    private void setValueForCombo() {
        this.esptoolExecutableService.findByIsSelectedToTrue()
                .ifPresent(esptoolExecutableDto -> {
                    this.comboBoxEsptoolHome.setValue(esptoolExecutableDto);
                    final int overlayLength = esptoolExecutableDto.esptoolVersion().concat(esptoolExecutableDto.absolutePathEsptool()).length();
                    this.overlay = (overlayLength * 9) + "px";
                    this.comboBoxEsptoolHome.getStyle().set("--vaadin-combo-box-overlay-width", overlay);
                    this.comboBoxEsptoolHome.setHelperComponent(createHelperText(esptoolExecutableDto.esptoolVersion(),
                            esptoolExecutableDto.absolutePathEsptool()));
                    this.updateTextFieldWithComputeSha256(esptoolExecutableDto.sha256());
                });
    }

    /**
     * @param esptoolExecutableDto to publish
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
                .subscribe(espToolVersion -> {
                    ui.access(() -> {
                        comboBoxEsptoolHome.setItems(this.esptoolExecutableService.findAll());
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
            //log.info("Attach {}", SettingsEsptoolHomePathContent.class.getSimpleName());
        }
        this.setValueForCombo();
    }
}
