package com.tcg.rpgengine.editor.components.assetmanagertabs;

import com.badlogic.gdx.files.FileHandle;
import com.tcg.rpgengine.common.data.assets.TiledImageAsset;
import com.tcg.rpgengine.editor.context.ApplicationContext;
import com.tcg.rpgengine.editor.dialogs.TiledImageDialog;
import com.tcg.rpgengine.editor.dialogs.TiledImagePreviewDialog;
import javafx.stage.Modality;
import javafx.stage.Window;
import javafx.util.Pair;

import java.util.List;
import java.util.Optional;

public class TilesetsTab extends TiledImageTab {


    public TilesetsTab(Window owner) {
        super(owner, "Tilesets");
    }

    @Override
    protected void removeFromAssetLibrary(TiledImageAsset tiledImageAsset) {
        ApplicationContext.context().currentProject.assetLibrary.deleteTilesetAsset(tiledImageAsset);
    }

    @Override
    protected void showPreviewDialog(Window owner, TiledImageAsset tiledImageAsset) {
        final TiledImagePreviewDialog tiledImagePreviewDialog = new TiledImagePreviewDialog(tiledImageAsset);
        tiledImagePreviewDialog.initOwner(owner);
        tiledImagePreviewDialog.initModality(Modality.APPLICATION_MODAL);
        tiledImagePreviewDialog.showAndWait();
    }

    @Override
    protected Optional<Pair<Integer, Integer>> inputTiledImageValues(Window owner, FileHandle selectedFileHandle) {
        final TiledImageDialog tiledImageDialog = new TiledImageDialog(selectedFileHandle);
        tiledImageDialog.setTitle("Import Tileset");
        tiledImageDialog.initOwner(owner);
        return tiledImageDialog.showAndWait();
    }

    @Override
    protected void addToAssetLibrary(TiledImageAsset tiledImage) {
        ApplicationContext.context().currentProject.assetLibrary.addTilesetAsset(tiledImage);
    }

    @Override
    protected List<TiledImageAsset> getTiledImageAssets() {
        return ApplicationContext.context().currentProject.assetLibrary.getAllTilesets();
    }
}
