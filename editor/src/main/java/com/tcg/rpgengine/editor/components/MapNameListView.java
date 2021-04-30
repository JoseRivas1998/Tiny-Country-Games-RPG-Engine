package com.tcg.rpgengine.editor.components;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;

import java.util.UUID;
import java.util.function.Function;

public class MapNameListView extends ListView<UUID> {

    public MapNameListView(Function<UUID, String> nameFunction) {
        super();
        this.setCellFactory(param -> new MapNameListCell(nameFunction));
        this.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    private static class MapNameListCell extends ListCell<UUID> {

        private final Function<UUID, String> nameFunction;

        private MapNameListCell(Function<UUID, String> nameFunction) {
            this.nameFunction = nameFunction;
        }

        @Override
        protected void updateItem(UUID item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                this.setGraphic(new Label(this.nameFunction.apply(item)));
            } else {
                this.setGraphic(null);
            }
        }
    }

}
