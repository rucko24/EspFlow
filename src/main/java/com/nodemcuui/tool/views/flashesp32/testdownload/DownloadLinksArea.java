package com.nodemcuui.tool.views.flashesp32.testdownload;

import com.nodemcuui.tool.data.util.downloader.DownloadFlashButton;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.StreamResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;

public class DownloadLinksArea extends VerticalLayout {

    private final Path uploadFolder;
    private DownloadFlashButton downloadFlashButton = new DownloadFlashButton();

    public DownloadLinksArea(Path uploadFolder) {
        this.uploadFolder = uploadFolder;
        refreshFileLinks();
        setMargin(true);
    }

    public void refreshFileLinks() {
        removeAll();
        add(new H4("Download Links:"));

       // downloadFlashButton.saveFirmware(uploadFolder.getFileName().toString());

        for (File file : uploadFolder.toFile().listFiles()) {
            addLinkToFile(file);
        }

    }

    private void addLinkToFile(File file) {
        StreamResource streamResource = new StreamResource(file.getName(), () -> getStream(file));
        Anchor link = new Anchor(streamResource, String.format("%s (%d KB)", file.getName(),
                (int) file.length() / 1024));
        link.getElement().setAttribute("download", true);
        add(link);
    }

    private InputStream getStream(File file) {
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return stream;
    }
}
