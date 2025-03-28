package com.esp.espflow.views.hexdump;

import com.esp.espflow.entity.dto.HexDumpDTO;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.textfield.TextField;
import org.apache.commons.lang3.StringUtils;

public class HexDumpFilter {

    private final GridListDataView<HexDumpDTO> dataView;
    private final TextField searchTextField;
    private final ComboBox<String> filterComboBox;

    public HexDumpFilter(final GridListDataView<HexDumpDTO> dataView,
                         final TextField searchTextField,
                         final ComboBox<String> filterComboBox) {
        this.dataView = dataView;
        this.searchTextField = searchTextField;
        this.filterComboBox = filterComboBox;
        this.dataView.addFilter(this::filter);
    }

    public boolean filter(HexDumpDTO hexDumpDTO) {
        final String value = searchTextField.getValue().trim();
        final String valueFromCombo = StringUtils.defaultIfEmpty(filterComboBox.getValue(), "").trim();
        if (value.isEmpty() && valueFromCombo.isEmpty()) {
            return true;
        }
        String srt = valueFromCombo.contains("Ascii")
                ? hexDumpDTO.getAscii()
                : hexDumpDTO.getOffset();
        return StringUtils.containsIgnoreCase(srt, value);
    }

}