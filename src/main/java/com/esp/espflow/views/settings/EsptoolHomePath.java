package com.esp.espflow.views.settings;

import com.esp.espflow.enums.Breakpoint;
import com.esp.espflow.service.EsptoolService;
import com.esp.espflow.views.Layout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.UploadI18N;
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Arrays;

import static com.esp.espflow.util.EspFlowConstants.BOX_SHADOW_VAADIN_BUTTON;
import static com.esp.espflow.util.EspFlowConstants.ESPTOOL_PY_NOT_FOUND;

/**
 * @author rub'n
 */
@Log4j2
@UIScope
@SpringComponent
@RequiredArgsConstructor
public class EsptoolHomePath {

    private final EsptoolService esptoolService;
    private final Layout esptoolLayout = new Layout();
    private Upload upload;

    public Layout createEsptoolHomePathContent() {
        esptoolLayout.removeAll();
        H2 title = new H2("Esptool home path");
        title.addClassNames(LumoUtility.FontSize.XLARGE, LumoUtility.Margin.Top.MEDIUM);
        title.setId(title.getText().replace(" ", "-").toLowerCase());

        Paragraph description = new Paragraph("By default it uses a version of esptool.py from the system's temporary directory, we can establish one by selecting it from here");
        description.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);

        final TextField currentTextField = createTextField();
        this.upload = this.configureUploadButton(currentTextField);
        this.setEsptoolPyVersion(currentTextField);
        this.esptoolLayout.add(currentTextField, upload);
        this.esptoolLayout.addClassNames(LumoUtility.Margin.Bottom.XSMALL, LumoUtility.Margin.Top.MEDIUM);
        this.esptoolLayout.setColumnSpan(Layout.ColumnSpan.COLUMN_SPAN_FULL, currentTextField);
        this.esptoolLayout.setGap(Layout.Gap.SMALL);

        final Layout layout = new Layout(title, description, esptoolLayout);

        layout.addClickListener(event -> {
            this.esptoolLayout.removeAll();
            layout.remove(esptoolLayout);
            final TextField newTextField = this.createTextField();
            this.upload = this.configureUploadButton(newTextField);
            this.esptoolLayout.add(newTextField, upload);
            this.esptoolLayout.addClassNames(LumoUtility.Margin.Bottom.XSMALL, LumoUtility.Margin.Top.MEDIUM);
            this.esptoolLayout.setColumnSpan(Layout.ColumnSpan.COLUMN_SPAN_FULL, currentTextField);
            this.esptoolLayout.setGap(Layout.Gap.SMALL);
            layout.add(esptoolLayout);
            this.setEsptoolPyVersion(newTextField);
        });

        // Viewport < 1024px
        layout.setFlexDirection(Layout.FlexDirection.COLUMN);
        // Viewport > 1024px
        layout.setDisplay(Breakpoint.LARGE, Layout.Display.GRID);
        layout.setColumns(Layout.GridColumns.COLUMNS_2);
        layout.setColumnGap(Layout.Gap.MEDIUM);
        layout.setColumnSpan(Layout.ColumnSpan.COLUMN_SPAN_FULL, title, description, esptoolLayout);

        return layout;
    }

    private TextField createTextField() {
        final TextField textFieldEsptoolBundle = new TextField();
        textFieldEsptoolBundle.setWidthFull();
        textFieldEsptoolBundle.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);
        return textFieldEsptoolBundle;
    }

    public Upload configureUploadButton(TextField textFieldEsptoolBundle) {
        final Upload upload = new Upload();
        final FileBuffer buffer = new FileBuffer();
        upload.setDropAllowed(false);
        upload.setMaxFiles(10);
        upload.setReceiver(buffer);
        upload.addClassName("esptool-homepath-upload");
        this.i18N(upload);

        upload.addStartedListener(event -> {
            final String esptoolCustomPath = Path.of(event.getFileName()).toAbsolutePath().toString();
            log.info("Esptool custom path {}", esptoolCustomPath);
            textFieldEsptoolBundle.setValue(esptoolCustomPath);
        });

        upload.addSucceededListener(event -> {
            final String esptoolCustomPath = Path.of(event.getFileName()).toAbsolutePath().toString();
            log.info("Esptool custom path addSucceededListener {}", esptoolCustomPath);
            textFieldEsptoolBundle.setValue(esptoolCustomPath);
        });

        return upload;
    }

    /**
     * @param textField
     */
    private void setEsptoolPyVersion(TextField textField) {
        final ProgressBar progressBar = new ProgressBar();
        progressBar.setWidthFull();
        progressBar.setVisible(true);
        progressBar.setIndeterminate(true);
        textField.setPrefixComponent(progressBar);
        this.esptoolService.showEsptoolVersion()
                .doOnError(error -> {
                    textField.getUI().ifPresent(ui -> {
                        ui.access(() -> {
                            log.info("doOnError: {}", error.getMessage());
                            textField.setPlaceholder(ESPTOOL_PY_NOT_FOUND);
                        });
                    });
                })
                .delayElements(Duration.ofSeconds(2))
                .doOnTerminate(() -> {
                    textField.getUI().ifPresent(ui -> ui.access(() -> {
                        progressBar.setVisible(false);
                        log.info("doOnComplete: {}", textField);
                    }));
                })
                .subscribe(espToolVersion -> {
                    textField.getUI().ifPresent(ui -> {
                        ui.access(() -> {
                            textField.setPlaceholder("Bundle ".concat(espToolVersion));
                        });
                    });
                });
    }

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

}
