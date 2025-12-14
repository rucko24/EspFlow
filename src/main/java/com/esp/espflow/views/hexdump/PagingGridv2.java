package com.esp.espflow.views.hexdump;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.FooterRow;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.event.SortEvent;
import com.vaadin.flow.dom.Element;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.components.grid.VGrid;
import org.vaadin.firitin.components.orderedlayout.VHorizontalLayout;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.List;

/**
 * A Grid which uses "traditional" paging, instead of the lazy loading while
 * scrolling commonly used in Vaadin apps.
 *
 * @param <T> the the of row shown in the grid
 * @author mstahv
 */
public class PagingGridv2<T> extends VGrid<T> {

    private PagingGridv2<T>.PaginationBar secondaryBar;
    private FooterRow.FooterCell footerCell;
    private HeaderRow.HeaderCell headerCell;
    private MessageFormat statusMessage = new MessageFormat("Page {0}, showing {1} results per page.");
    private PagingGridv2.PaginationBarMode paginationBarMode;
    private PagingGridv2.PagingDataProvider<T> dataProvider;
    private PagingGridv2<T>.PaginationBar paginationBar;
    private ComponentEventListener<SortEvent<Grid<T>, GridSortOrder<T>>> sortListener;

    public PagingGridv2() {
        this.paginationBarMode = PagingGridv2.PaginationBarMode.TOP;
        this.sortListener = (event) -> {
            if (this.dataProvider != null) {
                this.setItems(this.dataProvider.pageRequested(this.paginationBar.currentPage, this.getPageSize()));
            }
        };
        this.init();
    }

    public PagingGridv2(Class<T> beanType) {
        super(beanType);
        this.paginationBarMode = PagingGridv2.PaginationBarMode.TOP;
        this.sortListener = (event) -> {
            if (this.dataProvider != null) {
                this.setItems(this.dataProvider.pageRequested(this.paginationBar.currentPage, this.getPageSize()));
            }
        };
        this.init();
    }

    public PagingGridv2(Class<T> beanType, boolean autoCreateColumns) {
        super(beanType, autoCreateColumns);
        this.paginationBarMode = PagingGridv2.PaginationBarMode.TOP;
        this.sortListener = (event) -> {
            if (this.dataProvider != null) {
                this.setItems(this.dataProvider.pageRequested(this.paginationBar.currentPage, this.getPageSize()));
            }
        };
        this.init();
    }

    public PagingGridv2(int pageSize) {
        super(pageSize);
        this.paginationBarMode = PagingGridv2.PaginationBarMode.TOP;
        this.sortListener = (event) -> {
            if (this.dataProvider != null) {
                this.setItems(this.dataProvider.pageRequested(this.paginationBar.currentPage, this.getPageSize()));
            }
        };
        this.init();
    }

    public void setPaginationBarMode(PagingGridv2.PaginationBarMode value) {
        if (this.paginationBarMode != value) {
            this.paginationBarMode = value;
            if (this.paginationBar != null) {
                this.preparePaginationBar();
            }
        }

    }

    private void init() {
        this.addSortListener(this.sortListener);
        this.setPageSize(10);
        this.setAllRowsVisible(true);
    }

    protected void preparePaginationBar() {
        if (this.paginationBar == null) {
            this.paginationBar = new PaginationBar((Long)null);
        }

        if(paginationBar.pages != null && (paginationBar.pages >= 1 && paginationBar.pages < paginationBar.size) ) {
            paginationBar = new PaginationBar(null);
        }

        if (!this.getColumns().isEmpty()) {
            if (this.paginationBarMode == PagingGridv2.PaginationBarMode.BOTH) {
                this.addToHeader();
                this.secondaryBar = new PaginationBar(this.paginationBar.size);
                this.secondaryBar.currentPage = this.paginationBar.currentPage;
                this.secondaryBar.updateState();
                this.addToFooter();
            } else {
                if (this.secondaryBar != null) {
                    try {
                        this.secondaryBar.removeFromParent();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    this.secondaryBar = null;
                }

                if (this.paginationBarMode == PagingGridv2.PaginationBarMode.TOP) {
                    this.addToHeader();
                } else if (this.paginationBarMode == PagingGridv2.PaginationBarMode.BOTTOM) {
                    this.addToFooter();
                }
            }

        }
    }

    protected void addToFooter() {
        PagingGridv2<T>.PaginationBar bar = this.secondaryBar == null ? this.paginationBar : this.secondaryBar;
        if (this.footerCell == null) {
            this.uglyHackToAllowJoiningFirstFooterRowCells();
            FooterRow footerRow = this.appendFooterRow();
            this.footerCell = (FooterRow.FooterCell)footerRow.join((Grid.Column[])this.getColumns().toArray(new Grid.Column[0]));
        }

        this.footerCell.setComponent(bar);
    }

    private void uglyHackToAllowJoiningFirstFooterRowCells() {
        if (this.getFooterRows().isEmpty()) {
            this.appendFooterRow();
            this.addClassName("paging-grid");
            Element style = new Element("style");
            style.setProperty("innerHTML", "vaadin-grid.paging-grid::part(first-footer-row-cell) {\n   display: none;\n}\n");
            UI.getCurrent().getElement().appendChild(new Element[]{style});
        }

    }

    protected void addToHeader() {
        if (this.headerCell == null) {
            HeaderRow headerRow = this.prependHeaderRow();
            this.headerCell = headerRow.join((Grid.Column[])this.getColumns().toArray(new Grid.Column[0]));
        }

        this.headerCell.setComponent(this.paginationBar);
    }

    public PagingGridv2.PagingDataProvider<T> getPagingDataProvider() {
        return this.dataProvider;
    }

    public void setPagingDataProvider(PagingGridv2.PagingDataProvider<T> provider) {
        this.dataProvider = provider;
        this.preparePaginationBar();
        this.setItems(this.dataProvider.pageRequested(0L, this.getPageSize()));
        if (this.paginationBar.currentPage != 0L) {
            this.paginationBar.currentPage = 0L;
            this.paginationBar.updateState();
            if (this.secondaryBar != null) {
                this.secondaryBar.currentPage = 0L;
                this.secondaryBar.updateState();
            }
        }

    }

    public void setTotalResults(long totalResults) {
        this.paginationBar.setSize(totalResults);
        this.paginationBar.updateState();
        if (this.secondaryBar != null) {
            this.secondaryBar.setSize(totalResults);
            this.secondaryBar.updateState();
        }

    }

    public void setPageSize(int pageSize) {
        super.setPageSize(pageSize);
        if (this.paginationBar != null) {
            this.paginationBar.fetchPage();
        }

    }

    public void setStatusMessage(MessageFormat statusMessage) {
        this.statusMessage = statusMessage;
        if (this.paginationBar != null) {
            this.paginationBar.updateState();
            if (this.secondaryBar != null) {
                this.secondaryBar.updateState();
            }
        }

    }

    public static enum PaginationBarMode {
        TOP,
        BOTTOM,
        BOTH;
    }

    class PaginationBar extends VHorizontalLayout {
        private static final long serialVersionUID = 7799263034212965499L;
        private final Span status = new Span();
        private Long size;
        private long currentPage;
        private Long pages;
        private Button first;
        private Button last;
        private Button next;
        private Button previous;
        private VHorizontalLayout pageBtns = new VHorizontalLayout();

        public PaginationBar(Long size) {
            this.setSize(size);
            this.initButtons();
            this.updateState();
            this.add(new Component[]{this.first, this.previous});
            ((VHorizontalLayout)this.space().withComponents(new Component[]{this.pageBtns, this.status})).space();
            this.add(new Component[]{this.next, this.last});
            this.alignAll(Alignment.CENTER);
            this.withFullWidth();
        }

        private void updateState() {
            boolean hasPrev = this.currentPage > 0L;
            this.first.setEnabled(hasPrev);
            this.previous.setEnabled(hasPrev);
            if (this.sizeKnown()) {
                boolean hasNext = this.currentPage < this.pages - 1L;
                this.last.setVisible(true);
                this.last.setEnabled(hasNext);
                this.next.setEnabled(hasNext);
                this.pageBtns.removeAll();
                long start;
                long end;
                if (this.pages < 10L) {
                    start = 0L;
                    end = this.pages;
                } else {
                    start = this.currentPage - 4L;
                    if (start < 0L) {
                        start = 0L;
                    }

                    end = start + 9L;
                    if (end > this.pages) {
                        end = this.pages;
                    }

                    if (end - start < 10L) {
                        start = end - 9L;
                    }
                }

                for(long i = start; i < end; ++i) {
                    long finalI = i;
                    VButton btn = (VButton)(new VButton("" + (i + 1L), (e) -> {
                        this.currentPage = finalI;
                        this.fetchPage();
                    })).withEnabled(this.currentPage != i);
                    btn.withThemeVariants(new ButtonVariant[]{ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_SMALL});
                    this.pageBtns.add(new Component[]{btn});
                }

                this.status.setVisible(false);
                this.pageBtns.setVisible(true);
            } else {
                this.last.setEnabled(false);
                this.next.setEnabled(true);
                this.status.setText(PagingGridv2.this.statusMessage.format(new Object[]{this.currentPage + 1L, PagingGridv2.this.getPageSize()}));
                this.pageBtns.setVisible(false);
            }

        }

        private void initButtons() {
            this.first = new VButton(VaadinIcon.FAST_BACKWARD.create(), (e) -> this.handleClick(e));
            this.last = new VButton(VaadinIcon.FAST_FORWARD.create(), (e) -> this.handleClick(e));
            this.next = new VButton(VaadinIcon.FORWARD.create(), (e) -> this.handleClick(e));
            this.previous = new VButton(VaadinIcon.BACKWARDS.create(), (e) -> this.handleClick(e));
        }

        private void handleClick(ClickEvent<Button> event) {
            if (event.getSource() == this.first) {
                this.currentPage = 0L;
            } else if (event.getSource() == this.last) {
                this.currentPage = this.pages - 1L;
            } else if (event.getSource() == this.next) {
                ++this.currentPage;
            } else if (event.getSource() == this.previous) {
                --this.currentPage;
            }

            this.fetchPage();
            if (PagingGridv2.this.secondaryBar != null) {
                if (this == PagingGridv2.this.paginationBar) {
                    PagingGridv2.this.secondaryBar.currentPage = this.currentPage;
                    PagingGridv2.this.secondaryBar.updateState();
                } else {
                    PagingGridv2.this.paginationBar.currentPage = this.currentPage;
                    PagingGridv2.this.paginationBar.updateState();
                }
            }

        }

        void fetchPage() {
            List<T> page = PagingGridv2.this.dataProvider.pageRequested(this.currentPage, PagingGridv2.this.getPageSize());
            if (page.size() > 0) {
                PagingGridv2.this.setItems(page);
                if (page.size() < PagingGridv2.this.getPageSize()) {
                    this.setSize((long)page.size() + this.currentPage * (long)PagingGridv2.this.getPageSize());
                }
            } else {
                this.setSize(this.currentPage * (long)PagingGridv2.this.getPageSize());
                if (this.currentPage > 0L) {
                    --this.currentPage;
                }
            }

            this.updateState();
        }

        public long getSize() {
            return this.size;
        }

        void setSize(Long s) {
            this.size = s;
            if (this.sizeKnown()) {
                this.pages = (long)Math.ceil((double)1.0F * (double)this.size / (double)PagingGridv2.this.getPageSize());
            }

        }

        private boolean sizeKnown() {
            return this.size != null;
        }
    }

    public interface PagingDataProvider<T> extends Serializable {
        List<T> pageRequested(long var1, int var3);
    }
}