package com.nodemcutools.application.views.flashesp32;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import javax.annotation.PostConstruct;
import java.io.BufferedInputStream;
import java.io.IOException;

@Log4j2
@UIScope
@SpringComponent
@RequiredArgsConstructor
public class DivFlashUploader extends Div {

    @PostConstruct
    public void constructDiv() {
        removeAll();
        final MemoryBuffer memoryBuffer = new MemoryBuffer();
        final Upload upload = new Upload(memoryBuffer);

        upload.addSucceededListener(event -> {
            upload.getElement()
                    .executeJs("this.shadowRoot.querySelector('vaadin-upload-file').className = 'meta'");
            // Get information about the uploaded file
            try (var input = new BufferedInputStream(memoryBuffer.getInputStream())) {

            } catch (IOException ex) {

            }
            String fileName = event.getFileName();
            long contentLength = event.getContentLength();
            String mimeType = event.getMIMEType();

            // Do something with the file data
            // processFile(fileData, fileName, contentLength, mimeType);
        });
        upload.addStartedListener(e -> {
            log.info("Handling upload of " + e.getFileName() + " ("
                    + e.getContentLength() + " bytes) started");
        });

        upload.addFileRejectedListener(event -> {
            String errorMessage = event.getErrorMessage();

            Notification notification = Notification.show(
                    errorMessage,
                    5000,
                    Notification.Position.MIDDLE
            );
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        });

        upload.setAcceptedFileTypes("application/octet-stream", ".bin");
        upload.getStyle().set("display","flex");
        upload.getStyle().set("border", "none");
        upload.getStyle().set("padding", "0");
        upload.getStyle().set("margin-left", "10px");
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
}
