package com.nodemcuui.tool.views.flashesp32.testdownload;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Receiver;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;

import java.nio.file.Path;

public class UploadArea extends VerticalLayout {

    private final Upload uploadField;
    private final Span errorField;

    public UploadArea(Path uploadFolder) {
        uploadField = new Upload(createFileReceiver(uploadFolder));
        //uploadField.setMaxFiles(1);
        //uploadField.setAcceptedFileTypes("application/pdf", ".pdf");
        //uploadField.setAcceptedFileTypes(MediaType.APPLICATION_OCTET_STREAM_VALUE, ".bin");

        uploadField.setDropLabel(new Span("Drop file here (max 1MB)"));

        errorField = new Span();
        errorField.setVisible(false);
        errorField.getStyle().set("color", "red");

        uploadField.addFailedListener(e -> showErrorMessage(e.getReason().getMessage()));
        uploadField.addFileRejectedListener(e -> showErrorMessage(e.getErrorMessage()));

        add(uploadField, errorField);
    }

    public Upload getUploadField() {
        return uploadField;
    }

    public void hideErrorField() {
        errorField.setVisible(false);
    }

    private Receiver createFileReceiver(Path uploadFolder) {
        final MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
        buffer.getFileData(uploadFolder.getFileName().toString());
        return buffer;
    }

    private void showErrorMessage(String message) {
        errorField.setVisible(true);
        errorField.setText(message);
    }
}
