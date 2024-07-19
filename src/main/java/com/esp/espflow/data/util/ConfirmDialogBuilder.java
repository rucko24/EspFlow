package com.esp.espflow.data.util;

import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import lombok.Getter;
import lombok.Value;
import lombok.extern.log4j.Log4j2;

import java.util.Objects;

import static com.esp.espflow.data.util.EspFlowConstants.OK;

/**
 * ConfirmDialogBuilder
 */
@Log4j2
@Value
@Uses(ConfirmDialog.class)
public class ConfirmDialogBuilder {

    public static ConfirmDialog showWarning(String text) {
        return ConfirmDialogBuilder.builder()
                .withHeaderIconAndHeaderText(VaadinIcon.WARNING, EspFlowConstants.WARNING)
                .withText(text)
                .make();
    }

    public static ConfirmDialog showInformation(final String text) {
        return ConfirmDialogBuilder.builder()
                .withHeaderIconAndHeaderText(VaadinIcon.INFO, EspFlowConstants.INFORMATION)
                .withText(text)
                .make();
    }

    private static WithIconAndHeaderText builder() {
        return new InnerBuilder();
    }

    /**
     * 1
     */
    public interface WithIconAndHeaderText {
        Text withHeaderIconAndHeaderText(final VaadinIcon icon, final String textHeader);
    }

    /**
     * 2
     */
    public interface Text {
        Build withText(final String text);
    }

    /**
     * 3
     */
    public interface Build extends IBuilder<ConfirmDialog> {

    }

    /**
     * The magic builder
     */
    @Getter
    public static class InnerBuilder implements Text, WithIconAndHeaderText, Build {

        private VaadinIcon icon;
        private String text;
        private String headerText;
        private final ConfirmDialog confirmDialog = new ConfirmDialog();

        @Override
        public Build withText(String text) {
            Objects.requireNonNull(text, "Texto no debe ser null");
            this.text = text;
            return this;
        }

        @Override
        public Text withHeaderIconAndHeaderText(VaadinIcon icon, String headerText) {
            Objects.requireNonNull(icon, "Icon must not be null");
            Objects.requireNonNull(headerText, "Header text must not be null");
            this.icon = icon;
            this.headerText = headerText;
            return this;
        }

        @Override
        public ConfirmDialog make() {
            final HorizontalLayout headerRow = new HorizontalLayout(icon.create(), new H3(headerText));
            headerRow.setAlignItems(Alignment.CENTER);
            confirmDialog.setHeader(headerRow);
            confirmDialog.setConfirmText(OK);
            confirmDialog.setText(text);
            confirmDialog.open();
            return confirmDialog;
        }

    }
}

