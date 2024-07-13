package com.nodemcuui.tool.data.util.downloader;

import com.infraleap.animatecss.Animated;
import com.infraleap.animatecss.Animated.Animation;
import com.nodemcuui.tool.data.util.ProcessCommandsInternals;
import com.nodemcuui.tool.data.util.console.ConsoleOutPut;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.server.StreamResource;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.FastByteArrayOutputStream;
import org.vaadin.olli.FileDownloadWrapper;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * https://stackoverflow.com/questions/61513997/how-to-download-a-pdf-from-vaadin-flow-app
 * <p>
 * Escribir la flash en el disco primero luego generar un anchor o un FileDownloadWrapper ? o un anchor normal ?
 * Recuperar de la ruta del Vaadin Servlet para luego guardarlo en otro lugar
 * luego borrarlo del Vaadin Servlet
 */
@Log4j2
public class FlashButtonWrapper extends FileDownloadWrapper {

    private ProcessCommandsInternals processCommandsInternals = new ProcessCommandsInternals();
    private final Button download = new Button(VaadinIcon.DOWNLOAD.create());
    private String fileName;

    private ConsoleOutPut consoleOutPut;
    private UI ui;

    public FlashButtonWrapper(ConsoleOutPut consoleOutPut, UI ui) {
        super();
        setSizeFull();
        this.consoleOutPut = consoleOutPut;
        this.ui = ui;
    }


    public FlashButtonWrapper() {
        super();
        setSizeFull();
    }

    public Button getDownloadButton() {
        return download;
    }

    /**
     * @param writFileToTempDir
     */
    public void enableAnchorForDownloadTheFirmware(final String writFileToTempDir) {
        download.setTooltipText("Download flash backup");
        final var streamResource = new StreamResource("-", () -> this.firmwareToByteArray(writFileToTempDir)) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new ConcurrentHashMap<>(super.getHeaders());
                headers.put("Content-Disposition", "attachment; filename=\"" + writFileToTempDir + "\"");
                return headers;
            }
        };
        streamResource.setCacheTime(0);
        super.setResource(streamResource);
        super.wrapComponent(download);
        download.getStyle().set("box-shadow", "0 2px 1px -1px rgba(0, 0, 0, .2), 0 1px 1px 0 rgba(0, 0, 0, .14), 0 1px 3px 0 rgba(0, 0, 0, .12)");
        Animated.animate(download, Animation.FADE_IN);
    }


    /**
     * <p>
     * Posiblemente mejor guardar en el direcotiro temporal y listo
     * Bytearray con buffer para no cargar todo en memoria
     *
     * @return ByteArrayInputStream
     */
    private ByteArrayInputStream firmwareToByteArray(final String writFileToTempFile) {
        final Path path = Path.of(writFileToTempFile);
        try (final var bin = new BufferedInputStream(Files.newInputStream(path));
             final var baos = new FastByteArrayOutputStream()) {
            //transfer
            bin.transferTo(baos);
            return new ByteArrayInputStream(baos.toByteArray());
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
        return null;
    }

}
