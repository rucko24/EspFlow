package com.esp.espflow.views.flashesp;

import com.esp.espflow.data.util.ConfirmDialogBuilder;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.UploadI18N;
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static com.esp.espflow.data.util.EspFlowConstants.*;

@Log4j2
@UIScope
@SpringComponent
@RequiredArgsConstructor
public class DivFlashUploader extends Div {

    private final FileBuffer buffer = new FileBuffer();
    private final Upload upload = new Upload();
    private final UploadExamplesI18N uploadI18N = new UploadExamplesI18N();
    private final ApplicationEventPublisher publisher;

    @PostConstruct
    public void constructDiv() {
        this.uploadInitialConfig();
        this.addListeners();
        this.uploadStyles();
        this.i18N();

        final Div divUploader = new Div(upload);
        divUploader.setWidthFull();
        final Div divH3Firmware = this.h3Firmware();

        super.add(divH3Firmware, divUploader);
        super.setWidthFull();
        //super.setSizeFull();
        super.getStyle().set(DISPLAY, "flex");
        super.getStyle().set(MARGIN_LEFT, MARGIN_10_PX);
        super.addClassName("firmwareh3-vaadin-upload-div");
    }

    private void uploadInitialConfig() {
        upload.setReceiver(buffer);
        upload.setMaxFiles(1);
        upload.setAcceptedFileTypes(MediaType.APPLICATION_OCTET_STREAM_VALUE, ".bin");
    }

    private void addListeners() {

        upload.addSucceededListener(event -> {
            upload.getElement()
                    .executeJs("this.shadowRoot.querySelector('vaadin-upload-file').className = 'meta'");

            log.info("New file uploaded {}", event.getFileName());
            long contentLength = event.getContentLength();
            String mimeType = event.getMIMEType();

            publisher.publishEvent(event.getFileName());
            this.createUploadDir(buffer, event.getFileName());


            // Do something with the file data
            // processFile(fileData, fileName, contentLength, mimeType);
        });

        upload.addStartedListener(event -> {
            log.info("Handling upload of " + event.getFileName() + " ("
                    + event.getContentLength() + " bytes) started");
        });

        upload.addFileRejectedListener(event -> {
            ConfirmDialogBuilder.showWarning(event.getErrorMessage());
        });

        upload.addFailedListener(failedEvent -> {
            log.error("Error addFailed Listernet {}", failedEvent.getReason().getMessage());
            ConfirmDialogBuilder.showWarning(failedEvent.getReason().getMessage());
        });

    }

    /**
     *
     */
    private void createUploadDir(FileBuffer buffer, String fileName) {
        final String resourcesDir = "src/main/resources/";
        final var flashesDir = Path.of(resourcesDir.concat("flashesdir/"));
        if (!Files.exists(flashesDir)) {
            try {
                Files.createDirectory(flashesDir);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        // Get information about the uploaded file
        final Path fileNameResult = flashesDir.resolve(Path.of(fileName));
        try (var input = new BufferedInputStream(buffer.getInputStream());
             var outPut = new BufferedOutputStream(Files.newOutputStream(fileNameResult))) {

            input.transferTo(outPut);

        } catch (IOException ex) {
            log.error("Error a la hora de escribir el {}", ex.getMessage());
        }
    }

    private void uploadStyles() {
        upload.getStyle().set("display", "flex");
        upload.getStyle().set("border", "none");
        upload.getStyle().set("padding", "0");
        upload.getStyle().set("margin-left", "10px");
        upload.addClassName("vaadin-upload");
    }

    private Div h3Firmware() {
        final H3 h3 = new H3("Firmware");
        h3.getStyle().set(MARGIN_TOP, AUTO);
        final Div div = new Div(h3);
        div.addClassName("h2-firmware-div");
        return div;
    }

    private void i18N() {
        uploadI18N.getAddFiles().setOne("Select firmware...");

        uploadI18N.getDropFiles().setOne("Drop firmware here...");
        uploadI18N.getError()
                .setIncorrectFileType(
                        "The provided file doesn't have the correct format. Please provide a .bin file.");
        upload.setI18n(uploadI18N);
        upload.getUploadButton().addClassName(BOX_SHADOW_VAADIN_BUTTON);

    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        this.upload.clearFileList();
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        upload.setI18n(uploadI18N);
    }

    public static class UploadExamplesI18N extends UploadI18N {
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
