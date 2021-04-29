package com.tcg.rpgengine.editor.components;

import com.tcg.rpgengine.common.data.Entity;
import com.tcg.rpgengine.common.data.assets.Asset;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;

import java.util.Optional;
import java.util.function.Function;

public class SimpleEntityListView<T extends Entity> extends ListView<T> {

    public SimpleEntityListView(Function<T, String> nameFunction) {
        super();
        this.setCellFactory(param -> new SimpleAssetCell(nameFunction));
        this.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    private class SimpleAssetCell extends ListCell<T> {

        private final Function<T, String> nameFunction;

        private SimpleAssetCell(Function<T, String> nameFunction) {
            this.nameFunction = nameFunction;
        }

        @Override
        protected void updateItem(T item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                this.setGraphic(new Label(this.nameFunction.apply(item)));
            } else {
                this.setGraphic(null);
            }
        }
    }

}
