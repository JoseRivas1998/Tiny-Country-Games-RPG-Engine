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

public class IconPagesTab extends TiledImageTab {
    public IconPagesTab(Window owner) {
        super(owner, "Icon Pages");
    }

    @Override
    protected void removeFromAssetLibrary(TiledImageAsset iconPageAsset) {
        ApplicationContext.context().currentProject.assetLibrary.deleteIconPageAsset(iconPageAsset);
    }

    @Override
    protected void showPreviewDialog(Window owner, TiledImageAsset iconPageAsset) {
        final TiledImagePreviewDialog tiledImagePreviewDialog = new TiledImagePreviewDialog(iconPageAsset);
        tiledImagePreviewDialog.initOwner(owner);
        tiledImagePreviewDialog.initModality(Modality.APPLICATION_MODAL);
        tiledImagePreviewDialog.showAndWait();
    }

    @Override
    protected Optional<Pair<Integer, Integer>> inputTiledImageValues(Window owner, FileHandle selectedFileHandle) {
        final TiledImageDialog tiledImageDialog = new TiledImageDialog(selectedFileHandle);
        tiledImageDialog.initOwner(owner);
        return tiledImageDialog.showAndWait();
    }

    @Override
    protected void addToAssetLibrary(TiledImageAsset iconPageAsset) {
        ApplicationContext.context().currentProject.assetLibrary.addIconPageAsset(iconPageAsset);
    }

    @Override
    protected List<TiledImageAsset> getTiledImageAssets() {
        return ApplicationContext.context().currentProject.assetLibrary.getAllIconPages();
    }
}
