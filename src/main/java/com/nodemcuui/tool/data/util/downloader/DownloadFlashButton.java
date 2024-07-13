package com.nodemcuui.tool.data.util.downloader;

import com.nodemcuui.tool.data.util.GetOsName;
import com.nodemcuui.tool.data.util.ProcessCommandsInternals;
import com.nodemcuui.tool.data.util.console.ConsoleOutPut;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.server.StreamResource;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.util.FileCopyUtils;
import org.vaadin.olli.FileDownloadWrapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.nodemcuui.tool.data.util.UiToolConstants.CHARSET_CP850;

/**
 * https://stackoverflow.com/questions/61513997/how-to-download-a-pdf-from-vaadin-flow-app
 * <p>
 * Escribir la flash en el disco primero luego generar un anchor o un FileDownloadWrapper ? o un anchor normal ?
 * Recuperar de la ruta del Vaadin Servlet para luego guardarlo en otro lugar
 * luego borrarlo del Vaadin Servlet
 */
@Log4j2
public class DownloadFlashButton extends FileDownloadWrapper {

    private ProcessCommandsInternals processCommandsInternals = new ProcessCommandsInternals();
    private final Button download = new Button(VaadinIcon.DOWNLOAD.create());
    private String fileName;

    private ConsoleOutPut consoleOutPut;
    private UI ui;

    public DownloadFlashButton(ConsoleOutPut consoleOutPut, UI ui) {
        super();
        setSizeFull();
        this.consoleOutPut = consoleOutPut;
        this.ui = ui;
    }


    public DownloadFlashButton() {
        super();
        setSizeFull();
    }

    public Button getDownloadButton() {
        return download;
    }

    /**
     * Al invocar este metodo desde otro listener de un boton, solo se habilita el anchor
     * la propia descarga empieza cuando se le da click al anchor.
     *
     * @param fileName el nombre final del fichero
     */
    public void enableAnchorForDownloadTheFirmware(final String fileName) {
        this.fileName = fileName;
        final var streamResource = new StreamResource(fileName, this::firmwareToByteArray);
        streamResource.setCacheTime(0);
        super.setResource(streamResource);
        super.wrapComponent(download);
        download.getStyle().set("box-shadow", "0 2px 1px -1px rgba(0, 0, 0, .2), 0 1px 1px 0 rgba(0, 0, 0, .14), 0 1px 3px 0 rgba(0, 0, 0, .12)");

    }

    /**
     * https://github.com/newUserRepo/testbar/blob/issueBar/vaadin-upload/src/main/java/com/example/vaadinupload/ProcessingService.java#L49C17-L49C46
     * <p>
     * Posiblemente mejor guardar en el direcotiro temporal y listo
     * Bytearray con buffer para no cargar todo en memoria
     *
     * @return ByteArrayInputStream
     */
    private ByteArrayInputStream firmwareToByteArray() {
        final Path path = Path.of(fileName);
        try (final var bin = new BufferedInputStream(Files.newInputStream(path));
             final var baos = new FastByteArrayOutputStream()) {
            //transfer
            //Pasar listener del ConsoleOutPut aqui?
            //https://github.com/newUserRepo/testbar/blob/issueBar/vaadin-upload/src/main/java/com/example/vaadinupload/ProcessingService.java#L49C17-L49C46
            bin.transferTo(baos);
            return new ByteArrayInputStream(baos.toByteArray());
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
        return null;
    }


    private void writeToXterm(FastByteArrayOutputStream baos) {
        Flux.defer(() -> DataBufferUtils.readInputStream(baos::getInputStream, DefaultDataBufferFactory.sharedInstance, FileCopyUtils.BUFFER_SIZE))
                .subscribeOn(Schedulers.boundedElastic())
                .map(this::mappingDataBuffer)
                .onErrorResume(throwable -> {
                    log.error("onErrorResume: {}", throwable);
                    return Mono.error(new RuntimeException("Error al procesar dentro del try"));
                })
                .distinct(this::splitPercentaje)
                .subscribe(line -> {
                    ui.access(() -> {
                        this.consoleOutPut.writeln(line);
                    });
                });
    }

    private String splitPercentaje(String input) {
        if (input.contains("\\((\\d{1,2}|100) %\\)")) {
            return input.split("\\((\\d{1,2}|100) %\\)")[0];
        }
        return input;
    }

    private String mappingDataBuffer(final DataBuffer dataBuffer) {
        final byte[] bytes = new byte[dataBuffer.readableByteCount()];
        dataBuffer.read(bytes);
        DataBufferUtils.release(dataBuffer);
        if (GetOsName.getOsName() == GetOsName.WINDOWS) {
            try {
                return new String(bytes, CHARSET_CP850);
            } catch (UnsupportedEncodingException e) {
                return StringUtils.EMPTY;
            }
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

}
