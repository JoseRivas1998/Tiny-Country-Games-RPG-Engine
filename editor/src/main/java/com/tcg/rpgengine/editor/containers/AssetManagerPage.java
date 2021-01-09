package com.tcg.rpgengine.editor.containers;

import com.tcg.rpgengine.editor.components.assetmanagertabs.MusicTab;
import javafx.scene.control.TabPane;

public class AssetManagerPage extends TabPane {

    public AssetManagerPage() {
        super();
        this.getTabs().addAll(
                new MusicTab()
        );
    }

}
