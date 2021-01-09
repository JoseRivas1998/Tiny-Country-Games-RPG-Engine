package com.tcg.rpgengine.editor.containers;

import com.tcg.rpgengine.editor.components.assetmanagertabs.MusicTab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

public class AssetManagerPage extends TabPane {

    public AssetManagerPage(Stage stage) {
        super();
        this.getTabs().addAll(
                new MusicTab(stage)
        );
    }

}
