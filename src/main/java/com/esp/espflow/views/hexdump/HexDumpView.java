package com.esp.espflow.views.hexdump;

import com.esp.espflow.entity.dto.HexDumpDTO;
import com.esp.espflow.entity.event.EspflowMessageListItemEvent;
import com.esp.espflow.service.HexDumpService;
import com.esp.espflow.util.CreateCustomDirectory;
import com.esp.espflow.views.MainLayout;
import com.infraleap.animatecss.Animated;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.UploadI18N;
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import reactor.core.publisher.Sinks;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static com.esp.espflow.util.EspFlowConstants.BOX_SHADOW_VAADIN_BUTTON;
import static com.esp.espflow.util.EspFlowConstants.ESPFLOW_DIR;
import static com.esp.espflow.util.EspFlowConstants.FLASH_HEX_DUMP_ANALIZE;
import static com.esp.espflow.util.EspFlowConstants.JAVA_IO_USER_HOME_DIR_OS;
import static com.esp.espflow.util.EspFlowConstants.TABLE_SVG;

@Log4j2
@UIScope
@SpringComponent
@PageTitle("HexDump")
@Route(value = "hex-dump-viewer", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@RequiredArgsConstructor
@PreserveOnRefresh
public class HexDumpView extends VerticalLayout implements CreateCustomDirectory {

    private final HexDumpService hexDumpService;
    private final Sinks.Many<EspflowMessageListItemEvent> publisher;

    private final Upload upload = new Upload();
    private final FileBuffer buffer = new FileBuffer();
    private final Grid<HexDumpDTO> grid = new Grid<>();

    @PostConstruct
    private void init() {
        super.setSizeFull();
        this.initListeners();
        this.configureGrid();
        super.add(upload, grid);
        Animated.animate(this, Animated.Animation.FADE_IN);
    }

    private void initListeners() {
        this.upload.addSucceededListener(event -> {
            this.upload.clearFileList();
            final String initCustomFileName = JAVA_IO_USER_HOME_DIR_OS.concat(ESPFLOW_DIR).concat(FLASH_HEX_DUMP_ANALIZE).concat(event.getFileName());
            log.info("addSucceededListener flash-hex-dump-analize/ {}", initCustomFileName);
            this.createCustomDirectory(buffer, JAVA_IO_USER_HOME_DIR_OS.concat(ESPFLOW_DIR).concat(FLASH_HEX_DUMP_ANALIZE), event.getFileName());
            byte[] fileBytes = new byte[0];
            try {
                fileBytes = Files.readAllBytes(Path.of(initCustomFileName));
            } catch (IOException e) {
                log.error("readAllBytes failed ");
            }
            final List<HexDumpDTO> hexLines = this.hexDumpService.generateHexDump(fileBytes);
            grid.setItems(hexLines);
            this.publisher.tryEmitNext(new EspflowMessageListItemEvent("Loaded .bin successfully", "Hex dump viewer",TABLE_SVG));
        });

        upload.setDropAllowed(true);
        upload.setMaxFiles(1);
        upload.setReceiver(buffer);
        upload.setAcceptedFileTypes(MediaType.APPLICATION_OCTET_STREAM_VALUE, ".bin");
        Tooltip.forComponent(upload).setText("Drop executable here!");
        this.i18N(upload);
    }

    private void configureGrid() {
        this.grid.setWidthFull();
        this.grid.setColumnReorderingAllowed(true);
        this.grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        this.grid.setEmptyStateText("No .bin has been loaded.");
        this.grid.getColumns().forEach(e -> e.setResizable(Boolean.TRUE));
        this.grid.addColumn(HexDumpDTO::getOffset)
                .setHeader("Offset");
        for (int i = 0; i < 16; i++) {
            final int index = i;
            String header = String.format("%X", i); // "0", "1", "2", ..., "E", "F"
            this.grid.addColumn(dto -> dto.getHexBytes()[index])
                    .setHeader(header)
                    .setWidth("40px");
        }

        this.grid.addColumn(HexDumpDTO::getAscii)
                .setHeader("ASCII");

        super.add(grid);

    }


    /**
     * @param upload to configure
     */
    private void i18N(final Upload upload) {
        final UploadExamplesI18N uploadI18N = new UploadExamplesI18N();
        uploadI18N.getAddFiles().setOne("Select .bin file...");
        uploadI18N.getError().setIncorrectFileType("The provided file doesn't have the correct format. Please provide a [exe,py] file.");
        upload.setI18n(uploadI18N);
        upload.getUploadButton().addClassName(BOX_SHADOW_VAADIN_BUTTON);
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

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        if (attachEvent.isInitialAttach()) {

        }
    }
}
