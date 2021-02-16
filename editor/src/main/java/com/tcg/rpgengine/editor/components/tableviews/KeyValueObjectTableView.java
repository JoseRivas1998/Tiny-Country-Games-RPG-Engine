package com.tcg.rpgengine.editor.components.tableviews;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public abstract class KeyValueObjectTableView<T> extends TableView<T> {

    public KeyValueObjectTableView() {
        super();
        this.setEditable(false);
        final TableColumn<T, String> variableNameColumn = new TableColumn<>(this.getKeyLabel());
        variableNameColumn.setCellValueFactory(param -> {
            if (param.getValue() != null) {
                return new SimpleStringProperty(this.getKey(param.getValue()));
            }
            return null;
        });
        variableNameColumn.setSortable(this.keyColumnIsSortable());
        this.getColumns().add(variableNameColumn);

        final TableColumn<T, String> valueColumn = new TableColumn<>(this.getValueLabel());
        valueColumn.setCellValueFactory(param -> {
            if (param.getValue() != null) {
                return new SimpleStringProperty(this.getValue(param.getValue()));
            }
            return null;
        });
        valueColumn.setSortable(this.isValueColumnSortable());
        this.getColumns().add(valueColumn);
    }

    protected boolean isValueColumnSortable() {
        return false;
    }

    protected boolean keyColumnIsSortable() {
        return true;
    }

    protected abstract String getKeyLabel();
    protected abstract String getValueLabel();

    protected abstract String getKey(T rowValue);
    protected abstract String getValue(T rowValue);

}
