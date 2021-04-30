package com.tcg.rpgengine.editor.components;

import com.tcg.rpgengine.common.data.maps.MapEntity;
import com.tcg.rpgengine.common.data.maps.MapLayer;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;

import java.util.Objects;

public class MapLayerListView extends ListView<Integer> {

    private MapEntity map;

    public MapLayerListView() {
        super();
        this.setCellFactory(param -> new MapLayerListCell());
        this.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    public void setMap(MapEntity map) {
        this.getItems().clear();
        this.map = Objects.requireNonNull(map);
        final int numLayers = map.getLayers().size();
        for (int i = numLayers - 1; i >= 0; i--) {
            this.getItems().add(i);
        }
        this.getSelectionModel().select(this.getItems().size() - 1);
    }

    private class MapLayerListCell extends ListCell<Integer> {
        @Override
        protected void updateItem(Integer item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null && MapLayerListView.this.map != null) {
                final MapLayer mapLayer = MapLayerListView.this.map.getLayers().get(item);
                this.setGraphic(new Label(mapLayer.getName()));
            } else {
                this.setGraphic(null);
            }
        }
    }

}
