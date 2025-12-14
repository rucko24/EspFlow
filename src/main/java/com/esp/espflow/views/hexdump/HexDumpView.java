package com.esp.espflow.views.hexdump;

import com.esp.espflow.dto.HexDumpDto;
import com.esp.espflow.event.EspflowMessageListItemEvent;
import com.esp.espflow.service.HexDumpGeneratorService;
import com.esp.espflow.service.respository.impl.HexDumpService;
import com.esp.espflow.util.ConfirmDialogBuilder;
import com.esp.espflow.util.FileUploadHandler;
import com.esp.espflow.util.svgfactory.SvgFactory;
import com.esp.espflow.views.MainLayout;
import com.infraleap.animatecss.Animated;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.ColumnRendering;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.contextmenu.GridMenuItem;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.popover.Popover;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.UploadI18N;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.vaadin.lineawesome.LineAwesomeIcon;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static com.esp.espflow.util.EspFlowConstants.BOX_SHADOW_VAADIN_BUTTON;
import static com.esp.espflow.util.EspFlowConstants.CONTEXT_MENU_ITEM_GRID;
import static com.esp.espflow.util.EspFlowConstants.CONTEXT_MENU_ITEM_NO_CHECKMARK;
import static com.esp.espflow.util.EspFlowConstants.COPY_ALT_SVG;
import static com.esp.espflow.util.EspFlowConstants.COPY_TO_CLIPBOARD;
import static com.esp.espflow.util.EspFlowConstants.CURSOR_POINTER;
import static com.esp.espflow.util.EspFlowConstants.ESPFLOW_DIR;
import static com.esp.espflow.util.EspFlowConstants.FLASH_HEX_DUMP_ANALIZE;
import static com.esp.espflow.util.EspFlowConstants.JAVA_IO_USER_HOME_DIR_OS;
import static com.esp.espflow.util.EspFlowConstants.SIZE_30_PX;
import static com.esp.espflow.util.EspFlowConstants.TABLE_SVG;
import static com.esp.espflow.util.EspFlowConstants.WINDOW_COPY_TO_CLIPBOARD;

/**
 * @author rubn
 */
@Log4j2
@UIScope
@SpringComponent
@PageTitle("HexDump")
@Route(value = "hex-dump-viewer", layout = MainLayout.class)
@JsModule(COPY_TO_CLIPBOARD)
@RolesAllowed("ADMIN")
@RequiredArgsConstructor
public class HexDumpView extends VerticalLayout implements BeforeEnterObserver {

    private static final int DEBOUNCE_MS = 500;
    private static final int DEFAULT_GRID_PAGE_SIZE = 15;
    private static final String INDEX_PARAM_NAME = "i";

    private final HexDumpGeneratorService hexDumpGeneratorService;
    private final HexDumpService hexDumpService;
    private final Sinks.Many<EspflowMessageListItemEvent> publishEspflowMessageListItemEvent;
    private final Upload upload = new Upload();

    private final PagingGridv2<HexDumpDto> grid = new PagingGridv2<>();
    /**
     * Only to use the icon, when the Grid is empty.
     */
    private GridListDataView<HexDumpDto> gridListDataView;
    private ProgressBar progressBarHexDump;
    private ProgressBar deleteItemsGridProgressBar;
    private final Button buttonClearGrid = new Button(VaadinIcon.TRASH.create());

    @PostConstruct
    public void postConstruct() {
        super.setSizeFull();

        final Component headerRow = this.buildHeaderRow();
        final Component gridComponent = this.buildGrid();

        super.add(headerRow, progressBarHexDump, gridComponent);
        Animated.animate(this, Animated.Animation.FADE_IN);

        this.addPaginationOnGrid(StringUtils.EMPTY);

    }

    private HorizontalLayout buildHeaderRow() {
        this.progressBarHexDump = this.buildProgressBar("Generating hexdump please wait...");
        this.progressBarHexDump.setWidthFull();
        final FileUploadHandler fileUploadHandler = this.buildFileUploadHandler();
        this.upload.setUploadHandler(fileUploadHandler);

        upload.setDropAllowed(true);
        upload.setMaxFiles(1);
        upload.setAcceptedFileTypes(MediaType.APPLICATION_OCTET_STREAM_VALUE, ".bin");
        Tooltip.forComponent(upload).setText("Drop .bin here!");
        this.i18N(upload);

        final HorizontalLayout filterRow = this.buildFilterRow();
        final HorizontalLayout row = new HorizontalLayout(upload, filterRow);
        row.addClassNames("row-header-hexdump");
        row.setWidthFull();
        row.setJustifyContentMode(JustifyContentMode.BETWEEN);
        return row;
    }

    private FileUploadHandler buildFileUploadHandler() {
        final String fixedDir = JAVA_IO_USER_HOME_DIR_OS.concat(ESPFLOW_DIR).concat(FLASH_HEX_DUMP_ANALIZE);
        return new FileUploadHandler(fixedDir, this.upload)
                .whenStart((transferContext) -> {
                    this.progressBarHexDump.setVisible(true);
                    final String initCustomFileName = fixedDir.concat(transferContext.fileName());
                    log.info("Upload started flash-hex-dump-analize/ {}", initCustomFileName);
                })
                .whenComplete((transferContext, success) -> {
                    if (success) {
                        try {
                            final String initCustomFileName = fixedDir.concat(transferContext.fileName());
                            byte[] fileBytes = Files.readAllBytes(Path.of(initCustomFileName));
                            this.hexDumpGeneratorService.generateHexDump(fileBytes);
                            log.info("generate hexdump file completed successfully");
                            //The grid will be filled based on the predefined page layout.
                            this.addPaginationOnGrid(StringUtils.EMPTY);
                            this.publishEspflowMessageListItemEvent.tryEmitNext(new EspflowMessageListItemEvent("Loaded .bin successfully", "Hex dump viewer", TABLE_SVG));
                            this.progressBarHexDump.setVisible(false);
                            ConfirmDialogBuilder.showInformationUI("Generated hexdump completed.", transferContext.getUI());
                        } catch (IOException e) {
                            log.error("whenComplete failed {}", e.getMessage());
                        }
                    } else {
                        log.error("Upload failed");
                    }
                });
    }

    private HorizontalLayout buildFilterRow() {
        final HorizontalLayout row = new HorizontalLayout();
        buttonClearGrid.addClassName(BOX_SHADOW_VAADIN_BUTTON);
        buttonClearGrid.setTooltipText("Clear grid");
        buttonClearGrid.addThemeVariants(ButtonVariant.LUMO_ERROR);
        this.deleteItemsGridProgressBar = this.buildProgressBar("Deleting in progress...");
        this.buttonClearGrid.addClickListener(event -> {
            if (event.isFromClient() && this.gridListDataView.getItems().findAny().isPresent()) {
                getUI().ifPresent(ui -> {
                    ConfirmDialog confirmDialog = ConfirmDialogBuilder.showConfirmInformation("You want to delete this hexdump ?", ui);
                    confirmDialog.addConfirmListener(e -> {
                        this.buttonClearGrid.setVisible(false);
                        this.deleteItemsGridProgressBar.setVisible(true);
                        Mono.fromRunnable(this::clearGridAndRecords)
                                .subscribeOn(Schedulers.boundedElastic())
                                .delaySubscription(Duration.ofMillis(400))
                                .doOnTerminate(this::afterTerminate)
                                .subscribe();
                    });
                });
            } else {
                ConfirmDialogBuilder.showInformation("Empty grid, load a .bin first");
            }
        });
        final TextField searchTextField = this.buildSearchTextField();
        row.add(deleteItemsGridProgressBar, buttonClearGrid, searchTextField);
        row.setAlignSelf(Alignment.CENTER, searchTextField, buttonClearGrid, deleteItemsGridProgressBar);
        return row;
    }

    private void clearGridAndRecords() {
        getUI().ifPresent(ui -> {
            ui.access(() -> {
                this.hexDumpService.deleteAll();
                this.grid.setItems(List.of());
                this.upload.clearFileList();
            });
        });
    }

    private void afterTerminate() {
        getUI().ifPresent(ui -> ui.access(() -> {
            this.deleteItemsGridProgressBar.setVisible(false);
            this.buttonClearGrid.setVisible(true);
        }));
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

    private TextField buildSearchTextField() {
        final TextField searchTextField = new TextField();
        searchTextField.setWidthFull();
        SvgIcon icon = LineAwesomeIcon.SLIDERS_H_SOLID.create();
        icon.setTooltipText("Advanced search");
        icon.getStyle().setCursor(CURSOR_POINTER);
        searchTextField.setSuffixComponent(icon);

        final Popover popover = new Popover();
        popover.setModal(true);
        popover.setTarget(icon);
        final IntegerField setRowNumbersField = new IntegerField();
        final VerticalLayout verticalLayoutPopOver = new VerticalLayout(setRowNumbersField);
        popover.add(verticalLayoutPopOver);

        searchTextField.setPlaceholder("Search");
        searchTextField.setTooltipText("Filter by: offset or ascii/text");
        searchTextField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchTextField.setValueChangeMode(ValueChangeMode.EAGER);
        searchTextField.setClearButtonVisible(true);
        searchTextField.getStyle().set("max-width", "100%");
        searchTextField.addValueChangeListener(valueChangeEvent -> this.addPaginationOnGrid(valueChangeEvent.getValue()));

        setRowNumbersField.setLabel("Row numbers per page");
        setRowNumbersField.setStepButtonsVisible(true);
        setRowNumbersField.setTooltipText("Row numbers per page");
        setRowNumbersField.setClearButtonVisible(true);
        setRowNumbersField.setValue(DEFAULT_GRID_PAGE_SIZE);
        searchTextField.setValueChangeMode(ValueChangeMode.EAGER);
        setRowNumbersField.addValueChangeListener(event -> {
            if (event.isFromClient() && event.getValue() != null && gridListDataView.getItems().findAny().isPresent()) {
                final Integer reconfigureNumberOfRecords = event.getValue();
                this.grid.setPageSize(reconfigureNumberOfRecords);
                this.addPaginationOnGrid(StringUtils.EMPTY);
            }
        });
        return searchTextField;
    }

    private Component buildGrid() {

        this.grid.addColumn(HexDumpDto::getOffset)
                .setKey("offset")
                .setHeader("Offset");

        for (int i = 0; i < 16; i++) {
            final int index = i;
            String header = String.format("%X", i); // "0", "1", "2", ..., "E", "F"
            this.grid.addColumn(dto -> dto.getHexBytes()[index])
                    .setHeader(header)
                    .setWidth("40px");
        }

        this.grid.addColumn(HexDumpDto::getAscii)
                .setKey("ascii")
                .setAutoWidth(true)
                .setHeader("Ascii / Text");

        this.grid.setSizeFull();
        this.grid.setAllRowsVisible(false);
        this.grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        this.grid.setColumnRendering(ColumnRendering.LAZY);
        this.grid.setColumnReorderingAllowed(true);
        this.grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        this.grid.setPageSize(DEFAULT_GRID_PAGE_SIZE);
        this.gridListDataView = this.grid.setItems(List.of());
        this.grid.getColumns().forEach(e -> e.setResizable(Boolean.TRUE));
        this.grid.setPaginationBarMode(PagingGridv2.PaginationBarMode.BOTTOM);

        var tableIcon = SvgFactory.createIconFromSvg("table.svg", SIZE_30_PX, null);
        tableIcon.getStyle().setMarginRight("10px");
        final Div warningEmptyGrid = new Div(tableIcon, new Text("Empty grid!"));
        warningEmptyGrid.addClassNames("warning-empty-grid");
        this.grid.setEmptyStateComponent(warningEmptyGrid);

        this.buildGridContextMenu(warningEmptyGrid);

        return grid;
    }

    private void buildGridContextMenu(Div warning) {
        final GridContextMenu<HexDumpDto> contextMenu = grid.addContextMenu();
        final GridMenuItem<HexDumpDto> gridContextMeuOffset = contextMenu.addItem("Copy Offset");
        final GridMenuItem<HexDumpDto> gridContextMenuAscii = contextMenu.addItem("Copy Ascii text");
        final GridMenuItem<HexDumpDto> gridContextMenuHex = contextMenu.addItem("Copy Hex columns");
        final GridMenuItem<HexDumpDto> gridContextMenuRow = contextMenu.addItem("Copy entire row");

        /*Only enable the context menu when there are records*/
        this.gridListDataView.addItemCountChangeListener(itemCountChangeEvent -> {
            contextMenu.setEnabled(itemCountChangeEvent.getItemCount() != 0);
            if (itemCountChangeEvent.getItemCount() == 0) {
                warning.removeClassName("hidden");
            } else {
                warning.addClassName("hidden");
            }
        });

        gridContextMeuOffset.addComponentAsFirst(SvgFactory.createIconFromSvg(COPY_ALT_SVG, SIZE_30_PX, null));
        gridContextMeuOffset.addClassName(CONTEXT_MENU_ITEM_GRID);
        gridContextMeuOffset.addClassName(CONTEXT_MENU_ITEM_NO_CHECKMARK);
        gridContextMeuOffset.addMenuItemClickListener(event -> {
            event.getItem().ifPresent(userCookieDto -> {
                UI.getCurrent().getElement().executeJs(WINDOW_COPY_TO_CLIPBOARD, userCookieDto.getOffset());
                Notification.show("Copied Offset! " + userCookieDto.getOffset(), 2000, Notification.Position.MIDDLE);
            });
        });

        gridContextMenuAscii.addComponentAsFirst(SvgFactory.createIconFromSvg(COPY_ALT_SVG, SIZE_30_PX, null));
        gridContextMenuAscii.addClassName(CONTEXT_MENU_ITEM_GRID);
        gridContextMenuAscii.addMenuItemClickListener(event -> {
            event.getItem().ifPresent(userCookieDto -> {
                UI.getCurrent().getElement().executeJs(WINDOW_COPY_TO_CLIPBOARD, userCookieDto.getAscii());
                Notification.show("Copied Ascii text! " + userCookieDto.getAscii(), 2000, Notification.Position.MIDDLE);
            });
        });

        gridContextMenuHex.addComponentAsFirst(SvgFactory.createIconFromSvg(COPY_ALT_SVG, SIZE_30_PX, null));
        gridContextMenuHex.addClassName(CONTEXT_MENU_ITEM_GRID);
        gridContextMenuHex.addMenuItemClickListener(event -> {
            event.getItem().ifPresent(userCookieDto -> {
                final String hex = Arrays.toString(userCookieDto.getHexBytes());
                UI.getCurrent().getElement().executeJs(WINDOW_COPY_TO_CLIPBOARD, hex);
                Notification.show("Copied HEX columns! " + hex, 2000, Notification.Position.MIDDLE);

            });
        });

        gridContextMenuRow.addComponentAsFirst(SvgFactory.createIconFromSvg(COPY_ALT_SVG, SIZE_30_PX, null));
        gridContextMenuRow.addClassName(CONTEXT_MENU_ITEM_GRID);
        gridContextMenuRow.addMenuItemClickListener(event -> {
            event.getItem().ifPresent(userCookieDto -> {
                final String row = userCookieDto.getOffset() + " " + Arrays.toString(userCookieDto.getHexBytes()) + " " + userCookieDto.getAscii();
                UI.getCurrent().getElement().executeJs(WINDOW_COPY_TO_CLIPBOARD, row);
                Notification.show("Copied row! " + row, 2000, Notification.Position.MIDDLE);

            });
        });
    }

    private void addPaginationOnGrid(String filterText) {

        this.grid.setPagingDataProvider((page, pageSize) -> {
            int start = (int) (page * this.grid.getPageSize());
            return this.hexDumpService.findByFilterText(filterText, PageRequest.of(start, pageSize));
        });

    }

    /**
     * Class for traslation
     */
    private static class UploadExamplesI18N extends UploadI18N {
        public UploadExamplesI18N() {
            setDropFiles(new DropFiles()
                    .setOne("Drop .bin here")
                    .setMany("Drop .bin's here"));
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

    @SuppressWarnings("unused")
    private void registerScrollEventListener() {
        // grid._firstVisibleIndex is not public api of the vaadin grid. I hope this will not be
        // removed.
        grid.getElement().executeJs(
                "this.$.table.addEventListener('scroll', (e) => {\n" +
                        "clearTimeout($0.__scroll_position_timeout);\n" +
                        "$0.__scroll_position_timeout = setTimeout(() => $0.$server.receiveScrollPosition(this._firstVisibleIndex), " +
                        DEBOUNCE_MS +
                        ");\n" +
                        "})",
                this.getElement() // or wherever receiveScrollPosition(index) is implemented in the
                // backend
        );
    }

    private ProgressBar buildProgressBar(String text) {
        final ProgressBar progressBar = new ProgressBar();
        progressBar.setWidth("50px");
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);
        Tooltip.forComponent(progressBar).setText(text);
        return progressBar;
    }

    @ClientCallable
    @SuppressWarnings("unused")
    public void receiveScrollPosition(int index) {
        getUI().ifPresent(ui -> {
            ui.getPage().fetchCurrentURL(url -> {
                ui.getPage()
                        .getHistory()
                        .replaceState(null, url.getPath() + "?" + INDEX_PARAM_NAME + "=" + index);

            });
        });
    }

    @Override
    @SuppressWarnings("unused")
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        final List<String> index = beforeEnterEvent.getLocation().getQueryParameters().getParameters().get(INDEX_PARAM_NAME);
        if (index != null && index.size() > 0) {
            grid.scrollToIndex(Integer.parseInt(index.get(0)));
        }
    }

}
