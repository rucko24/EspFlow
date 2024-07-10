package com.nodemcuui.tool.data.util.downloader;

import com.infraleap.animatecss.Animated;
import com.infraleap.animatecss.Animated.Animation;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.server.StreamResource;
import lombok.extern.log4j.Log4j2;
import org.vaadin.olli.FileDownloadWrapper;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Clase de utilidad para que un boton pueda crear/descargar un pdf, además permite habilidar/deshabilitar el anchor
 * de descarga cuando sea necesario.
 */
@Log4j2
public class DownloadFlashButton extends FileDownloadWrapper {

    private final Button downloadFlashButton = new Button("Download flash", VaadinIcon.DOWNLOAD.create());
    private String fileName;

    public DownloadFlashButton() {
        super();
        downloadFlashButton.getStyle().set("box-shadow", "0 2px 1px -1px rgba(0, 0, 0, .2), 0 1px 1px 0 rgba(0, 0, 0, .14), 0 1px 3px 0 rgba(0, 0, 0, .12)");
        downloadFlashButton.setTooltipText("Download flash");
    }

    public Button getDownloadFlashButton() {
        return downloadFlashButton;
    }

    /**
     * Deshabilita el boton y remueve el href del anchor
     *
     * @param buttonToWrap
     */
    public void setDisabled(final Button buttonToWrap) {
        super.anchor.removeHref();
        //buttonToWrap.setEnabled(false);
        Animated.removeAnimations(this);
    }

    /**
     * Habilita el boton para que este pueda descargar nuevamente el pdf
     *
     * @param buttonToWrap
     */
    public void setEnabled(final Button buttonToWrap) {
        buttonToWrap.setEnabled(true);
        //this.createPdf(buttonToWrap, fileName);
        Animated.animate(this, Animation.HEART_BEAT);
    }

    /**
     * Esto invoca al StreamResource necesario para escribir el PDF con los byte de la clase PdfExport2
     *
     * @param fileName
     */
    public void saveFirmware(String fileName) {
        this.fileName = fileName;
        final var streamResource = new StreamResource(fileName.concat(".bin"), this::firmwareToByteArray);
        streamResource.setCacheTime(0);
        super.setResource(streamResource);
        super.wrapComponent(downloadFlashButton);
    }

    /**
     * Bytearray con buffer para no cargar todo en memoria
     *
     * @return ByteArrayInputStream
     */
    private ByteArrayInputStream firmwareToByteArray() {
        final Path path;
        try {
            path = Files.createTempFile(fileName, ".bin");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (final var bin = new BufferedInputStream(Files.newInputStream(path));
             final var baos = new ByteArrayOutputStream()) {
            //transfer
            bin.transferTo(baos);
            return new ByteArrayInputStream(baos.toByteArray());
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
        return null;
    }

}
