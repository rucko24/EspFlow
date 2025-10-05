package com.esp.espflow.service.downloader;

import com.esp.espflow.util.EspFlowConstants;
import com.infraleap.animatecss.Animated;
import com.infraleap.animatecss.Animated.Animation;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.server.streams.DownloadHandler;
import com.vaadin.flow.server.streams.DownloadResponse;
import com.vaadin.flow.server.streams.InputStreamDownloadHandler;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;

import java.io.BufferedInputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * <p>This class allows to generate an anchor to download the microcontroller firmware from a location in the temporary directory of the OS.  </p>
 *
 * <p>Once it is instantiated, the method <strong>enableAnchorForDownloadTheFirmware</strong> must be invoked and pass as parameter the name of the file that was generated in the temp directory, so that the browser knows where it is located. </p>
 *
 * @author rubn
 */
@Log4j2
public class FlashDownloadButtonWrapper {

    @Getter
    private final Anchor anchorDownload = new Anchor();
    private final Button download = new Button(VaadinIcon.DOWNLOAD.create());

    public FlashDownloadButtonWrapper() {
        anchorDownload.setVisible(false);
        download.setVisible(false);
    }

    /**
     * This method enables the anchor, and each time it is invoked a name is set by the Content-Disposition
     * allowing to determine the name of the file in the browser and to download it.
     *
     * @param writFileToTempDir the name of the file located in the temporary OS directory
     */
    public void enableAnchorForDownloadTheFirmware(final String writFileToTempDir) {
        this.download.setVisible(true);
        this.download.getStyle().setMarginRight("var(--lumo-space-m");
        this.anchorDownload.setVisible(true);
        this.download.setTooltipText("Download flash backup");
        this.download.addClassName(EspFlowConstants.BOX_SHADOW_VAADIN_BUTTON);
        Animated.animate(download, Animation.FADE_IN);
        this.anchorDownload.setHref(this.createInputStreamDownloadHandler(writFileToTempDir));
        this.anchorDownload.add(download);
    }

    /**
     * Convert the firmware to byte array
     *
     * @param writFileToTempFile the name of the file located in the temporary OS directory
     *
     * @return A {@link InputStreamDownloadHandler} with firmware read from tmp
     */
    private InputStreamDownloadHandler createInputStreamDownloadHandler(final String writFileToTempFile) {
        final Path path = Path.of(writFileToTempFile);
        path.toFile().deleteOnExit();
        return DownloadHandler.fromInputStream(event -> {
                    event.getResponse().setHeader("Content-Disposition", "attachment; filename=\"" + writFileToTempFile + "\"");
                    return new DownloadResponse(new BufferedInputStream(Files.newInputStream(path)),
                            Path.of(writFileToTempFile).getFileName().toString(),
                            MediaType.APPLICATION_OCTET_STREAM_VALUE,
                            Files.size(path));
                });
    }

}
