package com.tcg.rpgengine.editor.components.assetmanagertabs;

import com.badlogic.gdx.files.FileHandle;
import com.tcg.rpgengine.common.data.AssetLibrary;
import com.tcg.rpgengine.common.data.assets.TiledImageAsset;
import com.tcg.rpgengine.editor.components.SimpleAssetListView;
import com.tcg.rpgengine.editor.context.ApplicationContext;
import com.tcg.rpgengine.editor.context.CurrentProject;
import com.tcg.rpgengine.editor.dialogs.ErrorDialog;
import com.tcg.rpgengine.editor.dialogs.SpritesheetDialog;
import com.tcg.rpgengine.editor.dialogs.SpritesheetPreviewDialog;
import com.tcg.rpgengine.editor.utils.AssetUtils;
import com.tcg.rpgengine.editor.utils.ExtensionUtils;
import javafx.beans.binding.BooleanBinding;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Window;
import javafx.util.Pair;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class SpritesheetPagesTab extends TiledImageTab {

    public SpritesheetPagesTab(Window owner) {
        super(owner, "Spritesheet Pages");
    }

    @Override
    protected void removeFromAssetLibrary(TiledImageAsset tiledImageAsset) {
        ApplicationContext.context().currentProject.assetLibrary.deleteSpritesheetPageAsset(tiledImageAsset);
    }

    @Override
    protected void showPreviewDialog(Window owner, TiledImageAsset tiledImageAsset) {
        final SpritesheetPreviewDialog spritesheetPreviewDialog = new SpritesheetPreviewDialog(tiledImageAsset);
        spritesheetPreviewDialog.initOwner(owner);
        spritesheetPreviewDialog.initModality(Modality.APPLICATION_MODAL);
        spritesheetPreviewDialog.showAndWait();
    }

    @Override
    protected Optional<Pair<Integer, Integer>> inputTiledImageValues(Window owner, FileHandle selectedFileHandle) {
        final SpritesheetDialog spritesheetDialog = new SpritesheetDialog(selectedFileHandle);
        spritesheetDialog.initOwner(owner);
        return spritesheetDialog.showAndWait();
    }

    @Override
    protected void addToAssetLibrary(TiledImageAsset tiledImage) {
        ApplicationContext.context().currentProject.assetLibrary.addSpritesheetPageAsset(tiledImage);
    }

    @Override
    protected List<TiledImageAsset> getTiledImageAssets() {
        return ApplicationContext.context().currentProject.assetLibrary.getAllSpritesheetPages();
    }
}
