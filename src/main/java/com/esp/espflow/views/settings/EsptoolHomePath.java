package com.esp.espflow.views.settings;

import com.esp.espflow.enums.Breakpoint;
import com.esp.espflow.service.EsptoolService;
import com.esp.espflow.util.svgfactory.SvgFactory;
import com.esp.espflow.views.Layout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.UploadI18N;
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.vaadin.olli.ClipboardHelper;
import reactor.core.publisher.Mono;

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
    private final Upload upload = new Upload();
    private final TextField textfieldEsptoolHome = new TextField();
    private final Button copyButton = new Button();
    private final SvgIcon copySvgButton = SvgFactory.createCopyButtonFromSvg();
    private final ClipboardHelper clipboardHelper = new ClipboardHelper();

    @PostConstruct
    public void init() {
        this.esptoolLayout.removeAll();
        //Add the listener only once
        this.initListeners();
    }

    private void initListeners() {
        this.upload.addStartedListener(event -> {
            final String esptoolCustomPath = Path.of(event.getFileName()).toAbsolutePath().toString();
            log.info("Esptool custom path addStartedListener {}", esptoolCustomPath);
            textfieldEsptoolHome.setValue(esptoolCustomPath);
            this.clipboardHelper.setContent(textfieldEsptoolHome.getValue());
            this.copyButton.setTooltipText(textfieldEsptoolHome.getValue());
            this.upload.clearFileList();
        });

        this.clipboardHelper.wrap(this.copyButton);
        this.copyButton.setIcon(this.copySvgButton);
        this.copyButton.addClickListener(event -> {
            Notification.show("Copied " + this.textfieldEsptoolHome.getValue(), 2500, Notification.Position.MIDDLE);
            this.copyButton.setIcon(VaadinIcon.CHECK.create());
            this.upload.clearFileList();
            Mono.just(this.copyButton)
                    .delayElement(Duration.ofMillis(1500))
                    .subscribe(subscribeButton -> {
                        subscribeButton.getUI().ifPresent(ui -> ui.access(() -> {
                            subscribeButton.setIcon(this.copySvgButton);
                            this.clipboardHelper.setContent(this.textfieldEsptoolHome.getValue());
                        }));
                    });
        });
        this.textfieldEsptoolHome.setValueChangeMode(ValueChangeMode.EAGER);
        this.textfieldEsptoolHome.addValueChangeListener(event -> this.upload.clearFileList());

    }

    public Layout createEsptoolHomePathContent() {
        esptoolLayout.removeAll();
        H2 title = new H2("Esptool home path");
        title.addClassNames(LumoUtility.FontSize.XLARGE, LumoUtility.Margin.Top.MEDIUM);
        title.setId(title.getText().replace(" ", "-").toLowerCase());

        Paragraph description = new Paragraph("By default it uses a version of esptool.py from the system's temporary directory, we can establish one by selecting it from here");
        description.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);

        this.configureTextField();
        this.configureUploadButton();
        this.setEsptoolPyVersion(textfieldEsptoolHome);

        this.esptoolLayout.add(textfieldEsptoolHome, upload);
        this.esptoolLayout.addClassNames(LumoUtility.Margin.Bottom.XSMALL, LumoUtility.Margin.Top.MEDIUM);
        this.esptoolLayout.setColumnSpan(Layout.ColumnSpan.COLUMN_SPAN_FULL, textfieldEsptoolHome);
        this.esptoolLayout.setAlignItems(Layout.AlignItems.CENTER);
        this.esptoolLayout.setGap(Layout.Gap.SMALL);

        final Layout layout = new Layout(title, description, esptoolLayout);
        // Viewport < 1024px
        layout.setFlexDirection(Layout.FlexDirection.COLUMN);
        // Viewport > 1024px
        layout.setDisplay(Breakpoint.LARGE, Layout.Display.GRID);
        layout.setColumns(Layout.GridColumns.COLUMNS_2);
        layout.setColumnGap(Layout.Gap.MEDIUM);
        layout.setColumnSpan(Layout.ColumnSpan.COLUMN_SPAN_FULL, title, description, esptoolLayout);
        return layout;
    }

    /**
     *
     */
    private void configureTextField() {
        this.textfieldEsptoolHome.setWidthFull();
        this.textfieldEsptoolHome.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);
        this.copyButton.addClassName(BOX_SHADOW_VAADIN_BUTTON);
        //Tooltip.forComponent(spanValue).setText(value.trim());
        this.textfieldEsptoolHome.setSuffixComponent(clipboardHelper);
    }

    /**
     *
     */
    public void configureUploadButton() {
        final FileBuffer buffer = new FileBuffer();
        upload.setDropAllowed(false);
        upload.setMaxFiles(10);
        upload.setReceiver(buffer);
        upload.addClassName("esptool-homepath-upload");
        this.i18N(upload);
    }

    /**
     * @param textField
     */
    private void setEsptoolPyVersion(TextField textField) {
        final ProgressBar progressBar = new ProgressBar();
        //progressBar.setWidthFull();
        progressBar.setVisible(false);
        //progressBar.setIndeterminate(true);
        textField.setPrefixComponent(progressBar);
        this.esptoolService.showEsptoolVersion()
                .doOnError(error -> {
                    textField.getUI().ifPresent(ui -> {
                        ui.access(() -> {
                            //log.info("doOnError: {}", error.getMessage());
                            textField.setValue(ESPTOOL_PY_NOT_FOUND);
                            progressBar.setVisible(false);
                        });
                    });
                })
                .delayElements(Duration.ofSeconds(2))
                .doOnTerminate(() -> {
                    textField.getUI().ifPresent(ui -> ui.access(() -> {
                        progressBar.setVisible(false);
                        //log.info("doOnComplete: {}", textField);
                    }));
                })
                .subscribe(espToolVersion -> {
                    textField.getUI().ifPresent(ui -> {
                        ui.access(() -> {
                            String result = "Bundle ".concat(espToolVersion);
                            textField.setValue(result);
                            //log.info("subscribe: espToolVersion {} {}", espToolVersion, textField);
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
