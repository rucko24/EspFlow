package com.nodemcutools.application.views.flashesp32;

import com.nodemcutools.application.data.util.NotificationsUtils;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.UploadI18N;
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;

import javax.annotation.PostConstruct;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

@Log4j2
@UIScope
@SpringComponent
@RequiredArgsConstructor
public class DivFlashUploader extends Div implements NotificationsUtils {

    @PostConstruct
    public void constructDiv() {
        removeAll();
        final FileBuffer buffer = new FileBuffer();
        final Upload upload = new Upload(buffer);

        AtomicReference<String> fileName = new AtomicReference<>();
        upload.addSucceededListener(event -> {
            upload.getElement()
                    .executeJs("this.shadowRoot.querySelector('vaadin-upload-file').className = 'meta'");
            // Get information about the uploaded file
            try (var input = new BufferedInputStream(buffer.getInputStream())) {

            } catch (IOException ex) {

            }
            fileName.set(event.getFileName());
            long contentLength = event.getContentLength();
            String mimeType = event.getMIMEType();

            // Do something with the file data
            // processFile(fileData, fileName, contentLength, mimeType);
        });
        log.info("Nombre del fichero: {}", fileName.get());

        upload.addStartedListener(e -> {
            upload.getElement()
                    .executeJs("this.shadowRoot.querySelector('vaadin-upload-file').className = 'meta'");
            log.info("Handling upload of " + e.getFileName() + " ("
                    + e.getContentLength() + " bytes) started");
        });

        upload.addFileRejectedListener(event -> {
            String errorMessage = event.getErrorMessage();
            this.showError(errorMessage);
        });

        upload.addSucceededListener(event -> {
        });

        upload.addFailedListener( e -> {
            log.error("Error addFailed Listernet {}", e.getReason().getMessage());
            showError(e.getReason().getMessage());
        });

        upload.setAcceptedFileTypes(MediaType.APPLICATION_OCTET_STREAM_VALUE, ".bin");
        upload.setAutoUpload(Boolean.FALSE);
        upload.getStyle().set("display","flex");
        upload.getStyle().set("border", "none");
        upload.getStyle().set("padding", "0");
        upload.getStyle().set("margin-left", "10px");
        final UploadExamplesI18N uploadI18N = new UploadExamplesI18N();
        uploadI18N.getAddFiles().setOne("Select firmware...");
        uploadI18N.getDropFiles().setOne("Drop FIRMWARE here...");
        upload.setI18n(uploadI18N);
        final Div divUploader = new Div(upload);

        final H3 h3 = new H3("Firmware");
        h3.getStyle().set(FlashEsp32View.MARGIN_TOP, FlashEsp32View.AUTO);
        final Div divH3 = new Div(h3);


        // dentro del slot "file-list"
        // propiedad part="meta" darle width: 100% o a 0px, para que el nombre del fichero se vea completo en Horizontal
        //https://github.com/tarekoraby/upload-demo/blob/main/src/main/java/org/vaadin/example/MainView.java
        super.add(divH3, divUploader);
        super.setWidthFull();
        super.getStyle().set(FlashEsp32View.DISPLAY, "flex");
        super.getStyle().set(FlashEsp32View.MARGIN_LEFT, FlashEsp32View.MARGIN_10_PX);
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

    }

    private class UploadExamplesI18N extends UploadI18N {
        public UploadExamplesI18N() {
            setDropFiles(new DropFiles()
                    .setOne("Drop file here")
                    .setMany("Drop files here"));
            setAddFiles(new AddFiles()
                    .setOne("Upload File...")
                    .setMany("Upload Files..."));
            setCancel("Cancel");
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
                    .setSize(Arrays.asList("B", "kB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB")));
        }
    }
}
