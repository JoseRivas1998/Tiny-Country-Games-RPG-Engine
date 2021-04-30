package com.tcg.rpgengine.editor.components;

import com.tcg.rpgengine.common.data.maps.MapEntity;
import com.tcg.rpgengine.editor.utils.MapEditor;
import javafx.geometry.Side;
import javafx.scene.control.TabPane;

import java.util.List;
import java.util.UUID;

public class TilesetTabPane extends TabPane {

    final MapEditor editor;

    public TilesetTabPane(MapEditor editor) {
        super();
        this.editor = editor;

        this.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue instanceof TilesetTab) {
                final TilesetTab tab = (TilesetTab) newValue;
                tab.resetSelection();
            }
        });

    }

    public void setMap(MapEntity map) {
        this.getTabs().clear();
        char currentChar = 'A';
        final List<UUID> allTilesetIds = map.getAllTilesetIds();

        for (UUID tilesetId : allTilesetIds) {
            this.getTabs().add(new TilesetTab(String.valueOf(currentChar), tilesetId, this.editor));
            currentChar++;
        }

        this.setSide(Side.RIGHT);
    }

}
