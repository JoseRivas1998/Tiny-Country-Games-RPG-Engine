package com.tcg.rpgengine.editor.components;

import com.tcg.rpgengine.editor.components.canvasses.TilesetEditorCanvas;
import com.tcg.rpgengine.editor.utils.MapEditor;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;

import java.util.UUID;

public class TilesetTab extends Tab {

    private final TilesetEditorCanvas tilesetEditorCanvas;

    public TilesetTab(String title, UUID tilesetId, MapEditor editor) {
        super(title);
        this.setClosable(false);

        final ScrollPane scrollPane = new ScrollPane();

        this.tilesetEditorCanvas = new TilesetEditorCanvas(tilesetId, editor);
        scrollPane.setContent(this.tilesetEditorCanvas);

        this.setContent(scrollPane);

    }

    public void resetSelection() {
        this.tilesetEditorCanvas.resetSelection();
    }

}
