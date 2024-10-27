package com.esp.espflow.util.downloader;

import com.esp.espflow.util.EspFlowConstants;
import com.infraleap.animatecss.Animated;
import com.infraleap.animatecss.Animated.Animation;
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
 * <p>This class allows to generate an anchor to download the microcontroller firmware from a location in the temporary directory of the OS.  </p>
 *
 * <p>Once it is instantiated, the method <strong>enableAnchorForDownloadTheFirmware</strong> must be invoked and pass as parameter the name of the file that was generated in the temp directory, so that the browser knows where it is located. </p>
 *
 * @author rubn
 */
@Log4j2
public class FlashButtonWrapper extends FileDownloadWrapper {

    private final Button download = new Button(VaadinIcon.DOWNLOAD.create());

    public FlashButtonWrapper() {
        super();
    }

    /**
     * This method enables the anchor, and each time it is invoked a name is set by the Content-Disposition
     * allowing to determine the name of the file in the browser and to download it.
     *
     * @param writFileToTempDir the name of the file located in the temporary OS directory
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
        download.addClassName(EspFlowConstants.BOX_SHADOW_VAADIN_BUTTON);
        Animated.animate(download, Animation.FADE_IN);
    }

    /**
     *  Convert the firmware to byte array
     *
     * @param writFileToTempFile
     * @return A {@link ByteArrayInputStream} with firmware read from tmp
     */
    private ByteArrayInputStream firmwareToByteArray(final String writFileToTempFile) {
        final Path path = Path.of(writFileToTempFile);
        path.toFile().deleteOnExit();
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
