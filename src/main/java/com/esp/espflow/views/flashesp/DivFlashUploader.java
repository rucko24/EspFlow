package com.esp.espflow.views.flashesp;

import com.esp.espflow.util.ConfirmDialogBuilder;
import com.esp.espflow.util.CreateCustomDirectory;
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

import java.util.Arrays;

import static com.esp.espflow.util.EspFlowConstants.AUTO;
import static com.esp.espflow.util.EspFlowConstants.BOX_SHADOW_VAADIN_BUTTON;
import static com.esp.espflow.util.EspFlowConstants.DISPLAY;
import static com.esp.espflow.util.EspFlowConstants.JAVA_IO_TEMPORAL_DIR_OS;
import static com.esp.espflow.util.EspFlowConstants.MARGIN_10_PX;
import static com.esp.espflow.util.EspFlowConstants.MARGIN_LEFT;
import static com.esp.espflow.util.EspFlowConstants.MARGIN_TOP;

/**
 * @author rubn
 */
@UIScope
@Log4j2
@SpringComponent
@RequiredArgsConstructor
public class DivFlashUploader extends Div implements CreateCustomDirectory {

    private final FileBuffer buffer = new FileBuffer();
    private final Upload upload = new Upload();
    private final UploadExamplesI18N uploadI18N = new UploadExamplesI18N();
    /**
     * Important, when the firmware is uploaded, we publish its name to the FlashEspView.
     */
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

    /**
     * Configure the uploader
     */
    private void uploadInitialConfig() {
        upload.setDropAllowed(true);
        upload.setReceiver(buffer);
        upload.setMaxFiles(1);
        upload.setAcceptedFileTypes(MediaType.APPLICATION_OCTET_STREAM_VALUE, ".bin");
    }

    /**
     * Adding listeners here
     */
    private void addListeners() {

        upload.addSucceededListener(event -> {
            upload.getElement()
                    .executeJs("this.shadowRoot.querySelector('vaadin-upload-file').className = 'meta'");

            log.debug("New file uploaded {}", event.getFileName());
            long contentLength = event.getContentLength();
            String mimeType = event.getMIMEType();

            publisher.publishEvent(event.getFileName());
            this.createUploadDir(buffer, event.getFileName());

        });

        upload.addStartedListener(event -> {
            log.debug("Handling upload of " + event.getFileName() + " ("
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
     * The flash is written to the temporary directory of the operating system once it has been uploaded from the front-end and then read from this directory.
     *
     */
    private void createUploadDir(FileBuffer buffer, String fileName) {
        this.createCustomDirectory(buffer, JAVA_IO_TEMPORAL_DIR_OS.concat("/flash-esptool-write-dir/"), fileName);
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
