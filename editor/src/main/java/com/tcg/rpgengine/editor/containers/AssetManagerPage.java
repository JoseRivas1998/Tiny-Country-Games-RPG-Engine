package com.tcg.rpgengine.editor.containers;

import com.tcg.rpgengine.editor.components.assetmanagertabs.*;
import com.tcg.rpgengine.editor.context.ApplicationContext;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import javafx.stage.Window;

public class AssetManagerPage extends TabPane {

    public AssetManagerPage(Window stage) {
        super();
        this.getTabs().addAll(
                new MusicTab(stage),
                new SoundTab(stage),
                new ImageTab(stage),
                new SpritesheetPagesTab(stage),
                new TilesetsTab(stage)
        );
        this.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            ApplicationContext.context().jukebox.stopAll();
        });
    }

}
